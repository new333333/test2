package com.sitescape.ef.portlet.definitionBuilder;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author hurley
 *
 */
public class ViewDefinitionXmlController extends SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());

		Map formData = request.getParameterMap();

		String selectedItem = PortletRequestUtils.getStringParameter(request,"id", "");
			
		//See if there is an operation to perform
		if (formData.containsKey("saveLayout")) {
			//This is a request to save the x,y layout of the workflow state graph
			getDefinitionModule().saveDefinitionLayout(selectedItem, new MapInputData(formData));
		}
		
		//Pass the selection id to be shown on to the rendering phase
		response.setRenderParameter("selectedItem", selectedItem);
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
			
		return new ModelAndView(WebKeys.VIEW_DEFINITION_XML, model);
	}

}
