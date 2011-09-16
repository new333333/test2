/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

/**
 * @author Peter Hurley
 *
 */
@SuppressWarnings("serial")
public class PermalinkTag extends BodyTagSupport {
    @SuppressWarnings("unchecked")
	private Map searchResult=null;
	private DefinableEntity entity=null;
	private String entityId=null;
	private String entityType=null;
	public PermalinkTag() {
		setup();
	}
	/** 
	 * Initalize params at end of call and creation
	 * 
	 *
	 */
	protected void setup() {
		//need to reinitialize - class must be cached
		searchResult=null;
		entity = null;
		entityId =
		entityType = null;
	}
	
	@SuppressWarnings("unchecked")
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();
			String webUrl = null;
			if (searchResult != null) webUrl = PermaLinkUtil.getPermalink(req, searchResult);
			else if (entity != null) webUrl = PermaLinkUtil.getPermalink(req, entity);
			else {
				HashMap map = new HashMap();
				map.put(Constants.DOCID_FIELD, entityId);
				map.put(Constants.ENTITY_FIELD, entityType);
				webUrl = PermaLinkUtil.getPermalink(req, map);
			}
			if (Validator.isNotNull(webUrl)) pageContext.getOut().print(webUrl);
			return SKIP_BODY;
		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			setup();
		}
	}

	@SuppressWarnings("unchecked")
	public void setSearch(Map searchResult) {
	    this.searchResult = searchResult;
	}
	public void setEntity(DefinableEntity entity) {
		this.entity = entity;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

}


