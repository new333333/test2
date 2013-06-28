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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


import javax.portlet.PortletURL;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author Dave Griffin
 *
 */
public class PopupPaneTag extends BodyTagSupport {
	private String _bodyContent;
    private String titleTag;
    private String closeScript;
    private String width;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			httpReq.setAttribute("titleTag", this.titleTag);
			httpReq.setAttribute("closeScript", this.closeScript);
			httpReq.setAttribute("width", this.width);
			
			// Top
			String jsp = "/WEB-INF/jsp/tag_jsps/popup_pane/top.jsp";
			
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			
			ServletRequest req = pageContext.getRequest();

			StringServletResponse res = new StringServletResponse(httpRes);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			// Body

			pageContext.getOut().print(_bodyContent);

			// Bottom

			jsp = "/WEB-INF/jsp/tag_jsps/popup_pane/bottom.jsp";

			rd = httpReq.getRequestDispatcher(jsp);

			res = new StringServletResponse(httpRes);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
		}
	}
	
	
	public void setTitleTag(String text) {
	    this.titleTag = text;
	}

	public void setCloseScript(String text) {
	    this.closeScript = text;
	}

	public void setWidth(String text) {
	    this.width = text;
	}

}


