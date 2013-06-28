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
package org.kablink.teaming.servlet.forum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.domain.User;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;


public class ViewCssController extends SAbstractController {
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		HttpSession ses = request.getSession(false);

		Date cssDate = new Date();
		if(ses != null) {
			cssDate = (Date) ses.getAttribute("ssCssDate");
			if (cssDate == null) {
				cssDate = new Date();
				ses.setAttribute("ssCssDate", cssDate);
			}
		}
		String sheet = ServletRequestUtils.getStringParameter(request, WebKeys.URL_CSS_SHEET, "");
		Map model = new HashMap();
		response.setContentType("text/css");			

		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
		//24*60*60*1000 = 86400000
		Date d = new Date(cssDate.getTime() - Long.valueOf("86400000"));
		df.applyPattern("EEE, dd MMM yyyy kk:mm:ss zzz");
		response.setHeader(
				"Last-Modified", df.format(d));

		d = new Date(cssDate.getTime() + Long.valueOf("86400000"));
		df.applyPattern("EEE, dd MMM yyyy kk:mm:ss zzz");
		response.setHeader(
				"Expires", df.format(d));
		
		String viewPath = "common/ssf_css";
		if (sheet.equals("editor")) {
			viewPath = "common/editor_css";
		}
		return new ModelAndView(viewPath, model);
	}
}
