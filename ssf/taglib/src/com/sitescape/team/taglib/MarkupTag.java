package com.sitescape.team.taglib;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;


/**
 * @author Peter Hurley
 *
 */
public class MarkupTag extends BodyTagSupport {
	private String _bodyContent;
	private DefinableEntity entity = null;
	private String type = WebKeys.MARKUP_VIEW;
	private String binderId = "";
	private String entryId = "";
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			RenderRequest renderRequest = (RenderRequest) httpReq.getAttribute("javax.portlet.request");
			RenderResponse renderResponse = (RenderResponse) httpReq.getAttribute("javax.portlet.response");
			
			// Transform the body
			String translatedString = _bodyContent;
			
			//Transform the markup 
			if (binderId.equals("")) {
				translatedString = WebHelper.markupStringReplacement(renderRequest, renderResponse, 
						httpReq, httpRes, entity, _bodyContent, type);
			} else {
				translatedString = WebHelper.markupStringReplacement(renderRequest, renderResponse, 
						httpReq, httpRes, entity, _bodyContent, type, 
						Long.valueOf(binderId), Long.valueOf(entryId));
			}
			pageContext.getOut().print(translatedString);

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			this.type = WebKeys.MARKUP_VIEW;
			this.entity = null;
			this.binderId = "";
			this.entryId = "";
		}
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setEntity(DefinableEntity entity) {
	    this.entity = entity;
	}

	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setEntryId(String entryId) {
	    this.entryId = entryId;
	}

}


