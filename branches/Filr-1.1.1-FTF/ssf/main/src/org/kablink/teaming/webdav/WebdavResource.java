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
package org.kablink.teaming.webdav;

import java.util.Date;

import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.groovy.GroovyScriptService;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Request.Method;

/**
 * @author jong
 *
 */
public abstract class WebdavResource extends AbstractAllModulesInjected implements Resource, PropFindableResource {
	
	protected WebdavResourceFactory factory;
	
	// At minimum, each WebDAV resource must have a full path and a name (which is the last element of the path). 
	
	// Full WebDAV path leading to this resource
	private String webdavPath; 
	// Name portion of the URL. This must be consistent with the full path.
	private String name; 
	
	protected WebdavResource(WebdavResourceFactory factory, String webdavPath, String name) {
		this.factory = factory;
		this.webdavPath = webdavPath;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public Object authenticate(String user, String password) {
		return factory.getSecurityManager().authenticate(user, password);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#authorise(com.bradmcevoy.http.Request, com.bradmcevoy.http.Request.Method, com.bradmcevoy.http.Auth)
	 */
	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		return factory.getSecurityManager().authorise(request, method, auth, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getRealm()
	 */
	@Override
	public String getRealm() {
		return factory.getSecurityManager().getRealm(null);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#checkRedirect(com.bradmcevoy.http.Request)
	 */
	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	public java.util.Date getMiltonSafeDate(java.util.Date date) {
		// IMPORTANT: Milton fails to serialize these derived date types in SQL package. 
		// We need plain java.util.Date. Specifically, with java.util.Date value, Milton
		// produces correct GMT string representation for date value (eg. Fri Apr 13 00:00:00 GMT 2012). 
		// However, with java.sql.Date or java.sql.Timestamp, it creates one without time zone 
		// (e.g. 2012-01-12 13:41:00.83) which causes WebDAV client to fail on Windows 7.
		if((date instanceof java.sql.Date) || (date instanceof java.sql.Timestamp))
			return new Date(date.getTime());
		else
			return date;
	}
	
	public String getWebdavPath() {
		return webdavPath;
	}
	
	public void setWebdavPath(String webdavPath) {
		this.webdavPath = webdavPath;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void fixupName(String newName) {
		String oldName = getName();
		int index = getWebdavPath().indexOf(oldName);
		if(index >= 0) {
			setWebdavPath(getWebdavPath().substring(0, index) + newName);
			setName(newName);
		}
		else {
			// This should never happen!?
		}
	}
	
	@Override
	public String toString() {
		return this.webdavPath;
	}
	
	protected GroovyScriptService getGroovyScriptService() {
		return (GroovyScriptService) SpringContextUtil.getBean("groovyScriptService");
	}
	
	protected FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}

	protected CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	public static void main(String[] args) {
		java.util.Date now = new Date();
		System.out.println(now.toString());
	}
}
