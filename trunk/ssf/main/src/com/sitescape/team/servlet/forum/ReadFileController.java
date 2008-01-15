package com.sitescape.team.servlet.forum;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.servlet.SAbstractController;

public class ReadFileController extends SAbstractController {
	
	private FileTypeMap mimeTypes;

	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		// TODO To be completed by Peter

		// Assuming that the full request URL was http://localhost:8080/ssf/s/readFile/123/456/789/junk.doc,
		// the following call returns "/readFile/123/456/789/junk.doc" portion of the URL.
		String pathInfo = request.getPathInfo();
		
		// Do whatever you want with the path info obtained.
		
		return null;
	}
}
