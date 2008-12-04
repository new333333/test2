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
package org.kablink.teaming.taglib;

import java.util.ArrayList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.util.Html;


public class TextFormat extends BodyTagSupport {
	private String _bodyContent;
	private String formatAction;
	private String textMaxWords;
	
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
		
		String[] startMUArray = { "[[", "<", "{{" };
		String[] endMUArray = { "]]", ">", "}}" };
		
		String textContent= _bodyContent;
		if (textContent == null) textContent = "";

		try {
			//Check for the Text Content to be formatted and the Formatting Action to be done. 
			//if(textContent == null) throw new JspException("TextFormat: Text Content Missing");
			
			if (formatAction == null) formatAction = ""; 
			
			if (formatAction.equals("limitedDescription")) {
				int intMaxAllowedWords = 0;

				//Check for the Text Max Words settings and value format
				if (textMaxWords == null) throw new JspException("TextFormat: Text Max Words not specified");
				
				try {
					intMaxAllowedWords = Integer.parseInt(textMaxWords); 
				} catch (NumberFormatException nfe) {
					throw new JspException("TextFormat: Text Max Words not a numeric value");
				}
				
				textContent = textContent.trim();
				
				String strStrippedHTMLContent = Html.restrictiveStripHtml(textContent);
				
				String summary = "";
				String [] words = strStrippedHTMLContent.split(" ");
				
				ArrayList muArrList = new ArrayList();
				
				//Looping through the word list
				//We are excepting the tags and HTML code if any to be well formed.
				for (int i = 0; i < words.length; i++) {
					String strWord = words[i];
					summary = summary + " " + strWord;
					
					String strLCWord = strWord.toLowerCase();
	
					//Hemanth:
					//we are trying to make sure the words do not get truncated half way between tags like
					//[[ ]] - Title Hyper Link Tag and <img /> - Image Tag
					//For this, we add the tags that we encounter in a array list and
					//when we encounter an end tag for that, we remove the tag from the arraylist
					//So when we reach the max words limit, we check to see if the arraylist size is zero,
					//if so, we assume, there is no open tag and display the summary information
					//if not, we assume, we keep going until the array list size becomes zero.
					//when the array list size is zero, we assume that there is no more open tags
					
					//Code for adding information about the open tags in the array list
					for (int j = 0; j < startMUArray.length; j++) {
						String strStartTag = startMUArray[j];
						int intStartIdx = strLCWord.indexOf(strStartTag);
						if (intStartIdx != -1) {
							muArrList.add(strStartTag);
						}
					}
	
					//Code for removing tags from the arraylist, when the closing tag is encountered
					for (int j = 0; j < endMUArray.length; j++) {
						String strEndTag = endMUArray[j];
						int intEndIdx = strLCWord.indexOf(strEndTag);
						if (intEndIdx != -1) {
							String strStartTag = startMUArray[j];
							for (int k = 0; k < muArrList.size(); k++) {
								String strEntry = (String) muArrList.get(k);
								if (strStartTag.equalsIgnoreCase(strEntry)) {
									muArrList.remove(k);
									break;
								}
							}
						}
					}
	
					//Limit the summary to intMaxAllowedWords words
					//we are checking to see if the arraylist size is zero, to ensure that there are no open tags
					if (i >= (intMaxAllowedWords - 1) && (muArrList.size() == 0) ) {
						//If the actual text length is greater than the specified textMaxWords allowed then we
						//will append the "..." to the summary displayed. If not we will not append the "...".
						if (i < words.length - 1) summary = summary + "...";
						break;
					}
				}
				
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(summary);
					
			} else if (formatAction.equals("simpleLimitedDescription")) {
				int intMaxAllowedWords = 0;
				//Check for the Text Max Words settings and value format
				if (textMaxWords == null) throw new JspException("TextFormat: Text Max Words not specified");
				try {
					intMaxAllowedWords = Integer.parseInt(textMaxWords); 
				} catch (NumberFormatException nfe) {
					throw new JspException("TextFormat: Text Max Words not a numeric value");
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
				jspOut.print(summary);
				
			} else {
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(textContent);
			}
			
			return SKIP_BODY;
			
		} catch (Exception e) {
			throw new JspException(e);
		} finally {
			this.formatAction = null;
			this.textMaxWords = null;			
		}
	}

	public void setFormatAction(String formatAction) {
		this.formatAction = formatAction;
	}
	
	public void setTextMaxWords(String textMaxWords) {
		this.textMaxWords = textMaxWords;
	}
}
