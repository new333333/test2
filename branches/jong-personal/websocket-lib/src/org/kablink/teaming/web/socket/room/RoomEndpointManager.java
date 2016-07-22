/**
 * 
 */
package org.kablink.teaming.web.socket.room;

/**
 * @author jong
 *
 */
public interface RoomEndpointManager {

	/**
	 * Add room endpoint.
	 * 
	 * @param roomEndpoint Room endpoint to add.
	 */
	public void addRoomEndpoint(RoomEndpoint roomEndpoint);
	
	/**
	 * Remove room endpoint.
	 * 
	 * @param roomEndpoint Room endpoint to remove.
	 */
	public void removeRoomEndpoint(RoomEndpoint roomEndpoint);
	
	/**
	 * Broadcast the message to all clients connected to the room.
	 * 
	 * @param roomId ID of the room
	 * @param message Message to send
	 */
	public void broadcast(Long roomId, String message);	
}
