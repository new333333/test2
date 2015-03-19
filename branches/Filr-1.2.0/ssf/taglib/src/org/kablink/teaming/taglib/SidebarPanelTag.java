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
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author Dave
 *
 */
public class SidebarPanelTag extends BodyTagSupport {
	private String _bodyContent;
	private String title = "";
	private String titleHTML = "";
	private String titleInfo = "";
	private String id = "";
	private String divClass = "";
	private Boolean sticky = false;
	private Boolean initOpen = false;
	private Boolean noColorChange = false;
    
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
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/sidebar_box/top.jsp");

			if (this.sticky) {
				ProfileModule profileModule = (ProfileModule)SpringContextUtil.getBean("profileModule");			
				UserProperties folderProps = profileModule.getUserProperties(null);
				Boolean showPanel = (Boolean) folderProps.getProperty(ObjectKeys.USER_PROPERTY_SIDEBAR_PANEL_PREFIX + this.id);
				if (showPanel == null) showPanel = this.initOpen;
				this.initOpen = showPanel;
			}
			
			Map _params = new HashMap();
			_params.put("title", new String[] {NLT.get(this.title)});
			_params.put("titleHTML", new String[] {this.titleHTML});
			_params.put("titleInfo", new String[] {this.titleInfo});
			_params.put("id", new String[] {this.id});
			_params.put("divClass", new String[] {this.divClass});
			_params.put("initOpen", new String[] {this.initOpen.toString()});
			_params.put("sticky", new String[] {this.sticky.toString()});
			_params.put("noColorChange", new String[] {this.noColorChange.toString()});

						
			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			if (_bodyContent != null) {
				pageContext.getOut().print(_bodyContent);
			}

			//Output the end of the area
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/sidebar_box/bottom.jsp");
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
			this.titleHTML = "";
			this.titleInfo = "";
			this.id = "";
			this.divClass = "";
			this.sticky = false;
			this.initOpen = false;
			this.noColorChange = false;
		}
	}

	public void setTitle(String title) {
	    this.title = title;
	}

	public void setTitleHTML(String titleHTML) {
	    this.titleHTML = titleHTML;
	}

	public void setTitleInfo(String titleInfo) {
	    this.titleInfo = titleInfo;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public void setDivClass(String names) {
	    this.divClass = names;
	}

	public void setSticky(Boolean sticky) {
	    this.sticky = sticky;
	}


	public void setInitOpen(Boolean initOpen) {
	    this.initOpen = initOpen;
	}

	public void setNoColorChange(Boolean noColorChange) {
	    this.noColorChange = noColorChange;
	}

}


