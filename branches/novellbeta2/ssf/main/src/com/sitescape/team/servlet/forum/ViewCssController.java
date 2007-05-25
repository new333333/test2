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
package com.sitescape.team.servlet.forum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;

public class ViewCssController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		String theme = RequestUtils.getStringParameter(request, WebKeys.URL_CSS_THEME, "");
		String sheet = RequestUtils.getStringParameter(request, WebKeys.URL_CSS_SHEET, "");
		Map model = new HashMap();
		if (!theme.equals("")) model.put(WebKeys.CSS_THEME, theme);
		response.setContentType("text/css");			

		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
		Date d = new Date(System.currentTimeMillis() - (24*60*60*1000));
		df.applyPattern("E, d MMM yyyy kk:mm:ss z");
		response.setHeader(
				"Last-Modified", df.format(d));
		String viewPath = "common/ssf_css";
		if (sheet.equals("editor")) {
			viewPath = "common/editor_css";
		}
		return new ModelAndView(viewPath, model);
	}
}
