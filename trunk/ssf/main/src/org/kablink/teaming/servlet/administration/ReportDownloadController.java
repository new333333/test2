/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.text.SimpleDateFormat;
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

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DeletedBinder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * ?
 *  
 * @author ?
 */
@SuppressWarnings("unchecked")
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
		columnNames.put(ReportModule.USER_TYPE, "report.columns.userType");
		columnNames.put(ReportModule.SHARE_ROLE, "report.columns.shareRole");
		columnNames.put(ReportModule.SHARE_RECIPIENT_ID, "report.columns.shareRecipientId");
		columnNames.put(ReportModule.SHARE_RECIPIENT_TYPE, "report.columns.shareRecipientType");
		columnNames.put(ReportModule.USER_TYPE, "report.columns.userType");
		columnNames.put(AuditType.add.name(), "report.columns.add");
		columnNames.put(AuditType.view.name(), "report.columns.view");
		columnNames.put(AuditType.modify.name(), "report.columns.modify");
		columnNames.put(AuditType.rename.name(), "report.columns.rename");
		columnNames.put(AuditType.delete.name(), "report.columns.delete");
		columnNames.put(AuditType.preDelete.name(), "report.columns.preDelete");
		columnNames.put(AuditType.restore.name(), "report.columns.restore");
		columnNames.put(AuditType.acl.name(), "report.columns.acl");
		columnNames.put(AuditType.restore.name(), "report.columns.restore");
		columnNames.put(AuditType.shareAdd.name(), "report.columns.shareAdd");
		columnNames.put(AuditType.shareModify.name(), "report.columns.shareModify");
		columnNames.put(AuditType.shareDelete.name(), "report.columns.shareDelete");
		columnNames.put(ReportModule.LOGIN_COUNT, "report.columns.login_count");
		columnNames.put(ReportModule.LAST_LOGIN, "report.columns.last_login");
		columnNames.put(ReportModule.LOGIN_DATE, "report.columns.login_date");
		columnNames.put(ReportModule.LOGIN_CLIENT_ADDR, "report.columns.login_client_addr");
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
		return (column.equals(ReportModule.USER_ID));
	}
	
	static private boolean isRecipientColumn(String column) {
		return (column.equals(ReportModule.SHARE_RECIPIENT_ID));
	}
	
	static private boolean isRecipientTypeUser(String type) {
		return (type.equals(ShareItem.RecipientType.user.name()) || type.equals(ShareItem.RecipientType.group.name()));
	}
	
	static private boolean isUserTypeColumn(String column) {
		return column.equals(ReportModule.USER_TYPE);
	}
	
	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	@Override
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {	
		try {
			return _handleRequestAfterValidation(request, response);
		}
		catch(Exception e) {
			logger.warn("Error generating report", e);
			throw e;
		}
	}
	
	private ModelAndView _handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		
		Map formData = request.getParameterMap();
		MapInputData inputData = new MapInputData(formData);
		
		Date startDate;
		String dateStr = ServletRequestUtils.getStringParameter(request, WebKeys.URL_START_DATE_YYYYMMDD, "");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		if (MiscUtil.hasString(dateStr))
		     startDate = formatter.parse(dateStr);
		else startDate = inputData.getDateValue(WebKeys.URL_START_DATE);
		
		Date endDate;
		dateStr = ServletRequestUtils.getStringParameter(request, WebKeys.URL_END_DATE_YYYYMMDD, "");
		if (MiscUtil.hasString(dateStr))
		     endDate = formatter.parse(dateStr);
		else endDate = inputData.getDateValue(WebKeys.URL_END_DATE);
		
        String sortType   = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_SORT_TYPE,   "");
        String sortType2  = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_SORT_TYPE_2, "");
        String optionType = ServletRequestUtils.getStringParameter(request, WebKeys.URL_REPORT_OPTION_TYPE, "");
        
        Set memberIds = new HashSet();
        if (formData.containsKey("users")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
        
		if(endDate != null) {
			GregorianCalendar cal = new GregorianCalendar();
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
			//Write out the BOM so Excel knows how to handle double byte characters properly.
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(0xEF);   // 1st byte of BOM
			outputStream.write(0xBB);
			outputStream.write(0xBF);   // last byte of BOM

			String reportType = ServletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_REPORT_TYPE);
			String[] columns = null;
			List<Map<String, Object>> report = null;
			boolean hasUsers = true;
			if("binder".equals(reportType)) {
				//Get the list of binders for reporting
				String byUsers = ServletRequestUtils.getStringParameter(request, WebKeys.URL_BY_USER, "");
				boolean teamMembers = false;
				boolean allUsers = false;
				if (byUsers.equals(WebKeys.URL_BY_TEAM_MEMBERS)) teamMembers = true;
				if (byUsers.equals(WebKeys.URL_BY_ALL_USERS)) allUsers = true;
				if (!teamMembers && !allUsers) hasUsers = false;
				Collection<Long> ids = TreeHelper.getSelectedIds(formData);

				report = getReportModule().generateReport(ids, teamMembers, allUsers, startDate, endDate);
				columns = new String[]
				                     {
										ReportModule.BINDER_ID,
										ReportModule.BINDER_PARENT,
										ReportModule.BINDER_TITLE,
										ReportModule.USER_ID,
										AuditType.view.name(),
										AuditType.add.name(),
										AuditType.modify.name(),
										AuditType.delete.name(),
										AuditType.preDelete.name(),
										AuditType.restore.name()};
			} else if ("login".equals(reportType)) {
				report = getReportModule().generateLoginReport(startDate, endDate, optionType, 
						sortType, sortType2, memberIds);
				
				if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_SHORT))
					columns = new String[] {ReportModule.USER_ID, ReportModule.USER_TYPE, ReportModule.LOGIN_COUNT, ReportModule.LAST_LOGIN};
				else if(optionType.equals(WebKeys.URL_REPORT_OPTION_TYPE_LONG))
					columns = new String[] {ReportModule.USER_ID, ReportModule.USER_TYPE, ReportModule.LOGIN_DATE, ReportModule.LOGIN_CLIENT_ADDR};
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
				columns = new String[] {ReportModule.USER_ID, AuditType.view.name(), AuditType.add.name(),
						AuditType.modify.name(), AuditType.delete.name()};
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
				//Skip the file sync agent since this could contain millions of entries
				Set userIdsToSkip = new HashSet();
				User fsa = getProfileModule().getReservedUser(ObjectKeys.FILE_SYNC_AGENT_INTERNALID);
				if (fsa != null) userIdsToSkip.add(fsa.getId());
				report = getReportModule().generateActivityReportByUser(memberIds, userIdsToSkip, startDate, endDate, type);
				if (type.equals(ReportModule.REPORT_TYPE_SUMMARY)) {
					columns = new String[] {ReportModule.USER_ID, 
							AuditType.view.name(), 
							AuditType.add.name(),
							AuditType.modify.name(), 
							AuditType.rename.name(), 
							AuditType.delete.name(),
							AuditType.preDelete.name(),
							AuditType.restore.name(),
							AuditType.acl.name(),
							AuditType.shareAdd.name(),
							AuditType.shareModify.name(),
							AuditType.shareDelete.name()};
				} else {
					columns = new String[] {ReportModule.USER_ID, 
							ReportModule.ACTIVITY_TYPE, 
							ReportModule.COUNT,
							ReportModule.ACTIVITY_DATE, 
							ReportModule.FOLDER, 
							ReportModule.ENTRY_TITLE,
							ReportModule.ENTITY,
							ReportModule.SHARE_RECIPIENT_ID,
							ReportModule.SHARE_RECIPIENT_TYPE,
							ReportModule.SHARE_ROLE};
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
		//Set a maximum size for reports
		int maxRows = SPropsUtil.getInt("reports.max.size", 10000);
		if (report.size() > maxRows) {
			//Trim the list to show the last entries in the list
			report = report.subList(report.size()-maxRows, report.size());
		}
		HashMap<Long,Principal> userMap = new HashMap<Long,Principal>();
		HashMap<String,Definition> definitionMap = new HashMap<String, Definition>();
		HashMap<Long,Binder> binderMap = new HashMap<Long, Binder>();
		HashMap<Long,FolderEntry> entryMap = new HashMap<Long, FolderEntry>();
		HashMap<Long,String> deletedBinderTitles = new HashMap<Long,String>();
		HashMap<Long,String> deletedBinderPaths = new HashMap<Long,String>();
		HashMap<Long,String> deletedEntryTitles = new HashMap<Long,String>();
		HashMap<Long,String> sharedEntryTitles = new HashMap<Long,String>();
        User requestor = RequestContextHolder.getRequestContext().getUser();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, requestor.getLocale());
        dateFormat.setTimeZone(requestor.getTimeZone());
        
		HashSet userIds = new HashSet();
		HashSet userTypes = new HashSet();
		HashSet<String> definitionIds = new HashSet<String>();
		HashSet<Long> binderIds = new HashSet<Long>();
		HashSet<Long> entryIds = new HashSet<Long>();
		for(Map<String, Object> row : report) {
			if(row.containsKey(ReportModule.USER_ID)) {
				userIds.add(row.get(ReportModule.USER_ID));
			}
			if (row.containsKey(ReportModule.SHARE_RECIPIENT_ID) && row.containsKey(ReportModule.SHARE_RECIPIENT_TYPE)) {
				ShareItem.RecipientType recipientType = ShareItem.RecipientType.valueOf((String)row.get(ReportModule.SHARE_RECIPIENT_TYPE));
				if (ShareItem.RecipientType.user.equals(recipientType) || ShareItem.RecipientType.group.equals(recipientType)) {
					userIds.add(row.get(ReportModule.SHARE_RECIPIENT_ID));
				}
			}
			if(row.containsKey(ReportModule.USER_TYPE)) {
				userTypes.add(row.get(ReportModule.USER_TYPE));
			}
			if(row.containsKey(ReportModule.DEFINITION_ID)) {
				definitionIds.add((String) row.get(ReportModule.DEFINITION_ID));
			}
			if (row.containsKey(ReportModule.BINDER_ID)) {
				binderIds.add((Long)row.get(ReportModule.BINDER_ID));
			}
			if (row.containsKey(ReportModule.SHARE_BINDER_ID)) {
				binderIds.add((Long)row.get(ReportModule.SHARE_BINDER_ID));
			}
			if(row.containsKey(ReportModule.ENTRY_ID) && row.containsKey(ReportModule.ENTITY)) {
				if (row.get(ReportModule.ENTITY).equals("folderEntry")) {
					entryIds.add((Long)row.get(ReportModule.ENTRY_ID));
					if (row.containsKey(ReportModule.ENTRY_TITLE) && !"".equals((String) row.get(ReportModule.ENTRY_TITLE))) {
						sharedEntryTitles.put((Long)row.get(ReportModule.ENTRY_ID), (String) row.get(ReportModule.ENTRY_TITLE));
					}
				}
			}
			if (row.containsKey(ReportModule.DESCRIPTION) && row.get(ReportModule.DESCRIPTION) != null && !"".equals(row.get(ReportModule.DESCRIPTION))) {
				if (row.get(ReportModule.ENTITY).equals("folderEntry")) {
					deletedEntryTitles.put((Long)row.get(ReportModule.ENTRY_ID), (String)row.get(ReportModule.DESCRIPTION));
				} else if (row.get(ReportModule.ENTITY).equals("folder") || row.get(ReportModule.ENTITY).equals("workspace")) {
					String description = (String)row.get(ReportModule.DESCRIPTION);
					String title = description;
					String path = description;
					if (description.contains("/")) {
						title = title.substring(title.lastIndexOf("/")+1, title.length());
					} else {
						path = "";
					}
					deletedBinderTitles.put((Long)row.get(ReportModule.BINDER_ID), title);
					deletedBinderPaths.put((Long)row.get(ReportModule.BINDER_ID), path);
				}
			}
		}
		if(userIds.size() > 0) {
			SortedSet<Principal> principals = getProfileModule().getPrincipals(userIds);
			for(Principal p : principals) {
				userMap.put(p.getId(), p);
			}
		}
		if(userTypes.size() > 0 && userIds.size() == 0) {
			SortedSet<Principal> principals = getProfileModule().getPrincipals(userTypes);
			for(Principal p : principals) {
				userMap.put(p.getId(), p);
			}
		}
		if(entryIds.size() > 0) {
			HashSet<Long> deletedEntryIds = new HashSet<Long>();
			deletedEntryIds.addAll(entryIds);
			SortedSet<FolderEntry> entries = getFolderModule().getEntries(entryIds);
			for(FolderEntry fe : entries) {
				entryMap.put(fe.getId(), fe);
				deletedEntryIds.remove(fe.getId());
				binderMap.put(fe.getParentBinder().getId(), fe.getParentBinder());
			}
			//Now get the titles of any deleted entry
			if (deletedEntryIds.size() > 0) {
				for (Long id : deletedEntryIds) {
					if (sharedEntryTitles.containsKey(id)) {
						if (!deletedEntryTitles.containsKey(id) || !"".equals(deletedEntryTitles.get(id))) {
							deletedEntryTitles.put(id, sharedEntryTitles.get(id));
						}
					}
				}
				deletedEntryIds.removeAll(sharedEntryTitles.keySet());
			}
		}
		if(binderIds.size() > 0) {
			HashSet<Long> deletedBinderIds = new HashSet<Long>();
			deletedBinderIds.addAll(binderIds);
			SortedSet<Binder> binders = getBinderModule().getBinders(binderIds);
			for(Binder b : binders) {
				binderMap.put(b.getId(), b);
				deletedBinderIds.remove(b.getId());
			}
			//Now get the titles of any deleted binder
			if (deletedBinderIds.size() > 0) {
				while (!deletedBinderIds.isEmpty()) {
					HashSet<Long> nextDeletedBinderIds = new HashSet<Long>();
					int i = 0;
					for (Long id : deletedBinderIds) {
						nextDeletedBinderIds.add(id);
						i++;
						if (i >= 1000) break;
					}
					deletedBinderIds.removeAll(nextDeletedBinderIds);
					List<DeletedBinder> deletedBinderInfo = getReportModule().getDeletedBinderInfo(nextDeletedBinderIds);
					for (DeletedBinder binderInfo : deletedBinderInfo) {
						try {
							Long binderId = binderInfo.getBinderId();
							String path = binderInfo.getBinderPath();
							String title = "";
							if (path != null && path.contains("/")) {
								title = path.substring(path.lastIndexOf("/")+1, path.length());
							}
							if (!deletedBinderTitles.containsKey(binderId) || !title.equals("")) {
								deletedBinderTitles.put(binderId, title);
							}
							if (!deletedBinderPaths.containsKey(binderId) || !path.equals("")) {
								deletedBinderPaths.put(binderId, path);
							}
						} catch(Exception e) {
							e.getMessage();
						}
					}
				}
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
			if((!isUserColumn(name) && !isUserTypeColumn(name)) || hasUsers) {
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
			String eventType = (String) row.get(ReportModule.ACTIVITY_TYPE);
			if (row.containsKey(ReportModule.DEFINITION_ID)) {
				definition = definitionMap.get(row.get(ReportModule.DEFINITION_ID));
				row.put(ReportModule.DEFINITION_ID, definition.getTitle());
				if(row.containsKey(ReportModule.STATE)) {
					row.put(ReportModule.STATE, WorkflowUtils.getStateCaption(definition, (String) row.get(ReportModule.STATE)));
				}
			}
			Binder binder;
			if (row.containsKey(ReportModule.BINDER_ID) || row.containsKey(ReportModule.SHARE_BINDER_ID)) {
				if (row.containsKey(ReportModule.BINDER_ID)) {
					binder = binderMap.get(row.get(ReportModule.BINDER_ID));
				} else {
					binder = binderMap.get(row.get(ReportModule.SHARE_BINDER_ID));
				}
				try {
					if (binder != null) {
						row.put(ReportModule.FOLDER, binder.getPathName());
						String description = (String)row.get(ReportModule.DESCRIPTION);
						if (AuditType.rename.name().equals(eventType) && description != null && !description.equals("")) {
							String[] titles = new String[2];
							titles[0] = binder.getTitle();
							titles[1] = description;
							row.put(ReportModule.ENTRY_TITLE,  NLT.get("report.renamedEntity", titles));
						} else {
							row.put(ReportModule.ENTRY_TITLE, binder.getTitle());
						}
					} else {
						if (deletedBinderPaths.containsKey(row.get(ReportModule.BINDER_ID))) {
							row.put(ReportModule.FOLDER, deletedBinderPaths.get(row.get(ReportModule.BINDER_ID)));
						}
						if (deletedBinderTitles.containsKey(row.get(ReportModule.BINDER_ID))) {
							row.put(ReportModule.ENTRY_TITLE, deletedBinderTitles.get(row.get(ReportModule.BINDER_ID)));
						}
					}
				} catch(Exception e) {}
			}
			FolderEntry entry;
			if (row.containsKey(ReportModule.ENTRY_ID) && 
					row.containsKey(ReportModule.ENTITY) && 
					row.get(ReportModule.ENTITY).equals("folderEntry")) {
				entry = entryMap.get(row.get(ReportModule.ENTRY_ID));
				try {
					if (entry != null) {
						String description = (String)row.get(ReportModule.DESCRIPTION);
						if (AuditType.rename.name().equals(eventType) && description != null && !description.equals("")) {
							String[] titles = new String[2];
							titles[0] = entry.getTitle();
							titles[1] = description;
							row.put(ReportModule.ENTRY_TITLE,  NLT.get("report.renamedEntity", titles));
						} else {
							row.put(ReportModule.ENTRY_TITLE, entry.getTitle());
						}
						
					} else if (deletedEntryTitles.containsKey(row.get(ReportModule.ENTRY_ID))) {
						row.put(ReportModule.ENTRY_TITLE, deletedEntryTitles.get(row.get(ReportModule.ENTRY_ID)));
					}
				} catch(Exception e) {}
			}
			for(int i = 0; i < columns.length; i++) {
				String name = columns[i];
				if ((!isUserColumn(name) && !isUserTypeColumn(name)) || hasUsers) {
					if(i > 0) {
						out.write(",".getBytes());
					}
				}
				if (!isUserColumn(name) && !isUserTypeColumn(name) && !isRecipientColumn(name)) {
					if (row.containsKey(name)) {
						String colValue;
						
						// Get the value for this column.
						if ( row.get(name) instanceof Date ) {
							colValue = dateFormat.format( (Date) row.get(name) );
						} else {
							colValue = row.get(name).toString();
						}
						
						//Translate the "type"
						if (name.equals(ReportModule.ACTIVITY_TYPE)) {
							colValue = NLT.get("report.type."+colValue, colValue);
						} else if (name.equals(ReportModule.ENTITY)) {
							colValue = NLT.get("report.entity."+colValue, colValue);
						} else if (name.equals(ReportModule.SHARE_RECIPIENT_TYPE)) {
							colValue = NLT.get("report.type."+colValue, colValue);
						} else if (name.equals(ReportModule.SHARE_ROLE)) {
							colValue = NLT.get("report.rolename."+colValue, colValue);
						}

						// Does the value for this column have a ',' in it?
						indexOfComma = colValue.indexOf( ',' ); 
						if ( indexOfComma >= 0 ) {
							// Yes, enclose the value in quotes.
							out.write( doubleQuote );
						}
						
						out.write( colValue.getBytes() );

						// Does the value for this column have a ',' in it?
						if ( indexOfComma >= 0 ) {
							// Yes, enclose the user's name in quotes.
							out.write( doubleQuote );
						}
					}
				} else if (isUserColumn(name) && hasUsers && row.containsKey(name)) {
					Long userId = (Long) row.get(name);
					Principal user = null;
					if (userId != null) user = userMap.get(userId);
					if(user != null) {
						String userName;
						
						// Does the user's name have a ',' in it?
						String userTitle = Utils.getUserTitle(user);
						if (userTitle.trim().equals("")) userTitle = user.getName();
						userName = userTitle + " (" + user.getName() + ")";
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
				} else if (isRecipientColumn(name) && hasUsers && row.containsKey(name)) {
					Long userId = (Long) row.get(ReportModule.SHARE_RECIPIENT_ID);
					String recipientType = (String) row.get(ReportModule.SHARE_RECIPIENT_TYPE);
					String recipientTypeNLT = NLT.get("report.type."+recipientType, recipientType);
					if (isRecipientTypeUser(recipientType)) {
						Principal user = null;
						if (userId != null) user = userMap.get(userId);
						if(user != null) {
							String userName;
							
							// Does the user's name have a ',' in it?
							String userTitle = Utils.getUserTitle(user);
							if (userTitle.trim().equals("")) userTitle = user.getName();
							userName = userTitle + " (" + user.getName() + ")";
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
					} else {
						//Recipient type is not a user
						out.write(recipientTypeNLT.getBytes());
					}
				} else if (isUserTypeColumn(name) && hasUsers && row.containsKey(name)) {
					Long userId = (Long) row.get(name);
					Principal user = null;
					if (userId != null) user = userMap.get(userId);
					if(user != null) {
						String userType = "";
						if (user.getIdentityInfo().isFromLocal() && user.getIdentityInfo().isInternal()) {
							userType = NLT.get("login.type.local");
						} else if (user.getIdentityInfo().isFromLdap()) {
							userType = NLT.get("login.type.ldap");
						} else if (user.getIdentityInfo().isFromOpenid()) {
							userType = NLT.get("login.type.openId");
						} else if (user.getIdentityInfo().isFromLocal() && !user.getIdentityInfo().isInternal()) {
							userType = NLT.get("login.type.self");
						}
						out.write( userType.getBytes() );

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
