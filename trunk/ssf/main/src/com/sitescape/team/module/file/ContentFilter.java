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
package com.sitescape.team.module.file;

import java.io.InputStream;

import com.sitescape.team.UncheckedIOException;

public interface ContentFilter {

	/**
	 * Applies filtering on the file content.
	 * 
	 * @param fileName name of the input file
	 * @param content content of the input file
	 * @throws FilterException Thrown to indicate that the file failed to pass the filtering
	 * @throws UncheckedIOException Thrown to indicate that there is an I/O exception
	 */
	public void filter(String fileName, InputStream content) throws FilterException, UncheckedIOException;
	
}
