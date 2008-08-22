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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.dom4j.Document;
import org.dom4j.Node;

import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.web.WebKeys;

public class LicenseReportController extends AbstractReportController {

	@Override
	protected void populateModel(RenderRequest request, Map model)
	{
		super.populateModel(request, model);
		Map formData = request.getParameterMap();

		Date startDate = (Date) model.get(WebKeys.REPORT_START_DATE);
		Date endDate = (Date) model.get(WebKeys.REPORT_END_DATE);
		Date currentDate = new Date();
		if(endDate != null) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(endDate);
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}
		model.put(WebKeys.LICENSE_DATA, getReportModule().generateLicenseReport(startDate, endDate));
		model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);

		StringBuffer uids = new StringBuffer();
		for(Document doc : getLicenseModule().getLicenses()) {
			uids.append(getValue(doc, "//KeyInfo/@uid") + " ");
			model.put(WebKeys.LICENSE_ISSUED, getValue(doc, "//KeyInfo/@issued"));
			model.put(WebKeys.LICENSE_EFFECTIVE, getValue(doc, "//Dates/@effective") + " - " + getValue(doc, "//Dates/@expiration"));
			model.put(WebKeys.LICENSE_CONTACT, getValue(doc, "//AuditPolicy/ReportContact"));
		}
		model.put(WebKeys.LICENSE_KEY, uids.toString());
		model.put(WebKeys.LICENSE_USERS, "" + getLicenseModule().getRegisteredUsers());
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
