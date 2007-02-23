package com.sitescape.team.taglib;

import java.util.Set;

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
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * Displays user thumbnail photo.
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class BuddyPhotoTag extends BodyTagSupport {

	private Set photos = null;
	
	private String folderId = null;
	
	private String entryId = null;
	
	private String style = "";

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
						
			if (photos != null) {
				httpReq.setAttribute("thumbnail", this.photos.iterator().next());
			}
//			TODO: depends on user access rights we can create here link to upload user photo			
//			else if (folderId != null) {				
//				BinderModule binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
//				if (binderModule.testAccess(Long.parseLong(folderId), "modifyBinder")) {
//					url = ...
//				}
//			}
			httpReq.setAttribute("style", this.style);
			httpReq.setAttribute("photo_folder", this.folderId);
			httpReq.setAttribute("photo_entry", this.entryId);

			// Output the presence info
			String jsp = "/WEB-INF/jsp/tag_jsps/business_card/thumbnail.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());

		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		} finally {
			this.photos = null;
			this.style = null;
			this.folderId = null;
			this.entryId = null;
		}

		return EVAL_PAGE;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public void setPhotos(Set photos) {
		this.photos = photos;
	}

	public void setStyle(String style) {
		this.style = style;
	}


}
