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

import java.io.PrintWriter;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.module.workflow.WorkflowUtils;

public class ReportDownloadController extends  SAbstractController {
	
	static HashMap<String, String> columnNames = null;
	
	static {
		columnNames = new HashMap<String,String>();
		columnNames.put(ReportModule.BINDER_ID, "report.columns.id");
		columnNames.put(ReportModule.BINDER_PARENT, "report.columns.parent");
		columnNames.put(ReportModule.BINDER_TITLE, "report.columns.title");
		columnNames.put(ReportModule.USER_ID, "report.columns.user");
		columnNames.put(AuditTrail.AuditType.add.name(), "report.columns.add");
		columnNames.put(AuditTrail.AuditType.view.name(), "report.columns.view");
		columnNames.put(AuditTrail.AuditType.modify.name(), "report.columns.modify");
		columnNames.put(AuditTrail.AuditType.delete.name(), "report.columns.delete");
		columnNames.put(ReportModule.LOGIN_COUNT, "report.columns.login_count");
		columnNames.put(ReportModule.LAST_LOGIN, "report.columns.last_login");
		columnNames.put(ReportModule.STATE, "report.columns.state");
		columnNames.put(ReportModule.DEFINITION_ID, "report.columns.definition");
		columnNames.put(ReportModule.AVERAGE, "report.columns.average");
		columnNames.put(ReportModule.AVERAGE_TI, "report.columns.average_ti");
		columnNames.put(ReportModule.COUNT, "report.columns.count");
	}

	static private boolean isUserColumn(String column) {
		return column.equals(ReportModule.USER_ID);
	}
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Map formData = request.getParameterMap();
		MapInputData inputData = new MapInputData(formData);
		GregorianCalendar cal = new GregorianCalendar();
		Date startDate = DateHelper.getDateFromInput(inputData, WebKeys.URL_START_DATE);
		Date endDate = DateHelper.getDateFromInput(inputData, WebKeys.URL_END_DATE);
		if(endDate != null) {
			cal.setTime(endDate);
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}
		if (formData.containsKey("forumOkBtn")) {
			FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
			response.setContentType(mimeTypes.getContentType("report.csv"));
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"report.csv\"");

			String reportType = RequestUtils.getRequiredStringParameter(request, WebKeys.URL_REPORT_TYPE);
			String[] columns = null;
			List<Map<String, Object>> report = null;
			HashSet userIds = null;
			boolean hasUsers = true;
			if("binder".equals(reportType)) {
				//Get the list of binders for reporting
				hasUsers = RequestUtils.getBooleanParameter(request, WebKeys.URL_BY_USER, false);
				List<Long> ids = new ArrayList();

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
					}
				}

				report = getReportModule().generateReport(ids, hasUsers, startDate, endDate);
				columns = new String[] {ReportModule.BINDER_ID, ReportModule.BINDER_PARENT, ReportModule.BINDER_TITLE,
						ReportModule.USER_ID, AuditTrail.AuditType.view.name(), AuditTrail.AuditType.add.name(),
						AuditTrail.AuditType.modify.name(), AuditTrail.AuditType.delete.name()};
			} else if ("login".equals(reportType)) {
				report = getReportModule().generateLoginReport(startDate, endDate);
				columns = new String[] {ReportModule.USER_ID, ReportModule.LOGIN_COUNT, ReportModule.LAST_LOGIN};
			} else if ("workflow".equals(reportType)) {
				if(RequestUtils.getStringParameter(request, WebKeys.URL_REPORT_FLAVOR, "").equals("averages")) {
					//Get the list of binders for reporting
					List<Long> ids = new ArrayList();

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
						}
					}

					report = getReportModule().generateWorkflowStateReport(ids, startDate, endDate);
					columns = new String[] {ReportModule.BINDER_ID, ReportModule.BINDER_PARENT, ReportModule.BINDER_TITLE,
							ReportModule.DEFINITION_ID, ReportModule.STATE, ReportModule.AVERAGE_TI, ReportModule.AVERAGE };
				} else if(RequestUtils.getStringParameter(request, WebKeys.URL_REPORT_FLAVOR, "").equals("current")) {
					//Get the list of binders for reporting
					List<Long> ids = new ArrayList();

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
						}
					}

					report = getReportModule().generateWorkflowStateCountReport(ids);
					columns = new String[] {ReportModule.BINDER_ID, ReportModule.BINDER_PARENT, ReportModule.BINDER_TITLE,
							ReportModule.STATE, ReportModule.COUNT };
				}
			}
			printReport(response.getWriter(), report, columns, hasUsers);
			response.getWriter().flush();
		}
		return null;
	}
	
	protected void printReport(PrintWriter out, List<Map<String, Object>> report, String[] columns, boolean hasUsers)
	{
		HashMap<Long,Principal> userMap = new HashMap<Long,Principal>();
		HashMap<String,Definition> definitionMap = new HashMap<String, Definition>();
        User requestor = RequestContextHolder.getRequestContext().getUser();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, requestor.getLocale());
        dateFormat.setTimeZone(requestor.getTimeZone());
        
		HashSet userIds = new HashSet();
		HashSet<String> definitionIds = new HashSet<String>();
		for(Map<String, Object> row : report) {
			if(row.containsKey(ReportModule.USER_ID)) {
				userIds.add(row.get(ReportModule.USER_ID));
			}
			if(row.containsKey(ReportModule.DEFINITION_ID)) {
				definitionIds.add((String) row.get(ReportModule.DEFINITION_ID));
			}
		}
		if(userIds.size() > 0) {
			List<Principal> principals = getProfileModule().getPrincipals(userIds, RequestContextHolder.getRequestContext().getZoneId());
			for(Principal p : principals) {
				userMap.put(p.getId(), p);
			}
		}
		if(definitionIds.size() > 0) {
			for(String id : definitionIds) {
				definitionMap.put(id, getDefinitionModule().getDefinition(id));
			}
		}

		for(int i = 0; i < columns.length; i++) {
			String name = columns[i];
			if(!isUserColumn(name) || hasUsers) {
				if(i > 0) {
					out.print(",");
				}
				out.print(NLT.get(columnNames.get(name)));
			}
		}
		out.println();

		Definition definition;
		for(Map<String, Object> row : report) {
			if(row.containsKey(ReportModule.DEFINITION_ID)) {
				definition = definitionMap.get(row.get(ReportModule.DEFINITION_ID));
				row.put(ReportModule.DEFINITION_ID, definition.getTitle());
				if(row.containsKey(ReportModule.STATE)) {
					row.put(ReportModule.STATE, WorkflowUtils.getStateCaption(definition, (String) row.get(ReportModule.STATE)));
				}
			}
			for(int i = 0; i < columns.length; i++) {
				String name = columns[i];
				if(!isUserColumn(name) || hasUsers) {
					if(i > 0) {
						out.print(",");
					}
				}
				if(! isUserColumn(name)) {
					if(row.containsKey(name)) {
						if(row.get(name) instanceof Date) {
							out.print("\"" + dateFormat.format((Date) row.get(name)) +"\"");
						} else {
							out.print(row.get(name));
						}
					}
				} else if(hasUsers) {
					Long userId = (Long) row.get(name);
					Principal user = userMap.get(userId);
					if(user != null) {
						out.print(user.getTitle() + " (" + user.getName() + ")");
					}
				}
			}
			out.println();
		}
		out.flush();
	}
}
