/**
 * 
 */
package org.kablink.teaming.web.socket.room;

/**
 * @author jong
 *
 */
public interface RoomEndpoint {

	/**
	 * Get the ID of the room associated with this endpoint.
	 * 
	 * @return
	 */
	Long getRoomId();

    /**
     * Send the message to the client associated with this endpoint.
     * 
     * @param msg
     */
	void sendMessage(String message);

	/**
	 * Close the session/connection associated with this endpoint.
	 */
	void close();
}
