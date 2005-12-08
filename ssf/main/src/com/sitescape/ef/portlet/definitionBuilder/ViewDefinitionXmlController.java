package com.sitescape.ef.portlet.definitionBuilder;

import java.lang.Integer;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinitionInvalidException;
import com.sitescape.ef.domain.DefinitionInvalidOperation;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;

import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;

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
			String xmlData = PortletRequestUtils.getStringParameter(request,"xmlData", "");
			getDefinitionModule().saveDefinitionLayout(selectedItem, formData);
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
