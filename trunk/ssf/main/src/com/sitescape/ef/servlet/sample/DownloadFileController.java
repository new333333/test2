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

public class DownloadFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
    	// To test cross context session sharing: 
		if(1 == 2) {
			String loginName = request.getRemoteUser();
			HttpSession ses = request.getSession();
			ses.setAttribute("set-by-download-file", "Oh!");
			System.out.println("*** DownloadFileController login name: " + loginName);
			System.out.println("*** DownloadFileController session id: " + ses.getId()); 
			System.out.println("*** DownloadFileController set-by-main-servlet: " + ses.getAttribute("set-by-main-servlet")); 
			System.out.println("*** DownloadFileController set-by-employees: " + ses.getAttribute("set-by-employees"));
			System.out.println("*** DownloadFileController set-by-portlet-adapter: " + ses.getAttribute("set-by-portlet-adapter"));
		}
		// test ends:

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
