package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * Network Settings
 * 
 * The host name or IP address of the server must be specified here. The default, localhost, is only appropriate for test configurations
 * with no remote access.
 * 
 * If you have a dedicated server, setting the port to "80" and/or securePort to "443" avoids having to specify a port number in browser
 * URLs.
 * 
 * NOTE: On Linux/UNIX based systems, it is very dangerous to run this server on any port number less than 1024, since that would require
 * the web server to run as the root user. If you must run on a port lower than 1024, please see the Kablink documentation referring to port
 * forwarding via iptables.
 * 
 * If you use some sort of port forwarding or proxying (e.g., iptables or iChain), use the listenPort and secureListenPort settings to
 * specify the port numbers to use on the SERVER. The port and securePort settings should refer to what external users use to access the
 * system. (If not specified, the listen ports use the corresponding port/securePort setttings.)
 * 
 * A default certificate is supplied for SSL connections. You should replace this with your certificate. This can be placed in the
 * conf/.keystore file or change the keystoreFile attribute to point to the certificate.
 * 
 * 
 * OES2 servers will need to reassign the ajpPort and shutdownPorts as they will collide with other software. ajpPort="8010" and
 * shutdownPort="8011" seem to work well. See the Install Guide Network Planning section for more details on port conflicts.
 * 
 **/
public class Network implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private String host;
	private int port;
	private int listenPort;
	private int securePort;
	private int secureListenPort;
	private int shutdownPort;
	private int ajpPort;
	private String keystoreFile;

	private WebService webService;
	private int sessionTimeoutMinutes;
	private boolean portRedirect;
	private boolean forceSecure;
	private boolean listenPortEnabled;
	private int listenPortDisabled;
	private int portDisabled;

	public Network()
	{
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getListenPort()
	{
		return listenPort;
	}

	public void setListenPort(int listenPort)
	{
		this.listenPort = listenPort;
	}

	public int getSecurePort()
	{
		return securePort;
	}

	public void setSecurePort(int securePort)
	{
		this.securePort = securePort;
	}

	public int getSecureListenPort()
	{
		return secureListenPort;
	}

	public void setSecureListenPort(int secureListenPort)
	{
		this.secureListenPort = secureListenPort;
	}

	public int getShutdownPort()
	{
		return shutdownPort;
	}

	public void setShutdownPort(int shutdownPort)
	{
		this.shutdownPort = shutdownPort;
	}

	public int getAjpPort()
	{
		return ajpPort;
	}

	public void setAjpPort(int ajpPort)
	{
		this.ajpPort = ajpPort;
	}

	public String getKeystoreFile()
	{
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile)
	{
		this.keystoreFile = keystoreFile;
	}

	public WebService getWebService()
	{
		return webService;
	}

	public void setWebService(WebService webService)
	{
		this.webService = webService;
	}

	public int getSessionTimeoutMinutes()
	{
		return sessionTimeoutMinutes;
	}

	public void setSessionTimeoutMinutes(int sessionTimeoutMinutes)
	{
		this.sessionTimeoutMinutes = sessionTimeoutMinutes;
	}

	public boolean isPortRedirect()
	{
		return portRedirect;
	}

	public void setPortRedirect(boolean portRedirect)
	{
		this.portRedirect = portRedirect;
	}

	public boolean isForceSecure()
	{
		return forceSecure;
	}

	public void setForceSecure(boolean forceSecure)
	{
		this.forceSecure = forceSecure;
	}

	public boolean isListenPortEnabled()
	{
		return listenPortEnabled;
	}

	public void setListenPortEnabled(boolean listenPortEnabled)
	{
		this.listenPortEnabled = listenPortEnabled;
	}

	public int getListenPortDisabled()
	{
		return listenPortDisabled;
	}

	public void setListenPortDisabled(int listenPortDisabled)
	{
		this.listenPortDisabled = listenPortDisabled;
	}

	public int getPortDisabled()
	{
		return portDisabled;
	}

	public void setPortDisabled(int portDisabled)
	{
		this.portDisabled = portDisabled;
	}
}
