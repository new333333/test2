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
package com.sitescape.team.taglib;

import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.dom4j.Document;

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.impl.BinderModuleImpl;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * Displays user thumbnail photo.
 * 
 * 
 * @author Brian Kim
 * 
 */

public class IfAccessAllowedTag extends BodyTagSupport {
	
	private Binder binder = null;
	
	private BinderModule binderModule = null;
	
	private String operation = null;
	
	private BinderModule.BinderOperation binderOperation = null;

	public int doStartTag() throws JspTagException {
		try {
			if(binder == null){
				return SKIP_BODY;
			}	
			if(binderOperation == null){
				return SKIP_BODY;
			}			
			binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
			
			if(binderModule.testAccess(binder, binderOperation)){
				return EVAL_BODY_INCLUDE;
			}
		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			this.binder = null;
			this.binderModule = null;
			this.operation = null;
			this.binderOperation = null;
		}
		
		return EVAL_PAGE;
	}
	
	public int doAfterBody() {
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		return EVAL_BODY_INCLUDE;
	}

	public void setBinder(Binder binder) {
		this.binder = binder;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
		binderOperation = BinderModule.BinderOperation.valueOf(operation);
	}
}
