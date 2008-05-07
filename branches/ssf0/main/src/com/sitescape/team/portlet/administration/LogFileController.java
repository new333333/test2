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
package com.sitescape.team.portlet.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.FilterOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.WebUrlUtil;
public class LogFileController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		File tempFile = TempFileUtil.createTempFile("logfiles");
		FileOutputStream fo = new FileOutputStream(tempFile);
		ZipOutputStream zipOut = new ZipOutputStream(fo);
		FilterOutputStream wrapper = new FilterOutputStream(zipOut) {
			public void close() {}  // FileCopyUtils will try to close this too soon
		};
		File logDirectory = new File(SpringContextUtil.getServletContext().getRealPath("/WEB-INF/logs"));
		for(String logFile : logDirectory.list(new FilenameFilter() {
			public boolean accept(File file, String filename) { return filename.startsWith("ssf.log"); }})) {
			zipOut.putNextEntry(new ZipEntry(logFile));
			FileCopyUtils.copy(new FileInputStream(new File(logDirectory, logFile)), wrapper);
		}
		zipOut.finish();


		model.put(WebKeys.DOWNLOAD_URL, WebUrlUtil.getServletRootURL(request) + WebKeys.SERVLET_VIEW_FILE + "?viewType=zipped&fileId=" +
				tempFile.getName() + "&" + WebKeys.URL_FILE_TITLE + "=logfiles.zip");
		return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT, model);
	}
}
