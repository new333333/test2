package com.sitescape.team.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class TextFormat extends TagSupport {
	
	private String textContent;
	private String formatAction;
	private String textMaxWords;
	
	public int doStartTag() throws JspException {

		//Check for the Text Content to be formatted and the Formatting Action to be done. 
		if(textContent == null || formatAction == null)
			throw new JspException("TextFormat: Text Content or Format Action Missing");
		
		if (formatAction.equals("limitedDescription")) {
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
			
			for (int i = 0; i < words.length; i++) {
				summary = summary + " " + words[i];
				//Limit the summary to intMaxAllowedWords words
				if (i >= intMaxAllowedWords) {
					if (i < words.length - 1) summary = summary + "...";
					break;
				}
			}
			
			try {
				JspWriter jspOut = pageContext.getOut();
				jspOut.print(summary);
			}
			catch(Exception e) {
				throw new JspException(e);
			}
		}
		
		return SKIP_BODY;
	}
	
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public void setFormatAction(String formatAction) {
		this.formatAction = formatAction;
	}
	
	public void setTextMaxWords(String textMaxWords) {
		this.textMaxWords = textMaxWords;
	}
}
