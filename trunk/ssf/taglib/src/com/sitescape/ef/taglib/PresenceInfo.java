package com.sitescape.ef.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.shared.PresenceServiceUtils;


/**
 * @author Roy Klein
 *
 */
public class PresenceInfo extends TagSupport {
    private User user;
    private int userStatus=-1;
    private String contextPath;
    private String imagePath;
    
	public int doStartTag() throws JspException {
		try {
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			
			if (this.user != null) {
				userStatus = PresenceServiceUtils.getPresence(user);
				//sb.append(NLT.get(this.tag));
			}
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

			this.contextPath = req.getContextPath();
			if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
		    imagePath = contextPath + "/images";
		    
		    switch (userStatus) {
				case -1: 
					sb.append("<img border=\"0\" src=\"" + imagePath + "/pics/sym_s_white_dude.gif\">");
					break;
				case 1:
					sb.append("<img border=\"0\" src=\"" + imagePath + "/pics/sym_s_green_dude.gif\">");
					break;
				case -99:
					break;
				default:
					sb.append("<img border=\"0\" src=\"" + imagePath + "/pics/sym_s_yellow_dude.gif\">");
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
	
	public void setUser(User user) {
	    this.user = user;
	}

}


