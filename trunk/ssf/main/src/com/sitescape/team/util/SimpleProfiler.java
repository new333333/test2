/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

/**
 * This class provides very simple profiling functionality.
 * The object is not re-usable and not thread-safe.
 *  
 * @author Jong Kim
 *
 */
public class SimpleProfiler {
	
	private static final ThreadLocal<SimpleProfiler> TL = new ThreadLocal();
	
	private String title = null;
	private boolean active = true; // defaults to true 
    private Map<String,Event> events;
    private long beginTime; // time in ms
    
    public SimpleProfiler() {
    	events = new HashMap<String,Event>();
    	beginTime = System.currentTimeMillis();
    }
    public SimpleProfiler(String title) {
    	this();
    	this.title = title;
    }
    public SimpleProfiler(boolean active) {
    	this();
    	this.active = active;
    }
    public SimpleProfiler(String title, boolean active) {
    	this(title);
    	this.active = active;
    }
    
    public SimpleProfiler start(String eventName) {
    	if(active){
    		//System.out.println("*** " + new java.util.Date().toString() + ": start: " + ((title != null)? (title + "/") : "") + eventName);			
    		
    		Event event = events.get(eventName);
    		if(event == null) {
    			event = new Event();
    			events.put(eventName, event);
    		}
    		event.start();
    	}
    	return this;
    }
    
    public SimpleProfiler stop(String eventName) {
    	if(active) {
    		//System.out.println("*** " + new java.util.Date().toString() + ": stop : " + ((title != null)? (title + "/") : "") + eventName);			
    		
    		Event event = events.get(eventName);
    		if(event != null)
    			event.stop();
    	}
        return this;
    }

    public String toString() {
    	if(active) {
    		StringBuilder sb = new StringBuilder();
    		long currTime = System.currentTimeMillis();
    		if(title != null)
    			sb.append(title).append(": "); 
    		sb.append("Elapsed time (sec) = ")
    		.append((double) (currTime - beginTime)/1000.0);
    		for(Map.Entry entry : events.entrySet()) {
    			if(sb.length() > 0)
    				sb.append(Constants.NEWLINE);
    			sb.append(entry.getKey())
    			.append(": ")
    			.append(entry.getValue().toString());
    		}
    		return sb.toString();
    	}
    	else {
    		return "";
    	}

    }
    public void logDebug(Log logger) {
    	if(active) {
    		if(logger.isDebugEnabled())
    			logger.debug(toString());
    	}
    }
    public void logInfo(Log logger) {
    	if(active) {
    		if(logger.isInfoEnabled())
    			logger.info(toString());
    	}
    }
    public void print() {
    	if(active) {
    		System.out.println("*** " + toString());
    	}
    }
    
    public static void setProfiler(SimpleProfiler profiler) {
    	TL.set(profiler);
    }
    public static SimpleProfiler getProfiler() {
    	return (SimpleProfiler) TL.get();
    }
    
    public static void startProfiler(String eventName) {
    	if(getProfiler() != null)
    		getProfiler().start(eventName);
    }
    
    public static void stopProfiler(String eventName) {
    	if(getProfiler() != null)
    		getProfiler().stop(eventName);
    }
    
    public static String toStr() {
    	if(getProfiler() != null)
    		return getProfiler().toString();
    	else
    		return null;
    }
    
    public static void printProfiler() {
    	if(getProfiler() != null)
    		getProfiler().print();
    }
    
    public static void clearProfiler() {
    	TL.set(null);
    }
    
    public class Event {
        private long t1; // nano time
        private long t2; // nano time
        private long total;
        private int count;

        public void start() {
        	t1 = System.nanoTime();
        }
        public void stop() {
	        t2 = System.nanoTime();
	        total += (t2-t1);
	        count++;
        }
        public long totalNSTime() {
        	return total;
        }
        public double averageNSTime() {
	    	if(count > 0)
	    		return ((double)total) / count;
	    	else
	    		return 0;
        }
        public double totalMSTime() {
        	return totalNSTime() / 1000000.0;
        }
        public double averageMSTime() {
        	return averageNSTime() / 1000000.0;
        }
        public double totalSTime() {
        	return totalMSTime() / 1000.0;
        }
        public double averageSTime() {
        	return averageMSTime() / 1000.0;
        }
        public String toString() {
        	return new StringBuilder().append("count = ").append(count).append(" total = ").append(totalMSTime()).append(" (ms) average = ").append(averageMSTime()).append(" (ms)").toString();
        }

    }
}
