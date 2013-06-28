/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.taglib;

import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.dom4j.Document;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Displays user thumbnail photo.
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class BuddyPhotoTag extends BodyTagSupport {

	private Principal user = null;
	
	private String folderId = null;
	
	private String entryId = null;
	
	private String style = "";
	
	private Boolean scaled = false;

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
						
			httpReq.setAttribute("thumbnail", null);
			this.user = Utils.fixProxy(user);
			Set photos = null;
			CustomAttribute ca = this.user.getCustomAttribute("picture");
			if (ca != null) photos = ca.getValueSet();
			if (photos != null) {
				httpReq.setAttribute("thumbnail", photos.iterator().next());
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
			httpReq.setAttribute("isScaled", this.scaled);

			// Output the presence info
			String jsp = "/WEB-INF/jsp/tag_jsps/business_card/thumbnail.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			this.user = null;
			this.style = null;
			this.folderId = null;
			this.entryId = null;
			this.scaled = false;
		}

		return EVAL_PAGE;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public void setUser(Principal user) {
		this.user = user;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setScaled(Boolean scaled) {
		this.scaled = scaled;
	}

}
