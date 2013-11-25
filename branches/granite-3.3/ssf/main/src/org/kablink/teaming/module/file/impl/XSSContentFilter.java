/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.file.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.file.ContentFilter;
import org.kablink.teaming.module.file.FilterException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.stringcheck.XSSCheck;
import org.kablink.util.FileUtil;

public class XSSContentFilter implements ContentFilter {
	
	private String[] fileExtensions;
	private XSSCheck xssCheck;
	
	public XSSContentFilter() {
		fileExtensions = SPropsUtil.getStringArray("xss.content.filter.file.extensions", ",");
		for(int i = 0; i < fileExtensions.length; i++)
			fileExtensions[i] = fileExtensions[i].toLowerCase();
		xssCheck = new XSSCheck();
	}
	
	public void filter(Binder binder, DefinableEntity entity, String fileName, InputStream content)
			throws FilterException, IOException {
		if(matchFileExtension(fileName)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			FileUtil.copy(content, out);
			byte[] contentAsBytes = out.toByteArray();
			if(contentAsBytes.length > 0) {
				// Decode the bytes into string using ISO-8859-1 as charset.
				// Obviously, the actual charset of the content may be very different, but for 
				// the purpose of XSS check, we can safely read it in as a stream of 8-bit 
				// characters, since we don't care about losing the actual data as long as
				// we can detect the specific patterns that only involve ASCII characters.
				String contentAsStr = new String(contentAsBytes, "ISO-8859-1");
				try {
					xssCheck.checkFile(contentAsStr);
				}
				catch(Exception e) {
					throw new FilterException(fileName, e.getLocalizedMessage());
				}
			}
		}
	}
	
	private boolean matchFileExtension(String fileName) {
		fileName = fileName.toLowerCase();
		for(String ext:fileExtensions) {
			if(fileName.endsWith(ext))
				return true;
		}
		return false;
	}
}
