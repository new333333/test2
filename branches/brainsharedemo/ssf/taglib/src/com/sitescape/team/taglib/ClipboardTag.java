package com.sitescape.team.taglib;

import java.util.Map;
import java.util.Set;

import javax.portlet.RenderRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.dom4j.Document;

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * 
 * @author Pawel Nowicki
 */
public class ClipboardTag extends BodyTagSupport {

	private String type;
	
	private String formElement = "";
	
	private Integer instanceCount = 0;
	
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext
					.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext
					.getResponse();

			this.instanceCount++;
			
			RenderRequest renderRequest = (RenderRequest) httpReq.getAttribute("javax.portlet.request");
			Clipboard clipboard = new Clipboard(renderRequest);
			Map clipboardMap = clipboard.getClipboard();
			
			httpReq.setAttribute("type", this.type);			
			httpReq.setAttribute("clipboard_user_count", ((Set) clipboardMap.get(Clipboard.USERS)).size());
			httpReq.setAttribute("instanceCount", this.instanceCount);
			httpReq.setAttribute("formElement", this.formElement);
			
			String jsp = "/WEB-INF/jsp/tag_jsps/clipboard/clipboard.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());

		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		} finally {
			this.type = null;
			this.formElement = "";
		}

		return EVAL_PAGE;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFormElement(String formElement) {
		this.formElement = formElement;
	}

}
