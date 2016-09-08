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
package org.kablink.teaming.taglib;

import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.BinderModuleImpl;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Displays user thumbnail photo.
 * 
 * 
 * @author Brian Kim
 * 
 */

public class IfAccessAllowedTag extends TagSupport {
	
	private Binder binder = null;
	private DefinableEntity entity = null;
	
	private BinderModule binderModule = null;
	private FolderModule folderModule = null;
	
	private String operation = null;
	
	private BinderModule.BinderOperation binderOperation = null;
	private FolderModule.FolderOperation folderOperation = null;

	public int doStartTag() throws JspTagException {
		try {
			if(binder == null && entity == null){
				return SKIP_BODY;
			}	
			if(binderOperation == null){
				return SKIP_BODY;
			}			
			binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
			folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
			
			if(binder != null && binderModule.testAccess(binder, binderOperation)){
				return EVAL_BODY_INCLUDE;
			}
			if(entity != null && entity instanceof FolderEntry && folderModule.testAccess((FolderEntry)entity, folderOperation)){
				return EVAL_BODY_INCLUDE;
			}
		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			this.binder = null;
			this.entity = null;
			this.binderModule = null;
			this.folderModule = null;
			this.operation = null;
			this.binderOperation = null;
		}
		
		return SKIP_BODY;
	}
	
	public void setBinder(Binder binder) {
		this.binder = binder;
	}
	
	public void setEntity(DefinableEntity entity) {
		this.entity = entity;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
		binderOperation = BinderModule.BinderOperation.valueOf(operation);
		folderOperation = FolderModule.FolderOperation.valueOf(operation);
	}
}
