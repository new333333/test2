package com.sitescape.team.taglib;

import java.util.ArrayList;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Html;

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
