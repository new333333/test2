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
package org.kablink.teaming.servlet.administration;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Collection;
import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuditTrail;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.report.ReportModule.UserQuotaOption;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;


public class ReportDownloadController extends  SAbstractController {
	
	private FileTypeMap mimeTypes;

	static HashMap<String, String> columnNames = null;
	
	static {
		columnNames = new HashMap<String,String>();
		columnNames.put(ReportModule.BINDER_ID, "report.columns.id");
		columnNames.put(ReportModule.BINDER_PARENT, "report.columns.parent");
		columnNames.put(ReportModule.BINDER_TITLE, "report.columns.title");
		columnNames.put(ReportModule.ENTRY_TITLE, "report.columns.entryTitle");
		columnNames.put(ReportModule.ENTITY, "report.columns.entity");
		columnNames.put(ReportModule.ENTITY_PATH, "report.columns.entityPath");
		columnNames.put(ReportModule.ENTITY_TYPE, "report.columns.entity");
		columnNames.put(ReportModule.FOLDER, "report.columns.folder");
		columnNames.put(ReportModule.USER_ID, "report.columns.user");
		columnNames.put(ReportModule.USER_TITLE, "report.columns.user");
		columnNames.put(AuditTrail.AuditType.add.name(), "report.columns.add");
		columnNames.put(AuditTrail.AuditType.view.name(), "report.columns.view");
		columnNames.put(AuditTrail.AuditType.modify.name(), "report.columns.modify");
		columnNames.put(AuditTrail.AuditType.delete.name(), "report.columns.delete");
		columnNames.put(AuditTrail.AuditType.preDelete.name(), "report.columns.preDelete");
		columnNames.put(AuditTrail.AuditType.restore.name(), "report.columns.restore");
		columnNames.put(ReportModule.LOGIN_COUNT, "report.columns.login_count");
		columnNames.put(ReportModule.LAST_LOGIN, "report.columns.last_login");
		columnNames.put(ReportModule.LOGIN_DATE, "report.columns.login_date");
		columnNames.put(ReportModule.STATE, "report.columns.state");
		columnNames.put(ReportModule.DEFINITION_ID, "report.columns.definition");
		columnNames.put(ReportModule.AVERAGE, "report.columns.average");
		columnNames.put(ReportModule.AVERAGE_TI, "report.columns.average_ti");
		columnNames.put(ReportModule.COUNT, "report.columns.count");
		columnNames.put(ReportModule.SIZE, "report.columns.size");
		columnNames.put(ReportModule.ACTIVITY_TYPE, "report.columns.activityType");
		columnNames.put(ReportModule.ACTIVITY_DATE, "report.columns.activityDate");
		columnNames.put(ReportModule.DISK_SPACE_USED, "report.columns.diskspaceused");
		columnNames.put(ReportModule.DISKQUOTA, "report.columns.diskquota");
		columnNames.put(ReportModule.DEFAULT_QUOTA, "report.columns.defaultquota");
		columnNames.put(ReportModule.MAX_GROUPS_QUOTA, "report.columns.maxgroupsquota");
		columnNames.put(ReportModule.CREATIONDATE, "report.columns.creationDate");
	}

	static private boolean isUserColumn(String column) {
		return column.equals(ReportModule.USER_ID);
	}
	
	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Map formData = request.getParameterMap();
		MapInputData inputData = new MapInputData(formData);
		GregorianCalendar cal = new GregorianCalendar();
		Date startDate = inputData.getDateValue(WebKeys.URL_START_DATE);
		Date endDate = inputData.getDateValue(WebKeys.URL_END_DATE);
		
