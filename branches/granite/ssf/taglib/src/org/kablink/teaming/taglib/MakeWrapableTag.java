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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Make text wrapable.
 * 
 * 
 * @author Peter Hurley
 * 
 */
public class MakeWrapableTag extends BodyTagSupport {

	protected static final Log logger = LogFactory.getLog(MakeWrapableTag.class);
	
	private String _bodyContent;
	private static final String specChar = "^&[^&;]+;.*$";
	private Pattern pattern_specChar = Pattern.compile( specChar, Pattern.DOTALL + Pattern.MULTILINE );
	private static final String nonAlphaChar = "[!\"#$%&'()*+,-./:;<=>\\s?@\\[\\]^_`{|}~]+";
	private Pattern pattern_nonAlphaChar = Pattern.compile( nonAlphaChar );

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			// Transform the body
			String translatedString = "";
			int cCount = 0;
			for (int i = 0; i < _bodyContent.length(); i++) {
				String remainder = _bodyContent.substring(i, _bodyContent.length());
				Matcher m = pattern_specChar.matcher(remainder);
				if (m.find()) {
					//Skip special quoted characters
					for (int j = 0; j < remainder.indexOf(";")+1; j++) {
						translatedString += remainder.charAt(j);
					}
					translatedString += "<wbr/>";
					i = i + remainder.indexOf(";");
					cCount++;
					continue;
				}
				String c = String.valueOf(_bodyContent.charAt(i));
				cCount++;
				m = pattern_nonAlphaChar.matcher(c);
				if (m.find() || cCount > 15) {
					translatedString += c + "<wbr/>";
					cCount = 0;
				} else {
					translatedString += c;
				}
			}
			pageContext.getOut().print(translatedString);

			return EVAL_PAGE;
		} 
		catch(Exception e) {
	        throw new JspTagException(e); 
	    } 
		finally {
		}
	}

}
