package com.sitescape.ef.pipeline;

import com.sitescape.ef.UncheckedIOException;

public interface Conduit {
		
	/**
	 * Returns sink associated with the conduit.
	 * 
	 * @return
	 * @throws IllegalStateException thrown if this method is called after 
	 * <code>getSource</code> is called.
	 */
	public DocSink getSink() throws IllegalStateException;
	
	/**
	 * Returns source associated with the conduit. 
	 * 
	 * @return
	 * @throws IllegalStateException thrown if this method is called before
	 * <code>getSink</code> is called.
	 */
	public DocSource getSource() throws IllegalStateException;

	/**
	 * Closes the conduit. This closes and releases all resources associated
	 * with it including sink and source objects. It also deletes backing
	 * file if exists. 
	 *
	 */
	public void close();
}
