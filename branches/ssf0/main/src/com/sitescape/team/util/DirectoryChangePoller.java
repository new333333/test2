/**
 * 
 */
package com.sitescape.team.util;

import java.io.File;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * 
 * Determines whether a directory has been modified since the last check and
 * generates event notifications for each file changed since the last check.
 * Similarly to {@link FileChangePoller}, The default configuration for this
 * object will always report a change event for the first call to
 * {@link #check()} unless {@link #setLastModified(Date)} is set appropriately.
 * 
 * @see FileChangePoller
 * @author dml
 * 
 */
public class DirectoryChangePoller extends FileChangePoller {
	
	private IOFileFilter fileFilter = FileFilterUtils.trueFileFilter();
	private IOFileFilter dirFilter = FileFilterUtils.falseFileFilter();
	
	/* (non-Javadoc)
	 * @see com.sitescape.team.util.SimpleEventSource#propagate(java.lang.Object)
	 */
	@Override
	protected void propagate(File event) {
		@SuppressWarnings("unchecked")
		Collection<File> modifieds = FileUtils.listFiles(event, FileFilterUtils.andFileFilter(FileFilterUtils.ageFileFilter(lastModified
						.getMillis(), false), fileFilter), dirFilter);
		for (File modified : modifieds) {
			super.propagate(modified);
		}
	}

	public void setFileFilter(IOFileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void setDirFilter(IOFileFilter dirFilter) {
		this.dirFilter = dirFilter;
	}

}
