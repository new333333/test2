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
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import javax.portlet.PortletURL;


/**
 * @author Peter Hurley
 *
 */
public class EditableTag extends BodyTagSupport {
	private String _bodyContent;
	private DefinableEntity entity = null;
	private String element = "";
	private Map aclMap = null;
    
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
			
			if (this.aclMap == null) this.aclMap = new HashMap();
			
			AdaptedPortletURL editUrl = new AdaptedPortletURL(httpReq, "ss_forum", true);
			if (entity.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
				editUrl.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
				editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			} else if (entity.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
				editUrl.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
				editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			} else if (entity.getEntityType().equals(EntityIdentifier.EntityType.folderEntry)) {
				editUrl.setParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
				editUrl.setParameter(WebKeys.URL_ENTRY_ID, entity.getId().toString());
				editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
			} else if (entity.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
				editUrl.setParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
				editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			}
			editUrl.setParameter(WebKeys.URL_ELEMENT_TO_EDIT, this.element);
			
			// Top
			String jsp = "/WEB-INF/jsp/tag_jsps/editable/top.jsp";
			
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("entity", this.entity);			
			req.setAttribute("element", this.element);
			req.setAttribute("aclMap", this.aclMap);
			req.setAttribute("editUrl", editUrl);
			
			StringServletResponse res = new StringServletResponse(httpRes);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			// Body

			pageContext.getOut().print(_bodyContent);

			// Bottom

			jsp = "/WEB-INF/jsp/tag_jsps/editable/bottom.jsp";

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
			this.entity = null;
			this.element = "";
			this.aclMap = null;
		}
	}
	public void setEntity(DefinableEntity entity) {
	    this.entity = entity;
	}

	public void setElement(String element) {
	    this.element = element;
	}
	
	public void setAclMap(Map aclMap) {
		this.aclMap = aclMap;
	}
}


