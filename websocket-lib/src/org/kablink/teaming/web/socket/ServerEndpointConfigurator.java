/**
 * 
 */
package org.kablink.teaming.web.socket;

import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContext;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.web.UnauthenticatedAccessException;
import org.kablink.teaming.web.WebKeys;

/**
 * @author jong
 *
 */
public class ServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

	protected static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	/*
	 * The container instantiates a new ServerEndpointConfig for each client-server
	 * connection, so it is safe to associate connection specific information with
	 * the EndpointConfig instance so that open() method can subsequently make use
	 * of that information.
	 */
	@Override
    public void modifyHandshake(ServerEndpointConfig sec,
            HandshakeRequest request, HandshakeResponse response) {
		// Validate that the user has already been properly authenticated 
		// prior to the handshake.
		Principal principal = request.getUserPrincipal();
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		Map<String, List<String>> headers = request.getHeaders();
		String userName = null;
		if(principal != null) {
			userName = principal.getName();
		}
		else if(httpSession != null) {
			userName = (String) httpSession.getAttribute(WebKeys.USER_NAME);
		}
		logger.debug("HTTP headers:" + headers
				+ ", principal:" + principal + ", HTTP session is " + ((httpSession != null)? "present":"absent"));
		if(userName == null || userName.equals(""))
			throw new UnauthenticatedAccessException();	
		// Save user name for later use
		sec.getUserProperties().put("username", userName);
		
		// Once HTTP upgrade handshake is completed, zone context information 
		// can no longer be obtained in normal manner because subsequent websocket
		// communications take place completely outside of HTTP protocol, hence
		// not subject to the ZoneContextValve which is tied specifically to HTTP.
		// Save zone context information for later use.
		ZoneContext origZoneContext = ZoneContextHolder.getZoneContext();
		if(origZoneContext == null)
			throw new InternalException("Zone context isn't set up"); // This should never occur.
		ZoneContext copyZoneContext = new ZoneContext();
		copyZoneContext.setServerName(origZoneContext.getServerName());
		copyZoneContext.setServerPort(origZoneContext.getServerPort());
		copyZoneContext.setSecure(origZoneContext.getSecure());
		copyZoneContext.setClientAddr(origZoneContext.getClientAddr());
		copyZoneContext.setWebappName(origZoneContext.getWebappName());
		//copyZoneContext.setUseRuntimeContext(false); // ? not entirely sure about this right now...
		sec.getUserProperties().put("zonecontext", copyZoneContext);
    }

}
