/**
 * 
 */
package org.kablink.teaming.web.socket;

/**
 * @author jong
 *
 */
public class WebsocketException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WebsocketException(String message) {
		super(message);
	}
	
	public WebsocketException(Throwable cause) {
		super(cause);
	}
}
