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

	private String title;
    private long t1;
    private long t2;
    private long total = 0;
    private int count = 0;

    public SimpleProfiler(String title) {
        this.title = title;
    }
    public void begin() {
        t1 = System.currentTimeMillis();
    }
    public void end() {
        t2 = System.currentTimeMillis();
        total += (t2-t1);
        count++;
    }
    public long totalTime() {
        return total;
    }
    public double averageTime() {
        return ((double)total) / count;
    }
    public String toString() {
        return new StringBuffer(title).append(": count = ").append(count).append(" total = ").append(total).append(" (ms) average = ").append(averageTime()).append(" (ms)").toString(); 
    }
    public void logDebug() {
        if(logger.isDebugEnabled())
            logger.debug(toString());
    }
    public void logInfo() {
        logger.info(toString());
    }
    public void print() {
        System.out.println(toString());
    }
}
