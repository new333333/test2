package com.sitescape.ef.portlet.sample;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.portletadapter.portlet.ActionRequestImpl;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;

public class UploadFileController extends SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, 
			ActionResponse response) throws Exception {
		// Note: This sample implements oversimplified and naive flow control
		// that handles form processing involving file upload. Production
		// quality code will add appropriate error handling or base its
		// implementation on one of more suitable base classes provided by
		// the Spring MVC framework. 
		
		if(request.getParameterMap().containsKey("submit")) {
			// The form was submitted. Process it.
			
			// Get optional title
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			
			// Get optional server directory path. If not present, use default value.
			String serverDir = PortletRequestUtils.getStringParameter(request, "serverDir", "C:/junk");
		    
			// Process attached files (this code is generalized to handle multiple files)
			
			// Note: This line of code is based on the knowledge that our file
			// upload functionality is implemented using portlet adapter framework,
			// which in turn relies on the Spring's fileupload support. 
			// This code does not apply to standard portlets. 
			Map fileMap = ((MultipartFileSupport) request).getFileMap();
			
		    if(fileMap != null) {
		        for(Iterator it = fileMap.values().iterator(); it.hasNext();) {
		            MultipartFile mpFile = (MultipartFile) it.next();
		            
		            String destFileName = mpFile.getOriginalFilename();
		            
		            if(destFileName != null && destFileName.length() > 0) {
		            	// Get the destination filepath.
		            	String filepath = serverDir + File.separator + destFileName;

		            	// Transfer the received file to the given destination file.
			            mpFile.transferTo(new File(filepath));
		            }
		        }
		    }
		    response.setRenderParameter("submit", request.getParameter("submit"));
		}		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if(request.getParameterMap().containsKey("submit")) {
			// The form was submitted. Process it.
		    return new ModelAndView("sample/uploadFile", "header", "File Uploaded Successfully");
		}
		else {
			// Return form view.
			return new ModelAndView("sample/uploadFile", "header", "Upload File");
		}		
	}

}
