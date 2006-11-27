package com.sitescape.ef.taglib;

import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.shared.PresenceServiceUtils;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Roy Klein
 *
 */
public class PresenceInfo extends BodyTagSupport {
    private User user = null;
    private String zonName=null;
    private String componentId;
    private int userStatus=-1;
    private Boolean showOptionsInline=false;
    private Boolean showLargeDude=false;
    
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

			if (this.componentId == null) this.componentId = "";
			if (this.showOptionsInline == null) this.showOptionsInline = false;
			if (this.showLargeDude == null) this.showLargeDude = false;
			String gifSize = "s";
			if (this.showLargeDude) gifSize = "m";

			if (zonName != null) {
				userStatus = PresenceServiceUtils.getPresence(zonName);
			} else if (user != null) {
				userStatus = PresenceServiceUtils.getPresence(user);
			} else {
				userStatus = -99;
			}
			if (userStatus != -99) {
				String dudeGif = "sym_"+gifSize+"_white_dude.gif"; 
				String altText = NLT.get("presence.none");
				if (userStatus > 0) {
					if ((userStatus & 16) == 16) {
						dudeGif = "sym_"+gifSize+"_yellow_dude.gif";
						altText = NLT.get("presence.away");
					} else {
						dudeGif = "sym_"+gifSize+"_green_dude.gif";
						altText = NLT.get("presence.online");
					}
				} else if (userStatus == 0) {
					dudeGif = "sym_"+gifSize+"_gray_dude.gif";
					altText = NLT.get("presence.offline");
				}
				
				//Pass the user status to the jsp
				httpReq.setAttribute(WebKeys.PRESENCE_USER, user);
				httpReq.setAttribute(WebKeys.PRESENCE_STATUS, new Integer(userStatus));
				// TODO get date in the user's local time zone
				httpReq.setAttribute(WebKeys.PRESENCE_SWEEP_TIME, new Date());
				httpReq.setAttribute(WebKeys.PRESENCE_DUDE, dudeGif);
				httpReq.setAttribute(WebKeys.PRESENCE_TEXT, altText);
				httpReq.setAttribute(WebKeys.PRESENCE_ZON_BRIDGE, "enabled");
				httpReq.setAttribute(WebKeys.PRESENCE_COMPONENT_ID, this.componentId);
				httpReq.setAttribute(WebKeys.PRESENCE_SHOW_OPTIONS_INLINE, this.showOptionsInline);
	
				// Output the presence info
				String jsp = "/WEB-INF/jsp/tag_jsps/presence/show_dude.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString().trim());
			}
		}
	    catch(Exception e) {
			throw new JspTagException(e.getMessage());
	    }
		finally {
			userStatus = -1;
			componentId = "";
			user = null;
			zonName = null;
		}
	    
		return EVAL_PAGE;
	}

	public void setComponentId(String componentId) {
	    this.componentId = componentId;
	}

	public void setUser(User user) {
	    this.user = user;
	}
	public void setZonName(String zonName) {
	    this.zonName = zonName;
	}
	public void setShowOptionsInline(Boolean showOptionsInline) {
		this.showOptionsInline = showOptionsInline;
	}
	public void setShowLargeDude(Boolean showLargeDude) {
		this.showLargeDude = showLargeDude;
	}

}


