/**
 * 
 */
package com.sitescape.util;

import java.io.File;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import com.sitescape.team.util.SimpleEventSource;

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

	private File target;
	private DateTime lastModified = new DateTime(0L);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitescape.team.util.SimpleEventSource#myself()
	 */
	@Override
	protected FileChangePoller myself() {
		return this;
	}

	/**
	 * Check if the {@link File} under inspection has been modified since the
	 * last known modification and generate event notifications if this is the
	 * case.
	 */
	public synchronized void check() {
		if (lastModified.isBefore(target.lastModified())) {
			setLastModified(target.lastModified());
			propagate(target);
		}
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
