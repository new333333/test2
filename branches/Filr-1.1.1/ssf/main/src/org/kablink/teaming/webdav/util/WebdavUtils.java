/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.webdav.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;

import com.bradmcevoy.common.ContentTypeUtils;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.http11.PartialGetHelper;
import com.bradmcevoy.io.ReadingException;
import com.bradmcevoy.io.WritingException;

/**
 * @author jong
 *
 */
public class WebdavUtils {

	public static void sendFileContent(OutputStream out, Range range, FileAttachment fa, Log logger) 
	throws IOException {
		DefinableEntity owningEntity = fa.getOwner().getEntity();
		InputStream in = getFileModule().readFile(owningEntity.getParentBinder(), owningEntity, fa);

		try {
			if (range != null) {
				if(logger.isDebugEnabled())
					logger.debug("sendContent: ranged content for file " + toString(fa));
				PartialGetHelper.writeRange(in, range, out);
			} else {
				if(logger.isDebugEnabled())
					logger.debug("sendContent: send whole file " + toString(fa));
				IOUtils.copy(in, out);				
			}
			out.flush();
		} catch(ReadingException e) {
			throw new IOException(e);
		} catch(WritingException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	public static String getFileContentType(String accepts, String fileName, Log logger) {
		//return new MimetypesFileTypeMap().getContentType(name);
		
		String mime = ContentTypeUtils.findContentTypes(fileName);
		String s = ContentTypeUtils.findAcceptableContentType(mime, accepts);
		
		if(logger.isTraceEnabled())
			logger.trace("getContentType: preferred: " + accepts + " mime: " + mime + " selected: " + s);
		return s;
	}
	
	public static boolean userCanAccessMyFiles(AllModulesInjected bs) {
		return SearchUtils.userCanAccessMyFiles(bs, RequestContextHolder.getRequestContext().getUser());
	}
	
	public static boolean userCanAccessNetFolders() {
		User user = RequestContextHolder.getRequestContext().getUser();
		// We do not support Net Folders for the guest or external users.
		return !user.isShared() && user.getIdentityInfo().isInternal();
	}
	
	private static FileModule getFileModule() {
		return (FileModule) SpringContextUtil.getBean("fileModule");
	}
	
	private static String toString(FileAttachment fa) {
    	return new StringBuilder().append("[").append(fa.getFileItem().getName()).append(":").append(fa.getId()).append("]").toString(); 
	}
}
