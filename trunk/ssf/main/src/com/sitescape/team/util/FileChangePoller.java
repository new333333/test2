/**
 * 
 */
package com.sitescape.team.util;

import java.io.File;
import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author dml
 * 
 * A class which determines whether a {@link File} has been modified since the
 * last check and generates an event if that is the case. The default
 * configuration for this object will always report a change event for the first
 * call to {@link #check()} unless {@link #setLastModified(Date)} is set
 * appropriately.
 * 
 */
public class FileChangePoller extends SimpleEventSource<FileChangePoller, File> {
	private static final Logger log = LoggerFactory.getLogger(FileChangePoller.class);
	
	protected File target;
	protected DateTime lastModified = new DateTime(0L);

	/**
	 * Check if the {@link File} under inspection has been modified since the
	 * last known modification and generate event notifications if this is the
	 * case.
	 */
	public synchronized void check() {
		log.debug("Checking {} for changes.", target.getPath());
		
		if (isModified()) {
			
			log.debug("Change detected for {}", target.getPath());
			
			propagate(target);
			setLastModified(target.lastModified());
		}
	}
	
	/**
	 * Returns whether the target {@link File} has been modified since the last
	 * known modification.
	 * 
	 * @return whether the target {@link File} has been modified since the last
	 *         known modification.
	 */
	protected boolean isModified() {
		return lastModified.isBefore(target.lastModified());
	}

	@Required
	public void setTarget(File target) {
		this.target = target;
	}

	public synchronized void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public synchronized void setLastModified(Date lastModified) {
		this.lastModified = new DateTime(lastModified);
	}

	public synchronized void setLastModified(long lastModified) {
		this.lastModified = new DateTime(lastModified);
	}

}
