package com.sitescape.ef.presence.impl;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.ef.presence.PresenceService;

public class PresenceServiceImpl implements PresenceService, InitializingBean, DisposableBean {

	protected String jabberServer;
	
	public void setJabberServer(String jabberServer) {
		this.jabberServer = jabberServer;
	}
	protected String getJabberServer() {
		return jabberServer;
	}
	
	public void afterPropertiesSet() throws Exception {
		// Using jabberServer info, establishe a socket connection to the 
		// Jabber server or any other initialization that you have to do
		// at the system startup time. 
	}

	public void destroy() throws Exception {
		// Close the socket connection that you established in afterPropertiesSet.
		// Do any other cleanup stuff as necessary. 
	}

	public void getPresenceInfoOrWhatever() {
		// TODO Auto-generated method stub
		// Do whatever the caller asks.
	}
	

}
