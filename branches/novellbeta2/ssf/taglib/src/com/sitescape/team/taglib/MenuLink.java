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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Hemanth Chokkanathan
 *
 */
public class MenuLink extends BodyTagSupport implements ParamAncestorTag {
	private String _bodyContent;
	private String action = "";
	private Boolean adapter = Boolean.TRUE;
	private String entryId = "";
	private String binderId = "";
	private String entityType = "";
	private String displayDiv = "false";
	private String menuDivId = "";
	private String seenStyle = ""; 
	private String seenStyleFine = "";
	private String imageId = "";
	private String linkMenuObjIdx = "";
	private String namespace = "";
	private String entryCallbackRoutine = "";
	private String isDashboard = "";
	private String useBinderFunction = "";
	private String dashboardType = "";
	private String isFile = "";
	
	private Map _params;
    
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
			Boolean isAccessible = Boolean.FALSE;
			if (rc != null) user = rc.getUser();
			if (user != null && user.getDisplayStyle() != null) {
				if (user.getDisplayStyle() != null && user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					isAccessible = Boolean.TRUE;
				}
			}
			
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this._params == null) this._params = new HashMap();
			
			RequestDispatcher rd;
			
			if ("false".equalsIgnoreCase(displayDiv)) {
				//Output the start of the area
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menulink/menulink.jsp");
				
				if (_bodyContent != null) _params.put("title", new String[] {_bodyContent});
				_params.put("action", new String[] {this.action});
				
				String strAdapterValue = "true";
				if (adapter != null && adapter.booleanValue() == false) strAdapterValue = "false";
	
				_params.put("adapter", new String[] {strAdapterValue});
				_params.put("entryId", new String[] {this.entryId});
				_params.put("binderId", new String[] {this.binderId});
				_params.put("entityType", new String[] {this.entityType});
				_params.put("seenStyle", new String[] {this.seenStyle});
				_params.put("seenStyleFine", new String[] {this.seenStyleFine});
				_params.put("imageId", new String[] {this.imageId});
				_params.put("menuDivId", new String[] {this.menuDivId});
				_params.put("linkMenuObjIdx", new String[] {this.linkMenuObjIdx});
				_params.put("namespace", new String[] {this.namespace});
				_params.put("entryCallbackRoutine", new String[] {this.entryCallbackRoutine});
				_params.put("isDashboard", new String[] {this.isDashboard});
				_params.put("useBinderFunction", new String[] {this.useBinderFunction});
				_params.put("dashboardType", new String[] {this.dashboardType});
				_params.put("isFile", new String[] {this.isFile});
				_params.put("isAccessible", new String[] {isAccessible.toString()});
				
				ServletRequest req = null;
				req = new DynamicServletRequest(httpReq, _params);
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			} else {
				//Output the start of the area
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menulink/menulinkdiv.jsp");
				
				_params.put("menuDivId", new String[] {this.menuDivId});
				_params.put("linkMenuObjIdx", new String[] {this.linkMenuObjIdx});
				_params.put("namespace", new String[] {this.namespace});
				_params.put("isDashboard", new String[] {this.isDashboard});
				_params.put("useBinderFunction", new String[] {this.useBinderFunction});
				_params.put("dashboardType", new String[] {this.dashboardType});
				_params.put("isFile", new String[] {this.isFile});
				_params.put("isAccessible", new String[] {isAccessible.toString()});
				
				ServletRequest req = null;
				req = new DynamicServletRequest(httpReq, _params);
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			}

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			action = "";
			adapter= Boolean.TRUE;
			entryId = "";
			binderId = "";
			entityType = "";
			displayDiv = "false";
			menuDivId = "";
			seenStyle = "";
			seenStyleFine = "";
			imageId = "";
			linkMenuObjIdx = "";
			namespace = "";
			entryCallbackRoutine = "";
			isDashboard = "";
			useBinderFunction = "";
			dashboardType = "";
			isFile = "";
			if (_params != null) {
				_params.clear();
			}
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
	
	public void setAdapter(Boolean adapter) {
	    this.adapter = adapter;
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
	
	public void setDisplayDiv(String displayDiv) {
	    this.displayDiv = displayDiv;
	}
	
	public void setMenuDivId(String menuDivId) {
	    this.menuDivId = menuDivId;
	}
	
	public void setSeenStyle(String seenStyle) {
	    this.seenStyle = seenStyle;
	}
	
	public void setSeenStyleFine(String seenStyleFine) {
	    this.seenStyleFine = seenStyleFine;
	}
	
	public void setImageId(String imageId) {
	    this.imageId = imageId;
	}
	
	public void setLinkMenuObjIdx(String linkMenuObjIdx) {
	    this.linkMenuObjIdx = linkMenuObjIdx;
	}
	
	public void setNamespace(String namespace) {
	    this.namespace = namespace;
	}
	
	public void setEntryCallbackRoutine(String entryCallbackRoutine) {
	    this.entryCallbackRoutine = entryCallbackRoutine;
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
}
