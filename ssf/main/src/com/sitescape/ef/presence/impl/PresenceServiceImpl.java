package com.sitescape.ef.presence.impl;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Integer;
import java.util.HashMap;
import com.sitescape.ef.presence.PresenceService;

public class PresenceServiceImpl implements PresenceService, InitializingBean, DisposableBean {

	protected String jabberServer;
	protected String jabberServerPort;
	protected boolean enable = false;
	protected PresenceListener pl;
	protected HashMap presenceMap = new HashMap();
	
	public void PresenceServiceImpl() {
	}
	
	public void setJabberServer(String jabberServer) {
		this.jabberServer = jabberServer;
	}
	protected String getJabberServer() {
		return jabberServer;
	}
	
	public String getJabberServerPort() {
		return jabberServerPort;
	}
	
	public void setJabberServerPort(String jabberServerPort) {
		this.jabberServerPort = jabberServerPort;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public void afterPropertiesSet() throws Exception {
		if(enable) {
			// Using jabberServer info, establish a socket connection to the 
			// Jabber server or any other initialization that you have to do
			// at the system startup time. 
			pl = new PresenceListener();
			pl.start();
		}
	}

	public void destroy() throws Exception {
		
		// Close the socket connection that you established in afterPropertiesSet.
		// Do any other cleanup stuff as necessary. 
	}

	public void getPresenceInfo() {
		// TODO Auto-generated method stub
		// Do whatever the caller asks.
		
	}
	

	private class PresenceListener extends Thread {

	    private boolean stop = false;
	    private boolean reset = false;
	    private int SLEEPINTERVAL = 2000;
        private Socket presenceSocket = null;
        private DataInputStream dis;

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
	        } catch (IOException e) {
         	    System.out.println(e);
         	    return null;
         	}
	        return presenceSocket;
	    }	
	    /**
	     * Main body of this watchdog timer.  The basic operation is to sit in
	     * a timer loop, checking for updates each time the timer runs down.
	     */
	    public void run() {
	    	String line;
	    	String user;
	    	Integer status;
	    	while (true) {
		    	//set up the socket listener
	    		if ((presenceSocket == null) || (presenceSocket.isClosed())) {
	    			presenceSocket = openPresenceSocket(getJabberServer(),getJabberServerPort());
	    			if (presenceSocket == null) {
	    				try {
		    				Thread.sleep(SLEEPINTERVAL);
		    				continue;
	    				} catch (InterruptedException ie) {
	    	                if (stop)
	    	                    return;
	    	            }
	    			}
    		        try {
    			       dis = new DataInputStream(presenceSocket.getInputStream());
    			    }
    			    catch (IOException e) {
    			        presenceSocket = null;
    			        continue;
    			    }
	    		}
		        
		        try {
	               while ((line = dis.readLine()) != null) {
	                    System.out.println("User: " + line);
	                    String parts[] = line.split(" ");
	                    String addr[] = parts[0].split("@");
	                    user = addr[0];
	                    status = new Integer(parts[1]);
	                    presenceMap.remove(user);
	                    presenceMap.put(user,status);
	                }
		        }catch (Exception e ){
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
