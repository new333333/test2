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
package com.sitescape.team.portletadapter;

import java.util.Iterator;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface MultipartFileSupport {
	
	/**
	 * Return an Iterator of String objects containing the parameter names of the
	 * multipart files contained in this request. These are the field names of
	 * the form (like with normal parameters), not the original file names.
	 * @return the names of the files
	 */
	Iterator getFileNames();

	/**
	 * Return the contents plus description of an uploaded file in this request,
	 * or null if it does not exist.
	 * @param name a String specifying the parameter name of the multipart file
	 * @return the uploaded content in the form of a MultipartFile object
	 */
	MultipartFile getFile(String name);

	/**
	 * Return a Map of the multipart files contained in this request.
	 * @return a map containing the parameter names as keys, and the
	 * MultipartFile objects as values
	 * @see MultipartFile
	 */
	Map getFileMap();

}
