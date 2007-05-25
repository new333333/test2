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
package com.sitescape.team.portlet.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DebugHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

public class DownloadFileController extends SAbstractController {
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
    	// Print debug information pertaining to cross context session sharing
		DebugHelper.testRequestEnv("DownloadFileController", request);

		// I expect filespec parameter to be present, so validate that it is.
		String filespec = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_FILE);
		
		File file = new File(filespec);
		
		FileInputStream fi = new FileInputStream(file);
		try {
			response.setContentType("text/xml");
			OutputStream out = response.getPortletOutputStream();
			byte[] buffer = new byte[4096];
			int count;
			while((count = fi.read(buffer)) > -1) {
				out.write(buffer, 0, count);
			}
			out.flush();
		}
		finally {
			fi.close();
		}
		
		return null;
	}
}
