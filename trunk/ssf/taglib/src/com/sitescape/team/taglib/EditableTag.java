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
import java.util.List;
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
	private Map entityMap = null;
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
			
			if (entity != null || entityMap != null) {
				if (this.aclMap == null) this.aclMap = new HashMap();
				
				String id = "";
				String entityType = null;
				String parentBinderId = "";
				if (entity != null) {
					id = entity.getId().toString();
					entityType = entity.getEntityType().toString();
					if (EntityIdentifier.EntityType.folderEntry.toString().equals(entityType))
							parentBinderId = entity.getParentBinder().getId().toString();
				} else if (entityMap != null) {
					id = (String) entityMap.get("_docId");
					entityType = (String) entityMap.get("_entityType");
					if (EntityIdentifier.EntityType.folderEntry.toString().equals(entityType))
							parentBinderId = (String) entityMap.get("_binderId");
				}
				
				AdaptedPortletURL editUrl = new AdaptedPortletURL(httpReq, "ss_forum", true);
				if (EntityIdentifier.EntityType.workspace.toString().equals(entityType)) {
					editUrl.setParameter(WebKeys.URL_BINDER_ID, id);
					editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				} else if (EntityIdentifier.EntityType.folder.toString().equals(entityType)) {
					editUrl.setParameter(WebKeys.URL_BINDER_ID, id);
					editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				} else if (EntityIdentifier.EntityType.folderEntry.toString().equals(entityType)) {
					editUrl.setParameter(WebKeys.URL_BINDER_ID, parentBinderId);
					editUrl.setParameter(WebKeys.URL_ENTRY_ID, id);
					editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				} else if (EntityIdentifier.EntityType.profiles.toString().equals(entityType)) {
					editUrl.setParameter(WebKeys.URL_BINDER_ID, id);
					editUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				}
				editUrl.setParameter(WebKeys.URL_ELEMENT_TO_EDIT, this.element);
				editUrl.setParameter(WebKeys.URL_SECTION_TO_EDIT, WebKeys.URL_SECTION_PLACEHOLDER);
				
				// Top
				String jsp = "/WEB-INF/jsp/tag_jsps/editable/top.jsp";
				
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
	
				ServletRequest req = null;
				req = new DynamicServletRequest(httpReq);
				req.setAttribute("entity", this.entity);			
				req.setAttribute("entityMap", this.entityMap);			
				req.setAttribute("entityId", Long.valueOf(id));			
				req.setAttribute("element", this.element);
				req.setAttribute("aclMap", this.aclMap);
				req.setAttribute("editUrl", editUrl);
				
				StringServletResponse res = new StringServletResponse(httpRes);
	
				rd.include(req, res);
	
				pageContext.getOut().print(res.getString());
	
				// Body
				
				//Break the body into its sections
				List<Map> bodyParts = WebHelper.markupSplitBySection(_bodyContent);
				if (bodyParts.size() > 1) {
					jsp = "/WEB-INF/jsp/tag_jsps/editable/sections.jsp";
					rd = httpReq.getRequestDispatcher(jsp);
					req.setAttribute("parts", bodyParts);			
					res = new StringServletResponse(httpRes);
					rd.include(req, res);
					pageContext.getOut().print(res.getString());
				} else {
					//There is just one section
					pageContext.getOut().print(_bodyContent);
				}
	
				// Bottom
	
				jsp = "/WEB-INF/jsp/tag_jsps/editable/bottom.jsp";
				rd = httpReq.getRequestDispatcher(jsp);
				res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			}
			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			this.entity = null;
			this.entityMap = null;
			this.element = "";
			this.aclMap = null;
		}
	}
	public void setEntity(DefinableEntity entity) {
	    this.entity = entity;
	}

	public void setEntityMap(Map entityMap) {
	    this.entityMap = entityMap;
	}

	public void setElement(String element) {
	    this.element = element;
	}
	
	public void setAclMap(Map aclMap) {
		this.aclMap = aclMap;
	}
}


