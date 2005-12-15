package com.sitescape.ef.portlet.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DebugHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

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
