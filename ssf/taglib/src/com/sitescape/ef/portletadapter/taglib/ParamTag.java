package com.sitescape.ef.portletadapter.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class ParamTag extends TagSupport {

	public int doStartTag() throws JspTagException {
		ParamAncestorTag paramAncestor =
			(ParamAncestorTag)findAncestorWithClass(
				this, ParamAncestorTag.class);

		if (paramAncestor == null) {
			throw new JspTagException();
		}

		paramAncestor.addParam(_name, _value);

		return EVAL_BODY_INCLUDE;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setValue(String value) {
		_value = value;
	}

	private String _name;
	private String _value;

}