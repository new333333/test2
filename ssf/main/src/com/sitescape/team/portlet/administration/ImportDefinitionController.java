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

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ImportDefinitionController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && request instanceof MultipartFileSupport) {
			int i=0;
			Map fileMap = ((MultipartFileSupport) request).getFileMap();
			if (fileMap != null) {
				List errors = new ArrayList();
				while (++i>0) {
					MultipartFile myFile=null;
					try {
						myFile = (MultipartFile)fileMap.get("definition" + i);
						if (myFile == null) break;
						if (Validator.isNull(myFile.getOriginalFilename())) continue; //not filled in
						SAXReader xIn = new SAXReader();
						InputStream fIn = myFile.getInputStream();
						Document doc = xIn.read(fIn);   
						fIn.close();
						getDefinitionModule().addDefinition(doc, true);
					} catch (Exception fe) {
						errors.add((myFile==null ? "" : myFile.getOriginalFilename()) + " : " + (fe.getLocalizedMessage()==null ? fe.getMessage() : fe.getLocalizedMessage()));
					}
				}
				if (!errors.isEmpty()) response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
			}
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else {
			String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
			if (WebKeys.OPERATION_RELOAD_CONFIRM.equals(operation)) {
				response.setRenderParameters(formData);
			} else if (WebKeys.OPERATION_RELOAD.equals(operation)) {
				getAdminModule().updateDefaultDefinitions(RequestContextHolder.getRequestContext().getZoneId());
				response.setRenderParameter("redirect", "true");
			} else {
				response.setRenderParameters(formData);
			}
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}

		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
		if (WebKeys.OPERATION_RELOAD_CONFIRM.equals(operation)) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_IMPORT_ALL_DEFINITIONS_CONFIRM);
		}
		Map model = new HashMap();
		model.put(WebKeys.ERROR_LIST, request.getParameterValues(WebKeys.ERROR_LIST));

		return new ModelAndView(WebKeys.VIEW_ADMIN_IMPORT_DEFINITIONS, model);
	}

}
