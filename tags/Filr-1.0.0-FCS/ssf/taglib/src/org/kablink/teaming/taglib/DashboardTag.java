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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Dashboard;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;

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