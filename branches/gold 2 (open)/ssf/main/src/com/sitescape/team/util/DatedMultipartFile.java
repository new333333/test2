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
package com.sitescape.team.util;

import java.io.InputStream;
import java.util.Date;


public class DatedMultipartFile extends SimpleMultipartFile implements FileModDateSupport {

	private Date modDate;

	public DatedMultipartFile(String fileName, InputStream content) {
		super(fileName, content);
	}
	
	public DatedMultipartFile(String fileName, InputStream content, 
			Date modificationDate) {
		super(fileName, content);
		this.modDate = modificationDate;
	}

	public Date getModDate() {
		return modDate;
	}

}
