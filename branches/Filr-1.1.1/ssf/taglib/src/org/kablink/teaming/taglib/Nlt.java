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

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;



/**
 * @author Peter Hurley
 *
 */
public class Nlt extends BodyTagSupport implements ParamAncestorTag {
    private String tag;
    private String text;
    private Boolean checkIfTag;
    private Boolean quoteDoubleQuote = false;
    private Boolean quoteSingleQuote = false;
	private List _values;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		if (this.checkIfTag == null) this.checkIfTag = false;
		if (this.quoteDoubleQuote == null) this.quoteDoubleQuote = false;
		if (this.quoteSingleQuote == null) this.quoteSingleQuote = false;
		try {
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			if (_values == null) {
				_values = new ArrayList();
			}
			if (this.checkIfTag) {
				//This is a request to see if the tag itself is text or a tag
				sb.append(NLT.getDef(this.tag));
			} else if (this.text == null) {
				sb.append(NLT.get(this.tag, this._values.toArray()));
			} else {
				sb.append(NLT.get(this.tag, this._values.toArray(), this.text));
			}
			String result = sb.toString();
			if (quoteDoubleQuote) result = result.replaceAll("\"", "\\\\\"");
			if (quoteSingleQuote) result = result.replaceAll("\'", "\\\\\'");
			jspOut.print(result);
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_values = null;
			checkIfTag = null;
			text = null;
			quoteDoubleQuote = false;
			quoteSingleQuote = false;
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

	public void setQuoteDoubleQuote(Boolean value) {
	    this.quoteDoubleQuote = value;
	}

	public void setSingleDoubleQuote(Boolean value) {
	    this.quoteSingleQuote = value;
	}

	public void addParam(String name, String value) {
		if (_values == null) {
			_values = new ArrayList();
		}
		if (name.equals(WebKeys.NLT_VALUE)) _values.add(value);
	}

}


