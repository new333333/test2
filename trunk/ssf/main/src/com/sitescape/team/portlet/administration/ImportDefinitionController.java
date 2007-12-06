/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portlet.administration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
						if(myFile.getOriginalFilename().toLowerCase().endsWith(".zip")) {
							ZipInputStream zipIn = new ZipInputStream(myFile.getInputStream());
							ZipEntry entry = null;
							while((entry = zipIn.getNextEntry()) != null) {
								loadDefinitions(entry.getName(), new ZipStreamWrapper(zipIn), errors);
								zipIn.closeEntry();
							}
						} else {
							loadDefinitions(myFile.getOriginalFilename(), myFile.getInputStream(), errors);
						}
						myFile.getInputStream().close();
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

	protected void loadDefinitions(String fileName, InputStream fIn, List errors)
	{
		try {
			SAXReader xIn = new SAXReader();
			Document doc = xIn.read(fIn);   
			getDefinitionModule().addDefinition(doc, true);
		} catch (Exception fe) {
			errors.add((fileName==null ? "" : fileName) + " : " + (fe.getLocalizedMessage()==null ? fe.getMessage() : fe.getLocalizedMessage()));
		}
	}

	static class ZipStreamWrapper extends InputStream
	{
		ZipInputStream zipIn;
		public ZipStreamWrapper(ZipInputStream zipIn)
		{
			this.zipIn = zipIn;
		}
		
		public int read() throws IOException
		{
			return zipIn.read();
		}
		
		public void close() throws IOException
		{
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
