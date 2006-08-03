package com.sitescape.ef.portlet.administration;

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

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ImportDefinitionController extends  SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
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
		    		getDefinitionModule().addDefinition(doc);
		    	} catch (Exception fe) {
//		    		errorMap.put(entry.getKey(), fe.getLocalizedMessage());	
		    		fe.printStackTrace();
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
