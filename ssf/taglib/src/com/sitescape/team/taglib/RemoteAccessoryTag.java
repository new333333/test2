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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.remoteapplication.RemoteApplicationManager;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.util.WebHelper;

public class RemoteAccessoryTag extends BodyTagSupport implements ParamAncestorTag {

	protected static Log logger = LogFactory.getLog(RemoteAccessoryTag.class);

	private static final long serialVersionUID = 1L;
	
	private Long applicationId;
	private Map<String,String> params;

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	
	public int doStartTag() throws JspException {
		params = new HashMap<String,String>();
		return super.doStartTag();
	}
	
	public int doEndTag() throws JspException {
		if(applicationId != null) {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
	
			ServletOutputStream out = null;
			
			try {
				out = httpRes.getOutputStream();
			}
			catch(IOException e) {
				throw new JspTagException(e.toString());
			}
			
			try {
				getRemoteApplicationManager().executeInteractiveAction
				(params, applicationId, WebHelper.getTokenInfoId(httpReq), out);
			}
			catch(Exception e) {
				if(logger.isDebugEnabled()) {
					logger.error(e.getLocalizedMessage(), e);
				}
				else {
					logger.error(e.toString());
				}
				try {
					out.print(e.getLocalizedMessage());
				} catch (IOException e2) {
					throw new JspTagException(e2.getMessage());
				}
			}
		}
		
		return EVAL_PAGE;
	}
	
	protected RemoteApplicationManager getRemoteApplicationManager() {
		return (RemoteApplicationManager) SpringContextUtil.getBean("remoteApplicationManager");
	}

	public void addParam(String name, String value) {
		params.put(name, value);
	}
}