        String sortType = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_SORT_TYPE, "");
        
        String sortType2 = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_SORT_TYPE_2, "");
        
        String optionType = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_OPTION_TYPE, "");
        
        Set memberIds = new HashSet();
        if (formData.containsKey("users")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
        
		if(endDate != null) {
			cal.setTime(endDate);
			cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}
		if (formData.containsKey("forumOkBtn")) {
			response.setContentType(mimeTypes.getContentType("report.csv"));
			response.setHeader("Cache-Control", "private");
			response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"report.csv\"");

			String reportType = ServletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_REPORT_TYPE);
			String[] columns = null;
			List<Map<String, Object>> report = null;
			boolean hasUsers = true;
			if("binder".equals(reportType)) {
				//Get the list of binders for reporting
				hasUsers = ServletRequestUtils.getBooleanParameter(request, WebKeys.URL_BY_USER, false);
				Collection<Long> ids = TreeHelper.getSelectedIds(formData);

				report = getReportModule().generateReport(ids, hasUsers, startDate, endDate);
				columns = new String[]
				                     {
										ReportModule.BINDER_ID,
										ReportModule.BINDER_PARENT,
										ReportModule.BINDER_TITLE,
										ReportModule.USER_ID,
										AuditTrail.AuditType.view.name(),
										AuditTrail.AuditType.add.name(),
										AuditTrail.AuditType.modify.name(),
										AuditTrail.AuditType.delete.name(),
										AuditTrail.AuditType.preDelete.name(),
										AuditTrail.AuditType.restore.name()};
			} else if ("login".equals(reportType)) {
				report = getReportModule().generateLoginReport(startDate, endDate, optionType, 
						sortType, sortType2, memberIds);
				
				if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_SHORT))
					columns = new String[] {ReportModule.USER_ID, ReportModule.LOGIN_COUNT, ReportModule.LAST_LOGIN};
				else if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_LONG))
					columns = new String[] {ReportModule.USER_ID, ReportModule.LOGIN_DATE};
			} else if ("workflow".equals(reportType)) {
				if(ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_FLAVOR, "").equals("averages")) {
					//Get the list of binders for reporting
					Collection<Long> ids = TreeHelper.getSelectedIds(formData);

					report = getReportModule().generateWorkflowStateReport(ids, startDate, endDate);
					columns = new String[] {ReportModule.BINDER_ID, ReportModule.BINDER_PARENT, ReportModule.BINDER_TITLE,
							ReportModule.DEFINITION_ID, ReportModule.STATE, ReportModule.AVERAGE_TI, ReportModule.AVERAGE };
				} else if(ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_FLAVOR, "").equals("current")) {
					//Get the list of binders for reporting
					Collection<Long> ids = TreeHelper.getSelectedIds(formData);

					report = getReportModule().generateWorkflowStateCountReport(ids);
					columns = new String[] {ReportModule.BINDER_ID, ReportModule.BINDER_PARENT, ReportModule.BINDER_TITLE,
							ReportModule.STATE, ReportModule.COUNT };
				}
			} else if ("entry".equals(reportType)) {
				hasUsers = true;
				Long binderId = ServletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				Long entryId = ServletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
				report = getReportModule().generateActivityReport(binderId, entryId);
				columns = new String[] {ReportModule.USER_ID, AuditTrail.AuditType.view.name(), AuditTrail.AuditType.add.name(),
						AuditTrail.AuditType.modify.name(), AuditTrail.AuditType.delete.name()};
			} else if ("quota".equals(reportType)) {
				String quotaOption = ServletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_QUOTA_OPTION);
				Long threshold = null;
				try
				{
					threshold = ServletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_QUOTA_THRESHOLD);
				}
				catch (ServletRequestBindingException srbEx)
				{
					// Nothing to do.
				}
				if ( threshold == null )
					threshold = new Long( 0 );
				
				ReportModule.QuotaOption option = ReportModule.QuotaOption.valueOf(quotaOption);
				report = getReportModule().generateQuotaReport(option, threshold);
				switch(option) {
				case UsersOnly:
					hasUsers = true;
					columns = new String[] {ReportModule.USER_ID, ReportModule.SIZE};
					break;
				case WorkspacesOnly:
					hasUsers = false;
					columns = new String[] {ReportModule.BINDER_ID, ReportModule.BINDER_TITLE, ReportModule.SIZE};
					break;
				case UsersAndWorkspaces:
					hasUsers = true;
					columns = new String[] {ReportModule.USER_ID, ReportModule.BINDER_TITLE, ReportModule.SIZE};
					break;
				}
			} else if ("userDiskUsage".equals(reportType)) {
				String userQuotaOption = ServletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_USER_QUOTA_OPTION);
				ReportModule.UserQuotaOption option = ReportModule.UserQuotaOption.valueOf(userQuotaOption);
				report = getReportModule().generateUserDiskUsageReport(option);
				columns = new String[] {ReportModule.USER_ID, ReportModule.SIZE, ReportModule.CREATIONDATE};
			} else if ("quota_exceeded".equals(reportType)) {
				report = getReportModule().generateExceededDiskQuotaReport();
				columns = new String[] {ReportModule.USER_ID, ReportModule.DISK_SPACE_USED, ReportModule.DISKQUOTA, 
						ReportModule.MAX_GROUPS_QUOTA, ReportModule.DEFAULT_QUOTA};
			} else if ("quota_highwater_exceeded".equals(reportType)) {
				report = getReportModule().generateExceededHighWaterDiskQuotaReport();
				columns = new String[] {ReportModule.USER_ID, ReportModule.DISK_SPACE_USED, ReportModule.DISKQUOTA, 
						ReportModule.MAX_GROUPS_QUOTA, ReportModule.DEFAULT_QUOTA};
			} else if ("activityByUser".equals(reportType)) {
				hasUsers = true;
				String type = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_FLAVOR, ReportModule.REPORT_TYPE_SUMMARY);
				report = getReportModule().generateActivityReportByUser(memberIds, startDate, endDate, type);
				if (type.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
					columns = new String[] {ReportModule.USER_ID, 
							AuditTrail.AuditType.view.name(), 
							AuditTrail.AuditType.add.name(),
							AuditTrail.AuditType.modify.name(), 
							AuditTrail.AuditType.delete.name(),
							AuditTrail.AuditType.preDelete.name(),
							AuditTrail.AuditType.restore.name()};
				} else {
					columns = new String[] {ReportModule.USER_ID, 
							ReportModule.ACTIVITY_TYPE, 
							ReportModule.COUNT,
							ReportModule.ACTIVITY_DATE, 
							ReportModule.FOLDER, 
							ReportModule.ENTRY_TITLE,
							ReportModule.ENTITY};
				}
			} else if ("accessByGuest".equals(reportType)) {
				hasUsers = true;
				String type = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_FLAVOR, ReportModule.REPORT_TYPE_SUMMARY);
		        User guest = getProfileModule().getGuestUser();
		        Long userId = guest.getId();
		        if (!memberIds.isEmpty()) userId = (Long)memberIds.toArray()[0];
				report = getReportModule().generateAccessReportByUser(userId, startDate, endDate, type);
				columns = new String[] {ReportModule.ENTITY_TYPE, 
						ReportModule.BINDER_ID, 
						ReportModule.ENTITY_PATH};
			}
			printReport(response.getOutputStream(), report, columns, hasUsers);
			response.getOutputStream().flush();
		}
		return null;
	}
	
	protected void printReport(OutputStream out, List<Map<String, Object>> report, String[] columns, boolean hasUsers)
	{
		HashMap<Long,Principal> userMap = new HashMap<Long,Principal>();
		HashMap<String,Definition> definitionMap = new HashMap<String, Definition>();
		HashMap<Long,Binder> binderMap = new HashMap<Long, Binder>();
		HashMap<Long,FolderEntry> entryMap = new HashMap<Long, FolderEntry>();
        User requestor = RequestContextHolder.getRequestContext().getUser();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, requestor.getLocale());
        dateFormat.setTimeZone(requestor.getTimeZone());
        
		HashSet userIds = new HashSet();
		HashSet<String> definitionIds = new HashSet<String>();
		HashSet<Long> binderIds = new HashSet<Long>();
		HashSet<Long> entryIds = new HashSet<Long>();
		for(Map<String, Object> row : report) {
			if(row.containsKey(ReportModule.USER_ID)) {
				userIds.add(row.get(ReportModule.USER_ID));
			}
			if(row.containsKey(ReportModule.DEFINITION_ID)) {
				definitionIds.add((String) row.get(ReportModule.DEFINITION_ID));
			}
			if(row.containsKey(ReportModule.BINDER_ID)) {
				binderIds.add((Long)row.get(ReportModule.BINDER_ID));
			}
			if(row.containsKey(ReportModule.ENTRY_ID) && row.containsKey(ReportModule.ENTITY)) {
				if (row.get(ReportModule.ENTITY).equals("folderEntry")) 
					entryIds.add((Long)row.get(ReportModule.ENTRY_ID));
			}
		}
		if(userIds.size() > 0) {
			SortedSet<Principal> principals = getProfileModule().getPrincipals(userIds);
			for(Principal p : principals) {
				userMap.put(p.getId(), p);
			}
		}
		if(binderIds.size() > 0) {
			SortedSet<Binder> binders = getBinderModule().getBinders(binderIds);
			for(Binder b : binders) {
				binderMap.put(b.getId(), b);
			}
		}
		if(entryIds.size() > 0) {
			SortedSet<FolderEntry> entries = getFolderModule().getEntries(entryIds);
			for(FolderEntry fe : entries) {
				entryMap.put(fe.getId(), fe);
			}
		}
		if(definitionIds.size() > 0) {
			for(String id : definitionIds) {
				definitionMap.put(id, getDefinitionModule().getDefinition(id));
			}
		}

	try{
		byte[] doubleQuote = "\"".getBytes();
		int indexOfComma = -1;

		for(int i = 0; i < columns.length; i++) {
			String name = columns[i];
			if(!isUserColumn(name) || hasUsers) {
				String nltKey;
				String columnName;
				
				if(i > 0) {
					out.write(",".getBytes());
				}
				
				// Get the key used to read the column name from the properties file.
				nltKey = columnNames.get( name );
				
				// Do we have a key?
				if ( nltKey == null )
				{
					// No, this should never happen.
					columnName = "???";
				}
				else
					columnName = NLT.get( nltKey );
				
				out.write( columnName.getBytes("UTF-8"));
			}
		}
		out.write("\n".getBytes());

		Definition definition;
		for(Map<String, Object> row : report) {
			if (row.containsKey(ReportModule.DEFINITION_ID)) {
				definition = definitionMap.get(row.get(ReportModule.DEFINITION_ID));
				row.put(ReportModule.DEFINITION_ID, definition.getTitle());
				if(row.containsKey(ReportModule.STATE)) {
					row.put(ReportModule.STATE, WorkflowUtils.getStateCaption(definition, (String) row.get(ReportModule.STATE)));
				}
			}
			Binder binder;
			if (row.containsKey(ReportModule.BINDER_ID)) {
				binder = binderMap.get(row.get(ReportModule.BINDER_ID));
				try {
					if (binder != null) row.put(ReportModule.FOLDER, binder.getPathName());
				} catch(Exception e) {}
			}
			FolderEntry entry;
			if (row.containsKey(ReportModule.ENTRY_ID) && 
					row.containsKey(ReportModule.ENTITY) && 
					row.get(ReportModule.ENTITY).equals("folderEntry")) {
				entry = entryMap.get(row.get(ReportModule.ENTRY_ID));
				try {
					if (entry != null) row.put(ReportModule.ENTRY_TITLE, entry.getTitle());
				} catch(Exception e) {}
			}
			for(int i = 0; i < columns.length; i++) {
				String name = columns[i];
				if (!isUserColumn(name) || hasUsers) {
					if(i > 0) {
						out.write(",".getBytes());
					}
				}
				if (! isUserColumn(name)) {
					if(row.containsKey(name)) {
						String colValue;
						
						// Get the value for this column.
						if( row.get(name) instanceof Date )
						{
							colValue = dateFormat.format( (Date) row.get(name) );
						}
						else
						{
							colValue = row.get(name).toString();
						}

						// Does the value for this column have a ',' in it?
						indexOfComma = colValue.indexOf( ',' ); 
						if ( indexOfComma >= 0 )
						{
							// Yes, enclose the value in quotes.
							out.write( doubleQuote );
						}
						
						out.write( colValue.getBytes() );

						// Does the value for this column have a ',' in it?
						if ( indexOfComma >= 0 )
						{
							// Yes, enclose the user's name in quotes.
							out.write( doubleQuote );
						}
					}
				} else if (hasUsers && row.containsKey(name)) {
					Long userId = (Long) row.get(name);
					Principal user = null;
					if (userId != null) user = userMap.get(userId);
					if(user != null) {
						String userName;
						
						// Does the user's name have a ',' in it?
						userName = user.getTitle() + " (" + user.getName() + ")";
						indexOfComma = userName.indexOf( ',' ); 
						if ( indexOfComma >= 0 )
						{
							// Yes, enclose the user's name in quotes.
							out.write( doubleQuote );
						}

						out.write( userName.getBytes() );

						// Does the user's name have a ',' in it?
						if ( indexOfComma >= 0 )
						{
							// Yes, enclose the user's name in quotes.
							out.write( doubleQuote );
						}
					}
				}
			}
			out.write("\n".getBytes());
		}
		out.flush();
	} catch (IOException ioe) {}
	}
}
