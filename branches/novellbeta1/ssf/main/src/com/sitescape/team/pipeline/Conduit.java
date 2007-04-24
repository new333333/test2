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
package com.sitescape.team.pipeline;

public interface Conduit {
		
	/**
	 * Returns sink associated with the conduit.
	 * If this method is called multiple times, the same <code>DocSink</code>
	 * instance is returned. 
	 * 
	 * @return
	 * @throws IllegalStateException thrown if this method is called after 
	 * <code>getSource</code> is called.
	 */
	public DocSink getSink() throws IllegalStateException;
	
	/**
	 * Returns source associated with the conduit. 
	 * If this method is called multiple times, the same <code>DocSource</code>
	 * instance is returned. 
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
