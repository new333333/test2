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
package com.sitescape.team.portlet.administration;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ImportDefinitionController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			int i=0;
			Map errorMap = new HashMap();
			while (++i>0) {
				String data;
				try {
					data = PortletRequestUtils.getStringParameter(request, "definition" + i);
				} catch (Exception ex) {continue;}
		    	if (data == null) break;
		    	try {
			    	StringReader fIn = new StringReader(data);
		    		SAXReader xIn = new SAXReader();
		    		Document doc = xIn.read(fIn);   
		    		fIn.close();
		    		getDefinitionModule().addDefinition(doc, true);
		    	} catch (Exception fe) {
//		    		errorMap.put(entry.getKey(), fe.getLocalizedMessage());	
		    		logger.error(fe.getLocalizedMessage(), fe);
		    	}
			}
		
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		return new ModelAndView(WebKeys.VIEW_ADMIN_IMPORT_DEFINITIONS);
	}

}
