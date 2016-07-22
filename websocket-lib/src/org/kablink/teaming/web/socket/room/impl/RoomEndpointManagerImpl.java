/**
 * 
 */
package org.kablink.teaming.web.socket.room.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.web.socket.room.RoomEndpoint;
import org.kablink.teaming.web.socket.room.RoomEndpointManager;

/**
 * @author jong
 *
 */
public class RoomEndpointManagerImpl implements RoomEndpointManager {
	
	private static final Map<Long, Set<RoomEndpoint>> connections = new ConcurrentHashMap<>();

	private Log logger = LogFactory.getLog(getClass());

	@Override
	public void addRoomEndpoint(RoomEndpoint roomEndpoint) {
		Long roomId = roomEndpoint.getRoomId();
		Set<RoomEndpoint> roomConnections = connections.get(roomId);
		if(roomConnections == null) {
			// Only then synchronize
			synchronized (connections) {
				roomConnections = connections.get(roomId);
				// Set up an empty set for the room only if it's still not there
				if(roomConnections == null) {
					roomConnections = new CopyOnWriteArraySet<RoomEndpoint>();
					connections.put(roomId, roomConnections);
				}
			}
		}
		roomConnections.add(roomEndpoint);
	}

	@Override
	public void removeRoomEndpoint(RoomEndpoint roomEndpoint) {
		Set<RoomEndpoint> roomConnections = connections.get(roomEndpoint.getRoomId());
		if(roomConnections != null)
			roomConnections.remove(roomEndpoint);
	}

	@Override
	public void broadcast(Long roomId, String message) {
		Set<RoomEndpoint> roomConnections = connections.get(roomId);
		if(roomConnections != null) {
	        for (RoomEndpoint roomConnection : roomConnections) {
	        	try {
	        		roomConnection.sendMessage(message);
	        	}
	        	catch(Exception e) {
	                logger.error("Failed to send message to client on: " + roomConnection, e);
	                roomConnections.remove(roomConnection);
	                roomConnection.close();
	        	}
	        }
		}
	}
}
