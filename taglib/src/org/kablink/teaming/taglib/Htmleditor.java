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
/*
 * Created on Apr 7, 2005
 *
 *	Stuff for the HTML editor tag
 * 
 */
package org.kablink.teaming.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author billmers
 *;
 */


public class Htmleditor extends BodyTagSupport {
	private static String TYPE_STANDARD = "standard";
	private static String TYPE_MIMIMAL = "minimal";
	private static String TYPE_MIRRORED_FILE = "mirrored_file";
	private String id;
	private String name;
	private String toolbar;
	private String initText;
	private String height = "";
	private String color = "";
	private String _bodyContent;
        
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		BodyContent bc = getBodyContent();
		_bodyContent = "";
		if (bc != null) _bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
		    User user = RequestContextHolder.getRequestContext().getUser();
		    String languageCode = user.getLocale().toString();
		    if (languageCode == null || languageCode.equals("")) languageCode = "en";
		    if (languageCode.startsWith("en")) languageCode = "en";
	
		    if (height == null) {
		    	height = "";
		    }
	
		    if (initText == null) {
		    	initText = "";
		    }
		    if (toolbar == null || toolbar.equals("")) toolbar = TYPE_STANDARD;
		    if (!toolbar.equals(TYPE_MIMIMAL) && !toolbar.equals(TYPE_STANDARD) && !toolbar.equals(TYPE_MIRRORED_FILE)) 
		    	throw new JspException("Unknown ssf:htmleditor toolbar. Legal values are 'standard' and 'minimal'.");
	
			//Output the html editor
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/htmlarea/htmlarea.jsp");
	
			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("element_id", this.id);
			req.setAttribute("element_name", this.name);
			req.setAttribute("editor_toolbar", this.toolbar);
			req.setAttribute("init_text", this.initText);
			req.setAttribute("element_height", this.height);
			req.setAttribute("element_color", this.color);
			req.setAttribute("language", languageCode);
			req.setAttribute("body", _bodyContent); //pass body so it can be escaped
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Bottom
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/htmlarea/htmlarea_bottom.jsp");
			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());
	
			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			this.height = "";
			this.color = "";
			this.initText = "";
			this.id = "";
			this.name = "";
			this.toolbar = "";
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setToolbar(String toolbar) {
		this.toolbar = toolbar;
	}

	public void setInitText(String initText) {
		this.initText = initText;
    }

	public void setHeight(String height) {
		this.height = height;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
