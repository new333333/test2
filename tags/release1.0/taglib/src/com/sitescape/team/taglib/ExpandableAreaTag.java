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

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import javax.portlet.PortletURL;


/**
 * @author Peter Hurley
 *
 */
public class ExpandableAreaTag extends BodyTagSupport {
	private String _bodyContent;
	private String title = "";
	private String titleClass = "ss_bold";
	private String action = "";
	private Boolean initOpen = false;
    
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
			
			//Output the start of the area
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/expandable_area/top.jsp");

			Map _params = new HashMap();
			_params.put("title", new String[] {this.title});
			_params.put("titleClass", new String[] {this.titleClass});
			_params.put("openAction", new String[] {this.action});
			_params.put("initOpen", new String[] {this.initOpen.toString()});

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			pageContext.getOut().print(_bodyContent);

			//Output the end of the area
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/expandable_area/bottom.jsp");
			req = new DynamicServletRequest(httpReq, _params);
			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			this.title = "";
			this.titleClass = "ss_bold";
			this.action = "";
			this.initOpen = false;
		}
	}

	public void setTitle(String title) {
	    this.title = title;
	}

	public void setTitleClass(String titleClass) {
	    this.titleClass = titleClass;
	}

	public void setAction(String action) {
	    this.action = action;
	}

	public void setInitOpen(Boolean initOpen) {
	    this.initOpen = initOpen;
	}

}


