/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
