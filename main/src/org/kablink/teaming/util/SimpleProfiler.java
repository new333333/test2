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
package org.kablink.teaming.util;

import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements simple per-thread profiler.
 *
 */
public class SimpleProfiler {
	
	private static Log logger = LogFactory.getLog(SimpleProfiler.class);

	// Per thread profilers
	private static final ConcurrentHashMap<String,SimpleProfiler> profilers = new ConcurrentHashMap<String,SimpleProfiler>();
	
	// We can only enable/disable entire profilers. We can not enable/disable individual profilers. 
	private static boolean enabled = false;
	
	private String name = null;
    private Map<String,Event> events;
    private long beginTime; // time in nanoseconds
    
    private SimpleProfiler(String name) {
    	events = new TreeMap<String,Event>();
    	beginTime = System.nanoTime();
    	this.name = name;
    }
    
    private SimpleProfiler startEvent(String eventName) {
		Event event = events.get(eventName);
		if(event == null) {
			event = new Event();
			events.put(eventName, event);
		}
		event.start();
    	return this;
    }
    
    private SimpleProfiler stopEvent(String eventName) {
		Event event = events.get(eventName);
		if(event != null)
			event.stop();
        return this;
    }

    public String toString() {
		StringBuilder sb = new StringBuilder();
		long currTime = System.nanoTime();
		if(name != null)
			sb.append("Profiler name: ").append(name).append(", ");
		sb.append("Profile time: ")
		.append((double) (currTime - beginTime)/1000000.0)
		.append(" (ms)");
		int i = 1;
		for(Map.Entry entry : events.entrySet()) {
			sb.append(Constants.NEWLINE)
			.append(i++ + ". ")
			.append(entry.getKey())
			.append(": ")
			.append(entry.getValue().toString());
		}
		return sb.toString();
    }
        
    public static boolean isEnabled() {
    	return enabled;
    }
    
    public static void enable() {
    	enabled = true;
    }
    
    public static void disable() {
    	enabled = false;
    }

    public static void start(String eventName) {
    	if(enabled) {
    		String name = Thread.currentThread().getName();
    		SimpleProfiler sp = getProfiler(name);
    		if(sp == null) {
    			sp = new SimpleProfiler(Thread.currentThread().getName());
    			setProfiler(name, sp);
    		}
    		sp.startEvent(eventName);
    	}
    }
    
    public static void stop(String eventName) {
    	if(enabled) {
    		SimpleProfiler sp = getProfiler(Thread.currentThread().getName());
    		if(sp != null)
    			sp.stopEvent(eventName);
    	}
    }
    
    public static void clear() {
    	profilers.clear();
    }
    
    public static void dumpToLog() {
    	dumpToLog(logger);
    }
    
    public static void dumpToLog(Log logger) {
    	if(logger.isInfoEnabled()) {
			try {
				logger.info("Simple Profiler" + Constants.NEWLINE + dumpAsString());
			}
    		catch(Exception ignore) {}
    	}
    }
    
    public static String dumpAsString() {
    	StringBuilder sb = new StringBuilder();
		try {
			int i = 0;
    		for(SimpleProfiler sp:profilers.values()) {
    			if(i > 0)
    				sb.append(Constants.NEWLINE).append(Constants.NEWLINE);
    			sb.append(sp.toString());
    			i++;
    		}
		}
		catch(Exception ignore) {}
    	return sb.toString();
    }
    
    /*
    public static String asString() {
    	if(enabled) {
    		SimpleProfiler sp = getProfiler();
    		if(sp != null)
    			return sp.toString();
    	}
    	return "";
    }
    
    public static void done(Log logger) {
    	if(enabled) {
    		SimpleProfiler sp = getProfiler();
    		if(sp != null)
    			sp.logInfo(logger);
    	}
    	setProfiler(null);
    }
    */
    
    private static void setProfiler(String name, SimpleProfiler profiler) {
    	profilers.put(name, profiler);
    }
    
    private static SimpleProfiler getProfiler(String name) {
    	return profilers.get(name);
    }
    
    public class Event {
        private long t1; // nano time
        private long t2; // nano time
        private long total; // in nano
        private int count;

        public void start() {
        	t1 = System.nanoTime();
        }
        public void stop() {
	        t2 = System.nanoTime();
	        total += (t2-t1);
	        count++;
        }
        public long averageNSTime() {
	    	if(count > 0)
	    		return total / count;
	    	else
	    		return 0;
        }
        public double totalMSTime() {
        	return total / 1000000.0;
        }
        public double averageMSTime() {
        	if(count > 0)
        		return totalMSTime() / count;
        	else
        		return 0;
        }
        public double totalSTime() {
        	return totalMSTime() / 1000.0;
        }
        public double averageSTime() {
        	return averageMSTime() / 1000.0;
        }
        public String toString() {
        	return new StringBuilder().append("count=").append(count).append(", total=").append(totalMSTime()).append(" (ms), average=").append(averageMSTime()).append(" (ms)").toString();
        }
    }
}
