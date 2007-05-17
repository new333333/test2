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

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class DashboardTag extends BodyTagSupport {

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			User user = RequestContextHolder.getRequestContext().getUser();
			
			//Save the starting dashboard so it can be restored later
			savedDashboard = (Map) this._configuration.get(WebKeys.DASHBOARD_MAP);
			
			//Get the data map associated with the component
			String scope = this._id.split("_")[0];
			Map dashboard = null;
			if (scope.equals(DashboardHelper.Local)) {
				dashboard = (Map)this._configuration.get(WebKeys.DASHBOARD_LOCAL_MAP);
			} else if (scope.equals(DashboardHelper.Global)) {
				dashboard = (Map)this._configuration.get(WebKeys.DASHBOARD_GLOBAL_MAP);
			} else if (scope.equals(DashboardHelper.Binder)) {
				dashboard = (Map)this._configuration.get(WebKeys.DASHBOARD_BINDER_MAP);
			} else dashboard = new HashMap(savedDashboard);
			this._configuration.put(WebKeys.DASHBOARD_MAP, dashboard);
			if (dashboard != null) {
				// Get the jsp to run
				Map components = (Map) dashboard.get(Dashboard.COMPONENTS);
				if (components != null) {
					if (components.containsKey(this._id)) {
						Map component = (Map) components.get(this._id);
						String name = (String) component.get(Dashboard.NAME);
						String title = (String) component.get(Dashboard.COMPONENT_TITLE);
						if (title == null) title = "";
						String jsp = "";
						if (_type == null || _type.equals("")) _type = "config";
						if (_type.equals("config")) {
							jsp = SPropsUtil.getString("dashboard.configJsp." + name, "");
						} else if (_type.equals("viewComponent")) {
							jsp = "/WEB-INF/jsp/tag_jsps/dashboard/view_dashboard_canvas_component.jsp";
						} else if (_type.equals("viewData")) {
							if (this._initOnly) {
								jsp = SPropsUtil.getString("dashboard.viewInitJsp." + name, "");
							} else {
								jsp = SPropsUtil.getString("dashboard.viewJsp." + name, "");
							}
						}
						if (_type.equals("title")) {
							//Output just the title
							pageContext.getOut().print(NLT.getDef(title));
						
						} else if (!jsp.equals("")) {
							RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				
							ServletRequest req = new DynamicServletRequest(
								(HttpServletRequest)pageContext.getRequest());
							
							req.setAttribute(WebKeys.DASHBOARD_COMPONENT_ID, this._id);
							
							StringServletResponse res = new StringServletResponse(httpRes);
				
							rd.include(req, res);
				
							pageContext.getOut().print(res.getString());
						}
					}
				}
			}
			this._configuration.put(WebKeys.DASHBOARD_MAP, savedDashboard);
			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_id = "";
			_type = "";
		}
	}

	public void setId(String id) {
		_id = id;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setInitOnly(boolean initOnly) {
		_initOnly = initOnly;
	}

	public void setConfiguration(Map configuration) {
		_configuration = configuration;
	}

	private String _name;
	private String _id;
	private String _type;
	private boolean _initOnly;
	private Map _configuration;
	private Map savedDashboard;
	private String _bodyContent;

}