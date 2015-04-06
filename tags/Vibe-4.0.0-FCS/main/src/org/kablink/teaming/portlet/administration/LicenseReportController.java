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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.web.WebKeys;


public class LicenseReportController extends AbstractReportController {

	@Override
	protected void populateModel(RenderRequest request, Map model)
	{
		super.populateModel(request, model);
		Date	originalEndDate	= null;
		Map		formData;

		formData = request.getParameterMap();

		Date startDate = (Date) model.get(WebKeys.REPORT_START_DATE);
		Date endDate = (Date) model.get(WebKeys.REPORT_END_DATE);
		Date currentDate = new Date();
		if(endDate != null) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(endDate);
			
			// Remember the original end date supplied by the user.
			originalEndDate = cal.getTime();
			
			// Add 1 day to the end date.
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}
		
		if (formData.containsKey("okBtn")) {
			DateTimeFormatter	dtf;
			
			// There is a job that runs once a day that counts the number of users in Teaming and writes this information
			// into the SS_LicenseStats table.  If the end date of the report equals today, do a count right now and
			// write the count into the SS_LicenseStats table so the information returned to the user is up-to-date.
			// Is the end date of the report equal to today?
			if ( originalEndDate != null && currentDate != null )
			{
				GregorianCalendar	endCal;
				Calendar			todayCal;
				
				todayCal = GregorianCalendar.getInstance();
				endCal = new GregorianCalendar();
				endCal.setTime(  originalEndDate );
				if ( endCal.get( Calendar.MONTH ) == todayCal.get( Calendar.MONTH ) )
				{
					if ( endCal.get( Calendar.DAY_OF_MONTH ) == todayCal.get( Calendar.DAY_OF_MONTH ) )
					{
						if ( endCal.get( Calendar.YEAR ) == todayCal.get( Calendar.YEAR ) )
						{
							// If we get here then the end date of the report is today.
							// Count the users in Teaming so the data we return is up-to-date.
							getLicenseModule().recordCurrentUsage();
						}
					}
				}
			}
			
			model.put(WebKeys.LICENSE_DATA, getReportModule().generateLicenseReport(startDate, endDate));
			model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
			model.put("releaseInfo", ReleaseInfo.getReleaseInfo());
	
			dtf = DateTimeFormat.forPattern( "yyyy-MM-dd" );
			dtf = dtf.withOffsetParsed();
			
			StringBuffer uids = new StringBuffer();
			for(Document doc : getLicenseModule().getLicenses()) {
				Date	tmpDate;
				String	tmpDateStr;
				
				uids.append(getValue(doc, "//KeyInfo/@uid") + " ");
				
				// Get the date issued from the license xml and convert it to a Date object.
				tmpDate = null;
				tmpDateStr = getValue( doc, "//KeyInfo/@issued" );
				try
				{
					tmpDate = dtf.parseDateTime( tmpDateStr ).toDate();
				}
				catch (Exception ex)
				{
					// This should never happen.
				}
				if ( tmpDate == null )
				{
					// This should never happen.
					tmpDate = new Date();
				}
				model.put( WebKeys.LICENSE_ISSUED, tmpDate );
				
				dtf = DateTimeFormat.forPattern( "MM/dd/yyyy" );
				dtf = dtf.withOffsetParsed();
				
				// Get the license start date.
				tmpDate = null;
				tmpDateStr = getValue(doc, "//Dates/@effective");
				try
				{
					tmpDate = dtf.parseDateTime( tmpDateStr ).toDate();
				}
				catch (Exception ex)
				{
					// This should never happen.
				}
				if ( tmpDate == null )
				{
					// This should never happen.
					tmpDate = new Date();
				}
				model.put(WebKeys.LICENSE_EFFECTIVE_START,  tmpDate );

				// Get the license end date.
				tmpDate = null;
				tmpDateStr = getValue(doc, "//Dates/@expiration");
				try
				{
					tmpDate = dtf.parseDateTime( tmpDateStr ).toDate();
				}
				catch (Exception ex)
				{
					// This should never happen.
				}
				if ( tmpDate == null )
				{
					// This should never happen.
					tmpDate = new Date();
				}
				
				Object obj = doc.selectObject("//Options/*");

				if (obj != null) {
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

				model.put(WebKeys.LICENSE_EFFECTIVE_END,  tmpDate );

				model.put(WebKeys.LICENSE_CONTACT, getValue(doc, "//AuditPolicy/ReportContact"));
				model.put(WebKeys.LICENSE_PRODUCT_TITLE, getValue(doc, "//Product/@title"));
				model.put(WebKeys.LICENSE_PRODUCT_VERSION, getValue(doc, "//Product/@version"));
			}
			model.put(WebKeys.LICENSE_KEY, uids.toString());
			model.put(WebKeys.LICENSE_USERS, getLicenseModule().getRegisteredUsers());
			model.put(WebKeys.LICENSE_EXTERNAL_USERS, getLicenseModule().getExternalUsers());

		}
	}
	
	private String getValue(Document doc, String xpath)
	{
		Node node = null;
		return (doc != null && (node=doc.selectSingleNode(xpath))!=null)?node.getText():"";
	}
	
	@Override
	protected String chooseView(Map formData) {
		return WebKeys.VIEW_LICENSE_REPORT;
	}

}
