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
    
	public int doStartTag() throws JspException {
		try {
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			if (this.text == null) {
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

}


