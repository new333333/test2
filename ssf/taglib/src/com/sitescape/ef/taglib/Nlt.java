package com.sitescape.ef.taglib;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;


/**
 * @author Peter Hurley
 *
 */
public class Nlt extends BodyTagSupport implements ParamAncestorTag {
    private String tag;
    private String text;
    private Boolean checkIfTag;
	private List _values;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		if (this.checkIfTag == null) this.checkIfTag = false;
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
			jspOut.print(sb.toString());
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		finally {
			_values = null;
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

	public void addParam(String name, String value) {
		if (_values == null) {
			_values = new ArrayList();
		}
		if (name.equals(WebKeys.NLT_VALUE)) _values.add(value);
	}

}


