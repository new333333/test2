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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TextToHtml;
import org.kablink.util.Html;


@SuppressWarnings("serial")
public class TextFormat extends BodyTagSupport {
	private String _bodyContent;
	private String formatAction;
	private String textMaxWords;
	private String textMaxChars;
	private Boolean stripHtml = false;
	private Boolean breakOnLines = false;
	
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspException {
		//textContent will be mandatory
		
		//formatAction will not be mandatory
		//If the formatAction is not specified or happens to be other than
		//the one specified, then we will return the textContent as it is
		
		String textContent= _bodyContent;
		if (textContent == null) textContent = "";

		try {
			//Check for the Text Content to be formatted and the Formatting Action to be done. 
			//if(textContent == null) throw new JspException("TextFormat: Text Content Missing");
			
			if (formatAction == null) formatAction = ""; 
			
			if (formatAction.equals("limitedDescription") && textMaxWords != null) {
				int intMaxAllowedWords = 0;

				//Check for the Text Max Words settings and value format
				if (textMaxWords == null) {
					throw new JspException("TextFormat: Text Max Words or Chars not specified");
				}

				try {
					//See if this is a constant read in from the properties file
					String max = SPropsUtil.getString(textMaxWords, "");
					if (max.equals("")) {
						intMaxAllowedWords = Integer.parseInt(textMaxWords); 
					} else {
						intMaxAllowedWords = Integer.parseInt(max); 
					}
				} catch (NumberFormatException nfe) {
					throw new JspException("TextFormat: Text Max Words not a numeric value");
				}
				
				String summary = Html.wordStripHTML(textContent, intMaxAllowedWords);
				if (stripHtml) {
					//Ok, really strip out the remaining html
					summary = Html.stripHtml(summary);
				}
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(summary.trim());
				
			} else if (formatAction.equals("limitedCharacters")) {
				int intMaxAllowedChars = 0;

				
				try {
					//See if this is a constant read in from the properties file
					String max = SPropsUtil.getString(textMaxChars, "");
					if (max.equals("")) {
						intMaxAllowedChars = Integer.parseInt(textMaxChars); 
					} else {
						intMaxAllowedChars = Integer.parseInt(max); 
					}
				} catch (NumberFormatException nfe) {
					throw new JspException("TextFormat: Text Max Chars not a numeric value");
				}
				
				textContent = textContent.trim();
				
				String summary = textContent;
				if (stripHtml) {
					//Ok, strip out any html
					summary = Html.stripHtml(summary);
				}

				if (intMaxAllowedChars >= 0 && textContent.length() > intMaxAllowedChars) {
					summary = summary.substring(0, intMaxAllowedChars) + "...";
				}
				
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(summary.trim());
					
			} else if (formatAction.equals("simpleLimitedDescription")) {
				int intMaxAllowedWords = 0;
				//Check for the Text Max Words settings and value format
				if (textMaxWords == null) throw new JspException("TextFormat: Text Max Words not specified");
				try {
					intMaxAllowedWords = Integer.parseInt(textMaxWords); 
				} catch (NumberFormatException nfe) {
					throw new JspException("TextFormat: Text Max Words not a numeric value");
				}
	
				if (stripHtml) {
					//Ok, strip out any html
					textContent = Html.stripHtml(textContent);
				}
				String summary = "";
				String [] words = textContent.split(" ");
				
				//Looping through the word list
				for (int i = 0; i < words.length; i++) {
					summary = summary + " " + words[i];
					
					//Limit the summary to intMaxAllowedWords words
					if (i >= (intMaxAllowedWords - 1)) {
						//If the actual text length is greater than the specified textMaxWords allowed then we
						//will append the "..." to the summary displayed. If not we will not append the "...".
						if (i < words.length - 1) summary = summary + "...";
						break;
					}
				}
				
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(summary.trim());
				
			} else if (formatAction.equals("textToHtml")) {
				TextToHtml textToHtml = new TextToHtml();
				textToHtml.setBreakOnLines(this.breakOnLines);
				textToHtml.setStripHtml(this.stripHtml);
				textToHtml.parseText(textContent);
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(textToHtml.toString()); 
				
			} else {
				JspWriter jspOut = pageContext.getOut();
				if (stripHtml) {
					//Ok, strip out any html
					textContent = Html.stripHtml(textContent);
				}
				jspOut.print(textContent);
			}
			
			return SKIP_BODY;
			
		} catch (Exception e) {
			throw new JspException(e);
		} finally {
			this.formatAction = null;
			this.textMaxWords = null;			
			this.textMaxChars = null;			
			this.breakOnLines = false;
			this.stripHtml = false;
		}
	}

	public void setFormatAction(String formatAction) {
		this.formatAction = formatAction;
	}
	
	public void setTextMaxWords(String textMaxWords) {
		this.textMaxWords = textMaxWords;
	}
	
	public void setTextMaxChars(String textMaxChars) {
		this.textMaxChars = textMaxChars;
	}
	
	public void setStripHtml(Boolean stripHtml) {
		this.stripHtml = stripHtml;
	}
	
	public void setBreakOnLines(Boolean breakOnLines) {
		this.breakOnLines = breakOnLines;
	}
}
