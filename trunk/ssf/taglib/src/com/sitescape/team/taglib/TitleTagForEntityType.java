/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;


/**
 * @author Hemanth Chokkanathan
 *
 */
public class TitleTagForEntityType extends BodyTagSupport {
    private String entityType;
    private String text;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY; 
	}

	public int doEndTag() throws JspTagException {
		try {
			if (entityType != null && !entityType.equals("")) {
				
				RequestContext rc = RequestContextHolder.getRequestContext();
				User user = null;
				boolean isAccessible = false;
				if (rc != null) user = rc.getUser();
				if (user != null) {
					String displayStyle = user.getDisplayStyle();
					if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
						isAccessible = true;
					}
				}
				
				if (isAccessible) {

					JspWriter jspOut = pageContext.getOut();
					StringBuffer sb = new StringBuffer();

					if (entityType.equalsIgnoreCase("folderEntry")) sb.append(NLT.get("title.open.folderEntry", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("user")) sb.append(NLT.get("title.open.user", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("group")) sb.append(NLT.get("title.open.group", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("folder")) sb.append(NLT.get("title.open.folder", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("workspace")) sb.append(NLT.get("title.open.workspace", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("userWorkspace")) sb.append(NLT.get("title.open.userWorkspace", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("profiles")) sb.append(NLT.get("title.open.profiles", new Object [] {text} ));
					else if (entityType.equalsIgnoreCase("file")) sb.append(NLT.get("title.open.file", new Object [] {text} ));
					
					//if (text != null && !text.equals("")) sb.append(" " + text );
					
					jspOut.print( "TITLE=\"" + sb.toString() + "\" " );
				}
			}
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
			entityType = null;
			text = null;
		}
	    
		return EVAL_PAGE;
	}
	
	public void setEntityType(String entityType) {
	    this.entityType = entityType;
	}

	public void setText(String text) {
	    this.text = text;
	}
}