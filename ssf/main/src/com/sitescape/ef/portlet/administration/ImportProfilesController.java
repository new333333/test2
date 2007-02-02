package com.sitescape.ef.portlet.administration;

import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Binder;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ImportProfilesController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			String data=null;
			Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			data = PortletRequestUtils.getStringParameter(request, "profiles", null);
	    	if (data == null) response.setRenderParameters(formData);
	    	else {
    			StringReader fIn = new StringReader(data);
    			SAXReader xIn = new SAXReader();
    			Document doc = xIn.read(fIn);   
    			fIn.close();
    			
    			getProfileModule().addEntries(binderId, doc);
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
		Binder binder = getProfileModule().getProfileBinder();
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		return new ModelAndView(WebKeys.VIEW_ADMIN_IMPORT_PROFILES, model);
	}

}
