package com.sitescape.ef.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.util.NLT;


/**
 * @author Peter Hurley
 *
 */
public class Nlt extends TagSupport {
    private String tag;
    private String text;
    private Boolean checkIfTag;
    
	public int doStartTag() throws JspException {
		if (this.checkIfTag == null) this.checkIfTag = false;
		try {
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			if (this.checkIfTag) {
				//This is a request to see if the tag itself is text or a tag
				sb.append(NLT.getDef(this.tag));
			} else if (this.text == null) {
				sb.append(NLT.get(this.tag));
			} else {
				sb.append(NLT.get(this.tag, this.text));
			}
			jspOut.print(sb.toString());
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
	    
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
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

}


