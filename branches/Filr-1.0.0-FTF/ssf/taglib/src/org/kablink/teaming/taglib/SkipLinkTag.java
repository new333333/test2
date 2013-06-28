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
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author Hemanth Chokkanathan
 *
 */
public class SkipLinkTag extends BodyTagSupport {
	private String _bodyContent;
	private String tag = "";
	private String id = "";
	private Boolean anchorOnly = false;
	private Boolean linkOnly = false;
    
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
			if (user != null) {
				if (user.getDisplayStyle() != null && 
						user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					isAccessible = Boolean.TRUE;
				}
			}
			if (user != null && ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				//Always make the guest accessible so accessible readers can log in
				isAccessible = true;
			}
			
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			httpReq.setAttribute(WebKeys.SKIP_LINK_ID, id);
			httpReq.setAttribute(WebKeys.SKIP_LINK_TAG, tag);
			httpReq.setAttribute(WebKeys.SKIP_LINK_ANCHOR_ONLY, this.anchorOnly);
			httpReq.setAttribute(WebKeys.SKIP_LINK_LINK_ONLY, this.linkOnly);
			
			//Output the top section skip link
			if (!this.anchorOnly) {
				String jsp = "/WEB-INF/jsp/tag_jsps/skiplink/top.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}
			
			 //Body
			if (this._bodyContent != null) pageContext.getOut().print(_bodyContent);
			
			//Output the bottom section skip link
			if (!this.linkOnly) {
				String jsp = "/WEB-INF/jsp/tag_jsps/skiplink/bottom.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}
			
			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			this.tag = "";
			this.id = "";
			this.anchorOnly = false;
			this.linkOnly = false;
		}
	}

	public void setTag(String tag) {
	    this.tag = tag;
	}

	public void setId(String id) {
	    this.id = id;
	}	
	
	public void setAnchorOnly(Boolean value) {
	    this.anchorOnly = value;
	}	
	
	public void setLinkOnly(Boolean value) {
	    this.linkOnly = value;
	}	
	
}