package com.sitescape.ef.servlet.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.ef.web.util.DebugHelper;

public class DownloadFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
    	// Print debug information pertaining to cross context session sharing
		DebugHelper.testRequestEnv("DownloadFileController", request);

		// I expect filespec parameter to be present, so validate that it is.
		String filespec = RequestUtils.getRequiredStringParameter(request, WebKeys.FORUM_URL_FILE);
		
		File file = new File(filespec);
		
		FileInputStream fi = new FileInputStream(file);
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			response.setHeader(
				"Content-Disposition",
				"attachment; filename=\"" + file.getName() + "\"");
			OutputStream out = response.getOutputStream();
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
