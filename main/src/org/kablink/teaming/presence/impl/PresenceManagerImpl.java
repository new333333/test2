/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.presence.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.presence.PresenceInfo;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


public class PresenceManagerImpl implements PresenceManager, PresenceManagerImplMBean, InitializingBean, DisposableBean {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected String jabberServer;
	protected String jabberServerPort;
	protected boolean enabled = false;
	protected PresenceListener pl;
	protected HashMap presenceMap = new HashMap();
	
	public void PresenceServiceImpl() {
	}
	
	public void setJabberServer(String jabberServer) {
		this.jabberServer = jabberServer;
	}
	public String getJabberServer() {
		return jabberServer;
	}
	
	public String getJabberServerPort() {
		return jabberServerPort;
	}
	
	public void setJabberServerPort(String jabberServerPort) {
		this.jabberServerPort = jabberServerPort;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
	
	public void afterPropertiesSet() throws Exception {
		if(isEnabled()) {
			// Using jabberServer info, establish a socket connection to the 
			// Jabber server or any other initialization that you have to do
			// at the system startup time. 
			pl = new PresenceListener();
			pl.setDaemon(true);
			pl.start();
		}
		logger.debug("Presence engine is " + (isEnabled()?"enabled":"disabled") + ".");
	}

	public void destroy() throws Exception {
		
		// Close the socket connection that you established in afterPropertiesSet.
		// Do any other cleanup stuff as necessary. 
	}

	public int getPresenceInfo(String zonName) {
		if (!enabled) 
			return PresenceInfo.STATUS_UNKNOWN;
		if (presenceMap.containsKey(zonName)) {
			return ((Integer)presenceMap.get(zonName)).intValue();
		} else {
			return PresenceInfo.STATUS_UNKNOWN;
		}
	}

	public String getModuleName() {
		return "Kablink Conferencing";
	}

	public PresenceInfo getPresenceInfo(String zonNameAsking, String zonName) {
		int zonPresence = getPresenceInfo(zonName);
		return new PresenceInfoImpl(zonPresence);
	}

	public String getIMProtocolString(String zonName) {
		return "iic://im?screenName=" + zonName;
	}

	private class PresenceInfoImpl implements PresenceInfo {
		private int presence = PresenceInfo.STATUS_UNKNOWN;
		public PresenceInfoImpl(int zonPresence) {
			mapAndSetPresence(zonPresence);
		}
		public int getStatus() {
			return presence;
		}
		public String getStatusText() {
			return "";
		}
		private void mapAndSetPresence(int zonPresence) {
			if (zonPresence > 0) {
				if ((zonPresence & 16) == 16) {
					presence = PresenceInfo.STATUS_AWAY;
				} else {
					presence = PresenceInfo.STATUS_AVAILABLE;
				} 
			} else if (zonPresence == 0) {
				presence = PresenceInfo.STATUS_OFFLINE;
			} else {
				presence = PresenceInfo.STATUS_UNKNOWN;
			}
		}
	}

	private class PresenceListener extends Thread {

	    private boolean stop = false;
	    private boolean reset = false;
	    private int SLEEPINTERVAL = 2000;
        private Socket presenceSocket = null;
        private int connectAttempts = 0;
        //private DataInputStream dis;
        private BufferedReader sis;	//socket inputstream

	    /**
	     * Constructor - Set up the time interval, and keep a pointer
	     * to the indexobject that this thread will be checking on.
	     */
	    public PresenceListener( ) {
	    }

	    private Socket openPresenceSocket(String host, String portString) {
	    	Socket presenceSocket;
	    	int port = Integer.parseInt(portString);
	    	try {
 	            presenceSocket = new Socket(host, port);
	        } catch (Exception e) {
	        	if (connectAttempts%60 == 0) {
	        		logger.error("Can't connect to presence server: host ["+ host + "], port [" + port + "], error: " + e.getMessage());
	        	}
	        	connectAttempts++;
         	    return null;
         	}
	        return presenceSocket;
	    }	
	    /**
	     * Main body of this watchdog timer.  The basic operation is to sit in
	     * a timer loop, checking for updates each time the timer runs down.
	     */
	    public void run() {
	    	String line = null;
	    	String user;
	    	Integer status;
	    	int failCount = 0;
	    	int maxFailCount = 30; //one minute
	    	boolean dataFound = false;
	    	while (true) {
		    	//set up the socket listener
	    		if ((presenceSocket == null) || (presenceSocket.isClosed())) {
	    			presenceSocket = openPresenceSocket(getJabberServer(),getJabberServerPort());
	    			if (presenceSocket == null) {
	    				try {
		    				Thread.sleep(SLEEPINTERVAL * failCount);
		    				if ( failCount<maxFailCount) {
		    					failCount++;
		    					if (failCount == 5) {
		    						Iterator itr = presenceMap.keySet().iterator();
		    						while (itr.hasNext())
		    							presenceMap.put(itr.next(),new Integer(0));
		    					}
		    				}
		    				continue;
	    				} catch (InterruptedException ie) {
	    	                if (stop)
	    	                    return;
	    	            }
	    			} else {
	    				failCount = 0;
	    			}
    		        try {
    			       sis = new BufferedReader(new InputStreamReader(presenceSocket.getInputStream()));
    			    }
    			    catch (IOException e) {
    			    	logger.error("Error occures by reading presence info.", e);
    			        presenceSocket = null;
    			        continue;
    			    }
	    		}
		        
		        try {
		        	dataFound = false;
		        	while ((line = sis.readLine()) != null) {
		        		logger.debug("Incoming presence data [" + line + "].");
	                    String parts[] = line.split(" ");
	                    String addr[] = parts[0].split("@");
	                    user = addr[0];
	                    status = new Integer(parts[1]);
	                    presenceMap.remove(user);
	                    presenceMap.put(user,status);
	                    dataFound = true;
	                }
		        	if (!dataFound) {
		        		presenceSocket.close();
		        		presenceSocket = null;
		        	}
		        }catch (Exception e ){
		        	logger.error("Error occures by parsing presence data [" + line + "].", e);
		        	presenceSocket = null;
		        } 
	    	
		        }	    
		 }

	    /**
	     * Called when the process wants this thread to stop.
	     */
	    public void setStop() {
	        stop = true;
	    }

	    /**
	     * Called when another thread issues a commit(). Sets a boolean
	     * so that this thread doesn't bother trying to commit() during
	     * the next wake-up.
	     */
	    public void resetTimer() {
	        reset = true;
	    }
	}
}
