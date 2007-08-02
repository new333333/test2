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
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import javax.portlet.PortletURL;


/**
 * @author Dave
 *
 */
public class SidebarPanelTag extends BodyTagSupport {
	private String _bodyContent;
	private String title = "";
	private String id = "";
	private String divClass = "";
	private Boolean sticky = false;
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
			_params.put("id", new String[] {this.id});
			_params.put("divClass", new String[] {this.divClass});
			_params.put("initOpen", new String[] {this.initOpen.toString()});
			_params.put("sticky", new String[] {this.sticky.toString()});

						
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
			this.id = "";
			this.divClass = "";
			this.sticky = false;
			this.initOpen = false;
		}
	}

	public void setTitle(String title) {
	    this.title = title;
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

}


