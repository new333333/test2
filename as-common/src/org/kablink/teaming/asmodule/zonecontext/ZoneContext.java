/**
 * 
 */
package org.kablink.teaming.asmodule.zonecontext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jong
 *
 */
public class ZoneContext {

	private String serverName;
	private Integer serverPort;
	private Boolean secure;
	private String clientAddr;
	private String webappName;
	private Boolean useRuntimeContext;
	private HttpServletRequest httpServletRequest;
	// Used by application to set arbitrary properties. The properties can be set any time during the life cycle
    // of the request, and cleared at the very end of the request.
	private Map<Object,Object> properties;
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public Integer getServerPort() {
		return serverPort;
	}
	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}
	public Boolean getSecure() {
		return secure;
	}
	public void setSecure(Boolean secure) {
		this.secure = secure;
	}
	public String getClientAddr() {
		return clientAddr;
	}
	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}
	public String getWebappName() {
		return webappName;
	}
	public void setWebappName(String webappName) {
		this.webappName = webappName;
	}
	public Boolean getUseRuntimeContext() {
		return useRuntimeContext;
	}
	public void setUseRuntimeContext(Boolean useRuntimeContext) {
		this.useRuntimeContext = useRuntimeContext;
	}
    public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}
	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}
	public Map<Object, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<Object, Object> properties) {
		this.properties = properties;
	}
}
