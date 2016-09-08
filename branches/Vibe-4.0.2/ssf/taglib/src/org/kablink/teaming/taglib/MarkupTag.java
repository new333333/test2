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

import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MarkupUtil;



/**
 * @author Peter Hurley
 *
 */
public class MarkupTag extends BodyTagSupport {
	private String _bodyContent;
	private DefinableEntity entity = null;
	private String type = WebKeys.MARKUP_VIEW;
    private Map searchResult=null;
	private boolean leaveSectionsUnchanged = false;
	private boolean mobile = false;
    
	public void MarkupTag() {
		setup();
	}
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
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
		type = WebKeys.MARKUP_VIEW;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			RenderRequest renderRequest = (RenderRequest) httpReq.getAttribute("javax.portlet.request");
			RenderResponse renderResponse = (RenderResponse) httpReq.getAttribute("javax.portlet.response");
			
			// Transform the body
			String translatedString = _bodyContent;
			
			if (searchResult != null) {
				translatedString = MarkupUtil.markupStringReplacement(renderRequest, renderResponse, 
					httpReq, httpRes, searchResult, _bodyContent, type, mobile);
			} else if (entity != null) {
				translatedString = MarkupUtil.markupStringReplacement(renderRequest, renderResponse, 
						httpReq, httpRes, entity, _bodyContent, type, mobile);
			}
			if (!this.leaveSectionsUnchanged) {
				//Translate the "sections" markup
				translatedString = MarkupUtil.markupSectionsReplacement(translatedString);
			} else {
				translatedString = StringEscapeUtils.unescapeHtml(translatedString);
			}
			pageContext.getOut().print(translatedString);

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			setup();
			this.leaveSectionsUnchanged = false;
			this.mobile = false;
		}
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setEntity(DefinableEntity entity) {
	    this.entity = entity;
	}
	public void setSearch(Map searchResult) {
	    this.searchResult = searchResult;
	}


	public void setLeaveSectionsUnchanged(boolean leaveSectionsUnchanged) {
	    this.leaveSectionsUnchanged = leaveSectionsUnchanged;
	}

	public void setMobile(boolean mobile) {
	    this.mobile = mobile;
	}

}


