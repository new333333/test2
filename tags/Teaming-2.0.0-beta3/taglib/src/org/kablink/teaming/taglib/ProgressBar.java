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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.util.servlet.StringServletResponse;


public class ProgressBar extends BodyTagSupport {
	
	private String currentValue;
	
	private String namespace;
	
	private Map valuesMap;
	
	private String entryId;
	
	private Boolean readOnly = false;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			if (this.valuesMap != null && this.currentValue != null) {
				
				Integer currentValueInt = Integer.parseInt(this.currentValue.replace("c", ""));
				List valuesInt = new ArrayList();
				
				Iterator it = this.valuesMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry)it.next();
					String value = (String)entry.getKey();
					valuesInt.add(value.replace("c", ""));
				}
			
				httpReq.setAttribute("currentValueInt", currentValueInt);
				httpReq.setAttribute("valuesInt", valuesInt);
				httpReq.setAttribute("currentValue", currentValue);
				httpReq.setAttribute("valuesMap", valuesMap);		
				httpReq.setAttribute("namespace", this.namespace);
				httpReq.setAttribute("entryId", this.entryId);
				httpReq.setAttribute("readOnly", this.readOnly);
				
			}
			
			String jsp = "/WEB-INF/jsp/tag_jsps/progressbar/progressbar.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			this.currentValue = null;
			this.namespace = null;
			this.valuesMap = null;
			this.entryId = null;
			this.readOnly = false;
		}
		return EVAL_PAGE;		
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public void setValuesMap(Map valuesMap) {
		this.valuesMap = valuesMap;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

}