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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.groovy.GroovyScriptService;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;

/**
 * @author jong
 *
 */
public abstract class WebdavResource implements Resource, GetableResource {
	
	protected static final String CONTENT_TYPE_TEXT_HTML_UTF8 = "text/html; charset=utf-8";
	
	protected WebdavResourceFactory factory;
	
	protected WebdavResource(WebdavResourceFactory factory) {
		this.factory = factory;
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public Object authenticate(String user, String password) {
		return "";
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#authorise(com.bradmcevoy.http.Request, com.bradmcevoy.http.Request.Method, com.bradmcevoy.http.Auth)
	 */
	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getRealm()
	 */
	@Override
	public String getRealm() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#checkRedirect(com.bradmcevoy.http.Request)
	 */
	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsRoot();
	}

	protected Resource makeResourceFromBinder(Binder binder) {
		if(binder == null)
			return null;
		
		if(binder instanceof Workspace) {
			Workspace w = (Workspace) binder;
			if(w.isDeleted() || w.isPreDeleted())
				return null;
			else 
				return new WorkspaceResource(factory, w);
		}
		else if(binder instanceof Folder) {
			Folder f = (Folder) binder;
			if(f.isDeleted() || f.isPreDeleted())
				return null;
			else 
				return new FolderResource(factory, f);
		}
		else {
			return null;
		}
	}
	
	protected Resource makeResourceFromBinder(BinderIndexData binder) {
		if(binder == null)
			return null;
		
		EntityType entityType = binder.getEntityType();
		if(EntityType.workspace == entityType) {
			return new WorkspaceResource(factory, binder);
		}
		else if(EntityType.folder == entityType) {
			return new FolderResource(factory, binder);
		}
		else {
			return null;
		}
	}
	
	protected Resource makeResourceFromFile(FileAttachment fa) {
		if(fa == null)
			return null;
		else
			return new FileResource(factory, fa);
	}
	
	protected Resource makeResourceFromFile(FileIndexData file) {
		if(file == null)
			return null;
		else
			return new FileResource(factory, file);
	}
	
	protected WorkspaceModule getWorkspaceModule () {
		return (WorkspaceModule) SpringContextUtil.getBean("workspaceModule");
	}

	protected FolderModule getFolderModule () {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}

	protected BinderModule getBinderModule () {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}

	protected FileModule getFileModule () {
		return (FileModule) SpringContextUtil.getBean("fileModule");
	}
	
	protected GroovyScriptService getGroovyScriptService() {
		return (GroovyScriptService) SpringContextUtil.getBean("groovyScriptService");
	}
	
	protected FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}

}
