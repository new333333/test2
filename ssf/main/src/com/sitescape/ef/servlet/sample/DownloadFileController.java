package com.sitescape.ef.servlet.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

public class DownloadFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		// I expect filespec parameter to be present, so validate that it is.
		String filespec = RequestUtils.getRequiredStringParameter(request, "filespec");
		
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
