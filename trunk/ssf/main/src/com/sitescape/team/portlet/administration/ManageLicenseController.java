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

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DocumentSource;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.license.LicenseException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.util.Validator;

public class ManageLicenseController extends SAbstractController {
	private static final String LICENSE_XSL_FILE = "/WEB-INF/xslt/license.xslt";
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		}
		if(formData.containsKey("updateBtn")) {
			try {
				getLicenseModule().updateLicense();
			} catch(LicenseException e) {
			}
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		try {
			getLicenseModule().validateLicense();
		} catch(LicenseException e) {
			model.put(WebKeys.LICENSE_EXCEPTION,
					e.getLocalizedMessage());
		}
		Collection<Document> docs = getLicenseModule().getLicenses();
		StringBuffer visibleDoc = new StringBuffer();
		StringBuffer uids = new StringBuffer();
		for(Document doc : docs) {
			try {
				TransformerFactory transFactory = TransformerFactory.newInstance();
				Source xsltSource = new StreamSource(request.getPortletSession().getPortletContext().getResourceAsStream(LICENSE_XSL_FILE));
				Templates template = transFactory.newTemplates(xsltSource);
				Transformer trans = template.newTransformer();
				StreamResult result = new StreamResult(new StringWriter());
				trans.transform(new DocumentSource(doc), result);
				String textDoc= result.getWriter().toString().replaceAll("<\\?xml .*\\?>", "");
				Document helperDoc = DocumentHelper.createDocument();
				Element helperRoot = helperDoc.addElement("pre");
				helperRoot.addText(textDoc);
				visibleDoc.append(helperRoot.asXML());
			} catch(TransformerConfigurationException e) {
				logger.warn("Unable to process license with XSL", e);
			}
			uids.append(getValue(doc, "//KeyInfo/@uid") + " ");
			
			model.put(WebKeys.LICENSE_ISSUED, getValue(doc, "//KeyInfo/@issued"));

			String expireDate = getValue(doc, "//Dates/@expiration");

			if(expireDate.equals("1/1/2500"))
				model.put(WebKeys.LICENSE_EFFECTIVE, getValue(doc, "//Dates/@effective") 
						+ " - " + NLT.get("license.expire.never"));
			else
				model.put(WebKeys.LICENSE_EFFECTIVE, getValue(doc, "//Dates/@effective") + " - " + expireDate);
			model.put(WebKeys.LICENSE_CONTACT, getValue(doc, "//AuditPolicy/ReportContact"));

			model.put(WebKeys.LICENSE_ISSUER, getValue(doc, "//KeyInfo/@by"));
			model.put(WebKeys.LICENSE_PRODUCT_ID, getValue(doc, "//Product/@id"));
			model.put(WebKeys.LICENSE_PRODUCT_TITLE, getValue(doc, "//Product/@title"));
			model.put(WebKeys.LICENSE_PRODUCT_VERSION, getValue(doc, "//Product/@version"));

			Object obj = doc.selectObject("//Options/*");

			if(obj != null) {
				if(obj instanceof List) {
					List options = null;
					options = (List) obj;

					if(options != null) {
						StringBuilder optionsList = new StringBuilder();

						for(int i = 0; i < options.size(); i++) {
							Element ele = (Element) options.get(i);
							optionsList.append(ele.attribute("title").getValue() + ",");
						}
						model.put(WebKeys.LICENSE_OPTIONS_LIST, optionsList.toString());
					}
				}
				if(obj instanceof Element) {
					Element singleOption = null;
					singleOption = (Element) obj;

					if(singleOption != null) {
						model.put(WebKeys.LICENSE_OPTIONS_LIST, singleOption.attribute("title").getValue());
					}
				}
			}

			obj = doc.selectObject("//ExternalAccess/*");

			if(obj != null) {
				if(obj instanceof List) {
					List extAccess = null;
					extAccess = (List) obj;

					if(extAccess != null) {
						StringBuilder extAccessList = new StringBuilder();

						for(int i = 0; i < extAccess.size(); i++) {
							Element ele = (Element) extAccess.get(i);
							extAccessList.append(ele.asXML().replace("<", "").replace("/>", "") + ",");
						}
						model.put(WebKeys.LICENSE_EXTERNAL_ACCESS_LIST, extAccessList.toString());
					}
				}
				if(obj instanceof Element) {
					Element singleExtAccess = null;
					singleExtAccess = (Element) obj;

					if(singleExtAccess != null) {
						model.put(WebKeys.LICENSE_EXTERNAL_ACCESS_LIST, singleExtAccess.asXML().replace("<", "").replace("/>", ""));
					}
				}
			}
		}

		model.put(WebKeys.LICENSE_KEY, uids.toString());
		model.put(WebKeys.LICENSE_USERS, ""+getLicenseModule().getRegisteredUsers());
		model.put(WebKeys.LICENSE_EXTERNAL_USERS, ""+getLicenseModule().getExternalUsers());
		model.put(WebKeys.LICENSE, visibleDoc);
		return new ModelAndView("administration/manage_license", model);

	}

	private String getValue(Document doc, String xpath)
	{
		Node node = null;
		return (doc != null && (node=doc.selectSingleNode(xpath))!=null)?node.getText():"";
	}
	
	private List<Node> getMultipleValues(Document doc, String xpath)
	{
		List<Node> list = null;
		return (doc != null && (list=doc.selectNodes(xpath))!=null)?list:null;
	}
}
