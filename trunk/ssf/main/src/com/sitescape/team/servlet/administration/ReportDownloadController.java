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
package com.sitescape.team.servlet.administration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Collection;

import javax.activation.FileTypeMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.util.Validator;
public class ReportDownloadController extends  SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Map formData = request.getParameterMap();
		MapInputData inputData = new MapInputData(formData);
		GregorianCalendar cal = new GregorianCalendar();
		Date startDate = DateHelper.getDateFromInput(inputData, WebKeys.URL_START_DATE);
		Date endDate = DateHelper.getDateFromInput(inputData, WebKeys.URL_END_DATE);
		cal.setTime(endDate);
		cal.add(Calendar.DATE, 1);
		endDate = cal.getTime();
		if (formData.containsKey("okBtn") || formData.containsKey("applyBtn")) {
			//Get the list of binders for reporting
			List<Long> ids = new ArrayList();
			Long profileId = null;
			//Get the binders for reporting
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				String key = (String)me.getKey();
				if (key.startsWith(DomTreeBuilder.NODE_TYPE_FOLDER)) {
					String binderId = key.replaceFirst(DomTreeBuilder.NODE_TYPE_FOLDER + "_", "");
					ids.add(Long.valueOf(binderId));
				} else if (key.startsWith(DomTreeBuilder.NODE_TYPE_WORKSPACE)) {
					String binderId = key.replaceFirst(DomTreeBuilder.NODE_TYPE_WORKSPACE + "_", "");
					ids.add(Long.valueOf(binderId));
				} else if (key.startsWith(DomTreeBuilder.NODE_TYPE_PEOPLE)) {
					//people are handled separately
					String binderId = key.replaceFirst(DomTreeBuilder.NODE_TYPE_PEOPLE + "_", "");
					profileId = Long.valueOf(binderId);
				}
			}

			FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
			response.setContentType(mimeTypes.getContentType("report.csv"));
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"report.csv\"");
			List<Map<String, Object>> report = getReportModule().generateReport(ids, startDate, endDate);
			
			response.getWriter().print(NLT.get("report.columns.id") + ",");
			response.getWriter().print(NLT.get("report.columns.parent") + ",");
			response.getWriter().print(NLT.get("report.columns.title") + ",");
			response.getWriter().print(NLT.get("report.columns.add") + ",");
			response.getWriter().print(NLT.get("report.columns.view") + ",");
			response.getWriter().print(NLT.get("report.columns.modify") + ",");
			response.getWriter().print(NLT.get("report.columns.delete"));
			response.getWriter().println();

			for(Map<String, Object> row : report) {
				response.getWriter().print(row.get(ReportModule.BINDER_ID) + ",");
				if(row.containsKey(ReportModule.BINDER_PARENT)) {
					response.getWriter().print(row.get(ReportModule.BINDER_PARENT));
				}
				response.getWriter().print(",");
				response.getWriter().print(row.get(ReportModule.BINDER_TITLE) + ",");
				response.getWriter().print(row.get(AuditTrail.AuditType.add.name()) + ",");
				response.getWriter().print(row.get(AuditTrail.AuditType.view.name()) + ",");
				response.getWriter().print(row.get(AuditTrail.AuditType.modify.name()) + ",");
				response.getWriter().print(row.get(AuditTrail.AuditType.delete.name()));
				response.getWriter().println();
			}
			response.getWriter().flush();
		} 
		return null;
	}
}
