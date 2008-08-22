/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
					if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) &&
							!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
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