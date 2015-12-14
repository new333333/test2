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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;



/**
 * @author Peter Hurley
 *
 */
public class AltTag extends BodyTagSupport implements ParamAncestorTag {
    private String tag;
    private String text;
    private Boolean checkIfTag;
	private List _values;
	private String attName = "alt";
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		User user = null;
		boolean isAccessible = true;  //This tag now always shows the alt text.
		if (rc != null) user = rc.getUser();
		if (user != null) {
			String displayStyle = user.getDisplayStyle();
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				isAccessible = true;
			}
		}
		if (user != null && ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			//Always make the guest accessible so accessible readers can log in
			isAccessible = true;
		}
		if (this.checkIfTag == null) this.checkIfTag = false;
		try {
			if (isAccessible) {
				JspWriter jspOut = pageContext.getOut();
				StringBuffer sb = new StringBuffer();
				if (_values == null) {
					_values = new ArrayList();
				}
				if (tag != null && this.checkIfTag) {
					//This is a request to see if the tag itself is text or a tag
					sb.append(NLT.getDef(this.tag));
				} else if (tag != null && this.text == null) {
					sb.append(NLT.get(this.tag, this._values.toArray()));
				} else if (tag == null && this.text != null) {
					sb.append(this.text);
				} else {
					if (tag != null) sb.append(NLT.get(this.tag, this._values.toArray(), this.text));
				}
				jspOut.print(attName + "=\"" + sb.toString() + "\" ");
			}
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_values = null;
			checkIfTag = null;
			text = null;
			tag = null;
			attName = "alt";
		}
	    
		return EVAL_PAGE;
	}
	
	public void setTag(String tag) {
	    this.tag = tag;
	}

	public void setText(String text) {
	    this.text = text;
	}

	public void setCheckIfTag(Boolean value) {
	    this.checkIfTag = value;
	}

	public void setAttName(String attName) {
	    this.attName = attName;
	}

	public void addParam(String name, String value) {
		if (_values == null) {
			_values = new ArrayList();
		}
		if (name.equals(WebKeys.NLT_VALUE)) _values.add(value);
	}
}