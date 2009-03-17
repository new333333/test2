/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.kablink.teaming.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ParamTag extends BodyTagSupport {
	private String _name;
	private String _value;
	private Boolean _useBody;
	private String _bodyContent;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		if (getBodyContent() != null) _bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			ParamAncestorTag paramAncestor =
				(ParamAncestorTag)findAncestorWithClass(
					this, ParamAncestorTag.class);
	
			if (paramAncestor == null) {
				throw new JspTagException();
			}
	
			if (_bodyContent == null) _bodyContent = "";
			if (_value == null) _value = "";
			if (_useBody == null) _useBody = Boolean.FALSE;
			if (_useBody.booleanValue()) {
				paramAncestor.addParam(_name, _value.concat(_bodyContent));
			} else {
				paramAncestor.addParam(_name, _value);
			}
	
			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			_value = null;
			_bodyContent = null;
		}
	}

	public void setName(String name) {
		_name = name;
	}

	public void setUseBody(Boolean useBody) {
		_useBody = useBody;
	}

	public void setValue(String value) {
		_value = value;
	}

}