package com.sitescape.ef.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides very simple profiling functionality.
 * The object is not re-usable and not thread-safe.
 *  
 * @author Jong Kim
 *
 */
public class SimpleProfiler {
	private static final Log logger = LogFactory.getLog(SimpleProfiler.class);

	private boolean active = true; // defaults to true 
	private String title;
    private long t1; // nano time
    private long t2; // nano time
    private long total = 0;
    private int count = 0;

    public SimpleProfiler() {}
    public SimpleProfiler(boolean active) {
    	this.active = active;
    }
    public SimpleProfiler(String title) {
    	this.title = title;
    }
    public SimpleProfiler reset(String title) {
    	if(active) {
	    	this.title = title;
	    	this.t1 = 0;
	    	this.t2 = 0;
	    	this.total = 0;
	    	this.count = 0;
    	}
	    return this;
    }
    public void begin() {
    	if(active)
    		t1 = System.nanoTime();
    }
    public SimpleProfiler end() {
    	if(active) {
	        t2 = System.nanoTime();
	        total += (t2-t1);
	        count++;
    	}
        return this;
    }
    public long totalNSTime() {
    	if(active)
    		return total;
    	else
    		return 0;
    }
    public double averageNSTime() {
    	if(active) {
	    	if(count > 0)
	    		return ((double)total) / count;
	    	else
	    		return 0;
    	}
    	else {
    		return 0;
    	}
    }
    public double totalMSTime() {
    	if(active)
    		return totalNSTime() / 1000000.0;
    	else
    		return 0.0;
    }
    public double averageMSTime() {
    	if(active)
    		return averageNSTime() / 1000000.0;
    	else
    		return 0.0;
    }
    public String toString() {
    	if(active)
    		return new StringBuffer(title).append(": count = ").append(count).append(" total = ").append(totalMSTime()).append(" (ms) average = ").append(averageMSTime()).append(" (ms)").toString();
    	else
    		return "";
    }
    public void logDebug() {
    	if(active) {
    		if(logger.isDebugEnabled())
    			logger.debug(toString());
    	}
    }
    public void logInfo() {
    	if(active)
    		logger.info(toString());
    }
    public void print() {
    	if(active)
    		System.out.println("*** " + toString());
    }
}
