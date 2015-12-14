/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.administration;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.license.LicenseException;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;


public class ManageLicenseController extends SAbstractController {
	private static final String LICENSE_XSL_FILE = "/WEB-INF/xslt/license.xslt";
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		if (formData.containsKey("updateBtn") && WebHelper.isMethodPost(request)) {
			try {
				getLicenseModule().updateLicense();
			} catch(LicenseException e) {
			}
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user;
		DateFormat localeDateFormat; 
		
        user = RequestContextHolder.getRequestContext().getUser();
		
		// Get a DateFormat object based on the locale this user is running in.
		localeDateFormat = DateFormat.getDateInstance( DateFormat.MEDIUM, user.getLocale() );
		
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
		for(Document doc : docs)
		{
			String issuedDate;
			String effectiveDate;
			String expireDate;
			String expireDateOrig;
			
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

			// Format the "issued" date according to the locale the user is running in.
			issuedDate = getValue(doc, "//KeyInfo/@issued");
			if ( issuedDate != null && issuedDate.length() > 0 )
			{
				SimpleDateFormat dateFormat;
				Date date;
				
				// Parse the "issued" date.
				dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
				
				try
				{
					date = dateFormat.parse( issuedDate );
				
					// Format the "issued" date using the locale the user is running in.
					issuedDate = localeDateFormat.format( date );
				}
				catch (Exception ex)
				{
					// Nothing we can do.
				}
			}
			model.put( WebKeys.LICENSE_ISSUED, issuedDate );

			// Format the "effective" date according to the locale the user is running in.
			effectiveDate = getValue( doc, "//Dates/@effective" );
			if ( effectiveDate != null && effectiveDate.length() > 0 )
			{
				SimpleDateFormat dateFormat;
				Date date;
				
				// Parse the "effective" date.
				dateFormat = new SimpleDateFormat( "MM/dd/yyyy" );
				
				try
				{
					date = dateFormat.parse( effectiveDate );
				
					// Format the "effective" date using the locale the user is running in.
					effectiveDate = localeDateFormat.format( date );
				}
				catch (Exception ex)
				{
					// Nothing we can do.
				}
			}
			
			// Format the "expiration" date according to the locale the user is running in.
			SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy" );
			expireDate = getValue(doc, "//Dates/@expiration");
			expireDateOrig = expireDate;
			if ( expireDate != null && expireDate.length() > 0 )
			{
				Date date;
				
				// Parse the "expiration" date.				
				try
				{
					date = dateFormat.parse( expireDate );
				
					// Format the "expiration" date using the locale the user is running in.
					expireDate = localeDateFormat.format( date );
				}
				catch (Exception ex)
				{
					// Nothing we can do.
				}
			}

			if ( expireDateOrig.equals("1/1/2500") ) {
				model.put(WebKeys.LICENSE_EFFECTIVE, effectiveDate + " - " + NLT.get("license.expire.never"));
			} else {
				if (effectiveDate.equalsIgnoreCase("trial")) {
					String days = getValue( doc, "//Dates/@expiration");
					int daysSinceInstallation = getCoreDao().daysSinceInstallation();
					Calendar trialEffectiveDate = Calendar.getInstance();
					trialEffectiveDate.add(Calendar.DATE, -daysSinceInstallation);
					Calendar trialEndDate = Calendar.getInstance();
					trialEndDate.setTime(trialEffectiveDate.getTime());
					trialEndDate.add(Calendar.DATE, Integer.valueOf(days));
					model.put(WebKeys.LICENSE_EFFECTIVE, effectiveDate + " - " + expireDate + " (" + 
							localeDateFormat.format( trialEffectiveDate.getTime() ) + " - " + localeDateFormat.format( trialEndDate.getTime() ) + ")");
				} else {
					model.put(WebKeys.LICENSE_EFFECTIVE, effectiveDate + " - " + expireDate);
				}

			}

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
							if (LicenseChecker.isAuthorizedByLicense(ele.attributeValue("id"))) {
								optionsList.append(ele.attribute("title").getValue() + ",");
							}
						}
						model.put(WebKeys.LICENSE_OPTIONS_LIST, optionsList.toString());
					}
				}
				if(obj instanceof Element) {
					Element singleOption = null;
					singleOption = (Element) obj;

					if(singleOption != null) {
						if (LicenseChecker.isAuthorizedByLicense(singleOption.attributeValue("id"))) {
							model.put(WebKeys.LICENSE_OPTIONS_LIST, singleOption.attribute("title").getValue());
						}
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
	
	private CoreDao getCoreDao()
	{
		return (CoreDao) SpringContextUtil.getBean( "coreDao" );
	}

}
