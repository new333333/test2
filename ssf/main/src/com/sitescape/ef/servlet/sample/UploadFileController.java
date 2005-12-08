package com.sitescape.ef.servlet.file;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

/**
 * @author Jong Kim
 *
 */
public class UploadFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

		// Note: This sample implements oversimplified and naive flow control
		// that handles form processing involving file upload. Production
		// quality code will add appropriate error handling or base its
		// implementation on one of more suitable base classes provided by
		// the Spring MVC framework. 
		
		if(request.getParameterMap().containsKey("submit")) {
			// The form was submitted. Process it.
			
			// Get optional title
			String title = RequestUtils.getStringParameter(request, "title", "");
			
			// Get optional server directory path. If not present, use default value.
			String serverDir = RequestUtils.getStringParameter(request, "serverDir", "C:/junk");
		    
			// Process attached files (this code is generalized to handle multiple files)
		    Map fileMap = ((MultipartHttpServletRequest) request).getFileMap();
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
		    
		    // Normally, in addition to storing the uploaded file on disk, we 
		    // would store some metadata (for example, the title value obtained 
		    // above) into the database using our business module. But this 
		    // sample program doesn't do that. 

		    return new ModelAndView("sample/servlet.uploadFile", "header", "File Uploaded Successfully");
		}
		else {
			// Return form view.
			return new ModelAndView("sample/servlet.uploadFile", "header", "Upload File");
		}
		
	}
	
}
