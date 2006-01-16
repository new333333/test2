package com.sitescape.ef.taglib;

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
    private User user;
    private int userStatus=-1;
    
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

			if (this.user != null) {
				userStatus = PresenceServiceUtils.getPresence(user);
			}
			if (userStatus != -99) {
				String dudeGif = "sym_s_white_dude.gif"; 
				String altText = NLT.get("presence.none");
				if (userStatus > 0) {
					if ((userStatus & 16) == 16) {
						dudeGif = "sym_s_yellow_dude.gif";
						altText = NLT.get("presence.away");
					} else {
						dudeGif = "sym_s_green_dude.gif";
						altText = NLT.get("presence.online");
					}
				} else if (userStatus == 0) {
					dudeGif = "sym_s_gray_dude.gif";
					altText = NLT.get("presence.offline");
				}
				
				//Pass the user status to the jsp
				httpReq.setAttribute(WebKeys.PRESENCE_USER, user);
				httpReq.setAttribute(WebKeys.PRESENCE_STATUS, new Integer(userStatus));
				httpReq.setAttribute(WebKeys.PRESENCE_DUDE, dudeGif);
				httpReq.setAttribute(WebKeys.PRESENCE_TEXT, altText);
				httpReq.setAttribute(WebKeys.PRESENCE_ZON_BRIDGE, "enabled");
	
				// Output the presence info
				String jsp = "/WEB-INF/jsp/tag_jsps/presence/show_dude.jsp";
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
				ServletRequest req = pageContext.getRequest();
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			}
		}
	    catch(Exception e) {
			throw new JspTagException(e.getMessage());
	    }
		finally {
			userStatus = -1;
		}
	    
		return EVAL_PAGE;
	}

	public void setUser(User user) {
	    this.user = user;
	}

}


