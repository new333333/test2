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

import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Peter Hurley
 *
 */
public class InlineHelpTag extends BodyTagSupport {
	private String tag;
	private String jsp;
	private String _bodyContent;
    
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
			
			if (this.tag == null && this.jsp == null) {
				// Top
				String jsp = "/WEB-INF/jsp/tag_jsps/inline_help/top.jsp";				
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);	
				ServletRequest req = pageContext.getRequest();	
				StringServletResponse res = new StringServletResponse(httpRes);	
				rd.include(req, res);	
				pageContext.getOut().print(res.getString());
	
				// Body	
				pageContext.getOut().print(_bodyContent);
	
				// Bottom	
				jsp = "/WEB-INF/jsp/tag_jsps/inline_help/bottom.jsp";	
				rd = httpReq.getRequestDispatcher(jsp);	
				res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			
			} else {
				String jsp = "/WEB-INF/jsp/tag_jsps/inline_help/tag.jsp";				
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);	
				ServletRequest req = pageContext.getRequest();	
				StringServletResponse res = new StringServletResponse(httpRes);	
				req.setAttribute("tag", this.tag);			
				req.setAttribute("jsp", this.jsp);			
				rd.include(req, res);	
				pageContext.getOut().print(res.getString());
			}

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			tag = null;
			jsp = null;
		}
	}

	public void setTag(String tag) {
	    this.tag = tag;
	}

	public void setJsp(String jsp) {
	    this.jsp = jsp;
	}
}


