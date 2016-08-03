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
		Set<RoomEndpoint> roomEndpoints = connections.get(roomId);
		if(roomEndpoints == null) {
			// Only then synchronize
			synchronized (connections) {
				roomEndpoints = connections.get(roomId);
				// Set up an empty set for the room only if it's still not there
				if(roomEndpoints == null) {
					roomEndpoints = new CopyOnWriteArraySet<RoomEndpoint>();
					connections.put(roomId, roomEndpoints);
				}
			}
		}
		roomEndpoints.add(roomEndpoint);
	}

	@Override
	public void removeRoomEndpoint(RoomEndpoint roomEndpoint) {
		Set<RoomEndpoint> roomEndpoints = connections.get(roomEndpoint.getRoomId());
		if(roomEndpoints != null)
			roomEndpoints.remove(roomEndpoint);
	}

	@Override
	public void broadcast(Long roomId, String message) {
		Set<RoomEndpoint> roomEndpoints = connections.get(roomId);
		if(roomEndpoints != null) {
	        for (RoomEndpoint roomEndpoint : roomEndpoints) {
	        	try {
	        		roomEndpoint.sendMessage(message);
	        	}
	        	catch(Exception e) {
	                logger.error("Failed to send message to client on: " + roomEndpoint, e);
	                roomEndpoints.remove(roomEndpoint);
	                roomEndpoint.close();
	        	}
	        }
		}
	}
}
