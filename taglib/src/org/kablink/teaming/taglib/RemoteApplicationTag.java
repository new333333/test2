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
import org.kablink.teaming.remoteapplication.RemoteApplicationManager;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.WebHelper;


public class RemoteApplicationTag extends BodyTagSupport implements ParamAncestorTag {

	protected static Log logger = LogFactory.getLog(RemoteApplicationTag.class);

	private static final long serialVersionUID = 1L;
	
	private String applicationId;
	private Map<String,String> params;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId; 
	}
	
	public int doStartTag() throws JspException {
		params = new HashMap<String,String>();
		return super.doStartTag();
	}
	
	public int doEndTag() throws JspException {
		if(applicationId != null && !applicationId.equals("")) {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			try {
				getRemoteApplicationManager().executeSessionScopedRenderableAction
				(params, new Long(applicationId), httpReq, pageContext.getOut());
			}
			catch(Exception e) {
				if(logger.isDebugEnabled()) {
					logger.error(e.getLocalizedMessage(), e);
				}
				else {
					logger.error(e.toString());
				}
				try {
					String errorMessage = e.getLocalizedMessage();
					if (errorMessage == null) errorMessage = NLT.get("error.remoteApplicationFailure", new String[] {e.toString()});
					pageContext.getOut().print(errorMessage);
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
