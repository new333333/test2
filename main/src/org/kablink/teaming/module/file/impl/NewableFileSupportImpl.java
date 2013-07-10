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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.file.NewableFileSupport;
import org.kablink.util.FileUtil;
import org.springframework.core.io.ClassPathResource;

public class NewableFileSupportImpl implements NewableFileSupport {

	private ConcurrentHashMap<String,byte[]> fileContents = new ConcurrentHashMap<String, byte[]>();
	private Log logger = LogFactory.getLog(getClass());
	
	@Override
	public InputStream getInitialFileContent(String fileExtension) {
		if(!fileExtension.startsWith("."))
			fileExtension = "." + fileExtension;
		
		byte[] initialContent = fileContents.get(fileExtension);
		if(initialContent == null) {
			if(logger.isDebugEnabled())
				logger.debug("File extension '" + fileExtension + "' not found in the cache");
			initialContent = readInitialContent(fileExtension);
			fileContents.put(fileExtension, initialContent);
		}
		else {
			if(logger.isDebugEnabled())
				logger.debug("File extension '" + fileExtension + "' found in the cache");
		}
		
		return new ByteArrayInputStream(initialContent);
	}
	
	private byte[] readInitialContent(String fileExtension) {
		String file = "config/file_templates/initial" + fileExtension;
		InputStream is = null;
		byte[] content;
		try {
			is = new ClassPathResource(file).getInputStream();
			content = FileUtil.getBytes(is);
			logger.info("Initial file content for extension '" + fileExtension + "' is read from " + file);
		} catch (IOException e) {
			content = new byte[0];
			logger.info("Initial file content for extension '" + fileExtension + "' cannot be read from " + file + ". Using zero-length content.");
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
		return content;
	}

}

