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
		
		if(operation.equals("manageTag"))
			this.binderOperation = 
				com.sitescape.team.module.binder.BinderModule.BinderOperation.manageTag;
	}
}
