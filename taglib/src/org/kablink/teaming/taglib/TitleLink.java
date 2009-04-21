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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Hemanth Chokkanathan
 *
 */
public class TitleLink extends BodyTagSupport implements ParamAncestorTag {
	private String _bodyContent;
	private String action = "";
	private String entryId = "";
	private String binderId = "";
	private String entityType = "";
	private String seenStyle = ""; 
	private String seenStyleFine = "";
	private String namespace = "";
	private String isDashboard = "";
	private String useBinderFunction = "";
	private String dashboardType = "";
	private String isFile = "";
	private String hrefClass = "";
	
	private Map _params;

	public TitleLink() {
		setup();
	}
	/** 
	 * Initalize params at end of call and creation
	 * 
	 *
	 */
	protected void setup() {
		_bodyContent = null;
		action = "";
		entryId = "";
		binderId = "";
		entityType = "";
		seenStyle = "";
		seenStyleFine = "";
		namespace = "";
		isDashboard = "";
		useBinderFunction = "";
		dashboardType = "";
		isFile = "";
		if (_params != null) {
			_params.clear();
		}
	}
	
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			RequestContext rc = RequestContextHolder.getRequestContext();
			User user = null;
			boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
			Boolean isAccessible = Boolean.FALSE;
			if (rc != null) user = rc.getUser();
			if (user != null && user.getDisplayStyle() != null) {
				if (user.getDisplayStyle() != null && 
						user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && 
						accessible_simple_ui) {
					isAccessible = Boolean.TRUE;
				}
			}
			
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this._params == null) this._params = new HashMap();
			
			RequestDispatcher rd;
			
				//Output the start of the area
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/titlelink/titlelink.jsp");
				
				if (_bodyContent != null) _params.put("title", new String[] {_bodyContent});
				_params.put("action", new String[] {this.action});
				
				_params.put("entryId", new String[] {this.entryId});
				_params.put("binderId", new String[] {this.binderId});
				_params.put("entityType", new String[] {this.entityType});
				_params.put("seenStyle", new String[] {this.seenStyle});
				_params.put("seenStyleFine", new String[] {this.seenStyleFine});
				_params.put("namespace", new String[] {this.namespace});
				_params.put("isDashboard", new String[] {this.isDashboard});
				_params.put("useBinderFunction", new String[] {this.useBinderFunction});
				_params.put("dashboardType", new String[] {this.dashboardType});
				_params.put("isFile", new String[] {this.isFile});
				_params.put("isAccessible", new String[] {isAccessible.toString()});
				_params.put("hrefClass", new String[] {this.hrefClass});
				
				ServletRequest req = null;
				req = new DynamicServletRequest(httpReq, _params);
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
		return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			setup();
		}
	}

	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new HashMap();
		}

		String[] values = (String[])_params.get(name);

		if (values == null) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		_params.put(name, values);
	}

	public void setAction(String action) {
	    this.action = action;
	}
	
	
	public void setEntryId(String entryId) {
	    this.entryId = entryId;
	}
	
	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setEntityType(String entityType) {
	    this.entityType = entityType;
	}
	
	
	public void setSeenStyle(String seenStyle) {
	    this.seenStyle = seenStyle;
	}
	
	public void setSeenStyleFine(String seenStyleFine) {
	    this.seenStyleFine = seenStyleFine;
	}
	
	
	public void setNamespace(String namespace) {
	    this.namespace = namespace;
	}
	
	public void setIsDashboard(String isDashboard) {
	    this.isDashboard = isDashboard;
	}

	public void setUseBinderFunction(String useBinderFunction) {
	    this.useBinderFunction = useBinderFunction;
	}
	
	public void setDashboardType(String dashboardType) {
	    this.dashboardType = dashboardType;
	}

	public void setIsFile(String isFile) {
	    this.isFile = isFile;
	}

	public void setHrefClass(String hrefClass) {
	    this.hrefClass = hrefClass;
	}
}
