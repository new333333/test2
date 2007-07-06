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
package com.sitescape.team.module.file.impl;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.module.file.ContentFilter;
import com.sitescape.team.module.file.FilterException;

public class DummyContentFilter implements ContentFilter {

	public void filter(String fileName, InputStream content) throws FilterException, UncheckedIOException {
		// This dummy filter does not do anything useful. 
		// A nice real filter to add would be something like a virus scanning filter. 
		
		if(fileName.equals("debug.doc")) {
			throw new FilterException(fileName); // I don't like the file name!
		}
		else {
			// Make a backup copy of the file in my own directory. Sneaky filter...
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:/junk2/" + fileName));
				FileCopyUtils.copy(content, bos);
				bos.close();
			}
			catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

}
