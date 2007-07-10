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

		Document doc = getLicenseModule().getLicense();
		Node node = null;
		model.put(WebKeys.LICENSE_KEY, getValue(doc, "//KeyInfo/@uid"));
		model.put(WebKeys.LICENSE_ISSUED, getValue(doc, "//KeyInfo/@issued"));
		model.put(WebKeys.LICENSE_EFFECTIVE, getValue(doc, "//Dates/@effective") + " - " + getValue(doc, "//Dates/@expiration"));
		model.put(WebKeys.LICENSE_USERS, getValue(doc, "//Users/@registered"));
		model.put(WebKeys.LICENSE_CONTACT, getValue(doc, "//AuditPolicy/ReportContact"));
	}
	
	private String getValue(Document doc, String xpath)
	{
		Node node = null;
		return (node=doc.selectSingleNode(xpath))!=null?node.getText():"";
	}
	
	@Override
	protected String chooseView(Map formData) {
		return WebKeys.VIEW_LICENSE_REPORT;
	}

}
