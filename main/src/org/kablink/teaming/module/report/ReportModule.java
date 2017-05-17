/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

import org.kablink.teaming.domain.BasicAudit;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.DeletedBinder;
import org.kablink.teaming.domain.EmailLog;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.AuditType;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public interface ReportModule {
	public static final String BINDER_ID = "binder_id";
	public static final String BINDER_TITLE = "binder_title";
	public static final String BINDER_PARENT = "binder_parent";
	public static final String DATE = "date";
	public static final String DESCRIPTION = "description";
	public static final String ENTRY_ID = "entry_id";
	public static final String ENTRY_TITLE = "entry_title";
	public static final String ENTITY = "entity";
	public static final String ENTITY_ID = "entity_id";
	public static final String ENTITY_TYPE = "entity_type";
	public static final String ENTITY_PATH = "entity_path";
	public static final String ENTITY_PATH_ID = "entity_path_id";
	public static final String ENTITY_TITLE = "entity_title";
	public static final String ENTITY_CREATOR_ID = "entity_creator_id";
	public static final String FILE_ID = "file_id";
	public static final String FOLDER = "folder";
	public static final String HAS_ENTRY_ACL = "hasEntryAcl";
	public static final String CHECK_FOLDER_ACL = "checkFolderAcl";
	public static final String SHARE_RECIPIENT = "share_recipient";
	public static final String SHARE_BINDER_ID = "share_binder_id";
	public static final String SHARE_RECIPIENT_ID = "share_recipient_id";
	public static final String SHARE_RECIPIENT_TYPE = "share_recipient_type";
	public static final String SHARE_ROLE = "share_role";
	public static final String SHARE_ENTRY_TITLE = "share_entry_title";
	public static final String USER = "user";
	public static final String USER_ID = "user_id";
	public static final String USER_TITLE = "user_title";
	public static final String USER_TYPE = "user_type";
	public static final String LAST_LOGIN = "last_login";
	public static final String LOGIN_DATE = "login_date";
	public static final String LOGIN_COUNT = "login_count";
	public static final String LOGIN_CLIENT_ADDR = "login_client_addr";
	public static final String STATE = "state";
	public static final String DEFINITION_ID = "definition_id";
	public static final String AVERAGE = "average";
	public static final String AVERAGE_TI = "average_ti";
	public static final String COUNT = "count";
	public static final String SIZE = "size";
	public static final String TYPE = "type";
	public static final String ACTIVITY_DATE = "activity_date";
	public static final String ACTIVITY_TYPE = "activity_type";
	public static final String ACTIVITY_ENTRY_ID = "activity_entry_id";
	public static final String ACTIVITY_ENTITY_TYPE = "activity_entity_type";
	public static final String DISKQUOTA = "disk_quota";
	public static final String DEFAULT_QUOTA = "default_quota";
	public static final String MAX_GROUPS_QUOTA = "max_groups_quota";
	public static final String DISK_SPACE_USED = "disk_space_used";
	public static final String CREATOR_ID = "creator_id";
	public static final String CREATIONDATE = "creation_date";
	
	public static final Integer ACTIVITY_MERGED_COL_SIZE = 9;
	public static final Integer USER_ID_INDEX = 0;
	public static final Integer LAST_LOGIN_INDEX = 1;
	public static final Integer LOGIN_COUNT_INDEX = 2;
	public static final Integer LOGIN_DATE_INDEX = 1;
	public static final Integer ACTIVITY_TYPE_INDEX = 1;				//This must match SHARE_ACTIVITY_TYPE_INDEX
	public static final Integer ACTIVITY_DATE_INDEX = 2;				//This must match SHARE_ACTIVITY_DATE_INDEX
	public static final Integer ACTIVITY_COUNT_INDEX = 3;				//This must match SHARE_ACTIVITY_COUNT_INDEX
	public static final Integer ACTIVITY_BINDER_ID_INDEX = 4;
	public static final Integer ACTIVITY_ENTRY_ID_INDEX = 5;
	public static final Integer ACTIVITY_ENTITY_TYPE_INDEX = 6;
	public static final Integer ACTIVITY_ENTITY_DESCRIPTION_INDEX = 7;
	
	public static final Integer SHARE_ACTIVITY_TYPE_INDEX = 1;			//This must match ACTIVITY_TYPE_INDEX
	public static final Integer SHARE_ACTIVITY_DATE_INDEX = 2;			//This must match ACTIVITY_DATE_INDEX
	public static final Integer SHARE_ACTIVITY_COUNT_INDEX = 3;			//This must match ACTIVITY_COUNT_INDEX
	public static final Integer SHARE_ACTIVITY_BINDER_ID_INDEX = 4;
	public static final Integer SHARE_ACTIVITY_RECIPIENT_ID_INDEX = 5;
	public static final Integer SHARE_ACTIVITY_RECIPIENT_TYPE_INDEX = 6;
	public static final Integer SHARE_ACTIVITY_ENTITY_ID_INDEX = 7;
	public static final Integer SHARE_ACTIVITY_ENTITY_TYPE_INDEX = 8;
	public static final Integer SHARE_ACTIVITY_ROLE_NAME_INDEX = 9;
	public static final Integer SHARE_ACTIVITY_ENTRY_TITLE_INDEX = 10;
	
	public static final String REPORT_TYPE_SUMMARY = "summary";

	public static final String NONE_SORT = "none_sort";
	public static final String USER_SORT = "user_sort";
	public static final String LAST_LOGIN_SORT = "last_login_sort";
	public static final String LOGIN_COUNT_SORT = "login_count_sort";
	public static final String LOGIN_DATE_SORT = "login_date_sort";
	
	//Email Log Report
	public static final String EMAIL_LOG_SEND_DATE = "sendDate";
	public static final String EMAIL_LOG_FROM_ADDRESS = "from";
	public static final String EMAIL_LOG_SUBJECT = "subj";
	public static final String EMAIL_LOG_COMMENT = "comment";
	public static final String EMAIL_LOG_TYPE = "type";
	public static final String EMAIL_LOG_STATUS = "status";
	public static final String EMAIL_LOG_TO_ADDRESSES = "to_addresses";
	public static final String EMAIL_LOG_ATTACHED_FILES = "attached_files";
	
	public static final Integer EMAIL_LOG_SEND_DATE_INDEX = 0;
	public static final Integer EMAIL_LOG_FROM_ADDRESS_INDEX = 1;
	public static final Integer EMAIL_LOG_SUBJECT_INDEX = 2;
	public static final Integer EMAIL_LOG_COMMENT_INDEX = 3;
	public static final Integer EMAIL_LOG_TYPE_INDEX = 4;
	public static final Integer EMAIL_LOG_STATUS_INDEX = 5;
	public static final Integer EMAIL_LOG_TO_ADDRESSES_INDEX = 6;
	public static final Integer EMAIL_LOG_ATTACHED_FILES_INDEX = 7;
	
	public static final String EMAIL_REPORT_TYPE_SEND = "send";
	public static final String EMAIL_REPORT_TYPE_RECEIVE = "receive";
	public static final String EMAIL_REPORT_TYPE_ERRORS = "errors";
	
	public static final String LOGIN_REPORT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss zzz";	// Used in GwtViewHelper too to extract a user's last login.
	
	public static final String EXTERNAL_USER_ID="userid";
	public static final String EXTERNAL_USER_FIRSTNAME="firstname";
	public static final String EXTERNAL_USER_LASTNAME="lastname";
	public static final String EXTERNAL_USER_EMAIL="emailaddress";
	public static final String EXTERNAL_USER_CREATION_DATE="creationdate";
	public static final String EXTERNAL_USER_TERMS_ACCEPT_DATE="termsacceptancedate";
	
	public static class ActivityInfo
	{
		DefinableEntity whoOrWhat;
		int count;
		Date last;
		
		public ActivityInfo(DefinableEntity whoOrWhat, int count, Date last)
		{
			this.whoOrWhat = whoOrWhat;
			this.count = count;
			this.last = last;
		}
		public DefinableEntity getWhoOrWhat() { return whoOrWhat; }
		public int getCount() { return count; }
		public Date getLast() { return last; }
	};
	
	public enum QuotaOption { UsersOnly, WorkspacesOnly, UsersAndWorkspaces };
	public enum UserQuotaOption {FileLength, Age};
	
	public void addAuditTrail(BasicAudit auditTrail);
	public void addAuditTrail(AuditType type, User user, DefinableEntity entity);
	public void addAuditTrail(AuditType type, DefinableEntity entity);
	public void addAuditTrail(AuditType type, DefinableEntity entity, Date startDate);
	public void addAuditTrail(AuditType type, DefinableEntity entity, Date startDate, String description);
	public void addAuditTrail(AuditType type, DefinableEntity entity, String description);
	public void addLoginInfo(LoginAudit loginInfo);
	public void addStatusInfo(User user);
	public void addFileInfo(AuditType type, FileAttachment attachment);
	public void addFileInfo(AuditType type, FileAttachment attachment, User asUser);
	public void addEmailLog(EmailLog emailLog);

	public void addLicenseStats(LicenseStats stats);
	public LicenseStats getLicenseHighWaterMark(Calendar startDate, Calendar endDate);
	
	public List<Map<String, Object>> generateReport(Collection ids, boolean byTeamMembers, boolean byAllUsers, Date startDate, Date endDate);
	public List<Map<String, Object>> generateActivityReport(final Long binderId, final Long entryId);
	public List<Map<String, Object>> generateActivityReportByUser(final Set<Long> userIds, final Set<Long> userIdsToSkip, final Date startDate, final Date endDate, final String reportType);
	public List<Map<String, Object>> generateExternalUserReport(final Set<Long> userIds, final Set<Long> userIdsToSkip, final Date startDate, final Date endDate);
	public Map<String, Object> generateEntryAclReport(final Folder folder);
	public List<Map<String,Object>> generateLoginReport(Date startDate, Date endDate, String optionType, String sortType, String sortType2, Set memberIds);
	public List<Map<String,Object>> generateWorkflowStateReport(Collection ids, Date startDate, Date endDate);
	public List<Map<String,Object>> generateWorkflowStateCountReport(Collection ids);
	public List<Map<String, Object>> generateQuotaReport(QuotaOption option, Long threshold);
	public List<Map<String, Object>> generateExceededDiskQuotaReport();
	public List<Map<String, Object>> generateExceededHighWaterDiskQuotaReport();
	public List<Map<String, Object>> generateUserDiskUsageReport(UserQuotaOption option);
	public List<Map<String, Object>> generateAccessReportByUser(final Long userId, final Date startDate, final Date endDate, final String reportType);
	public List<Map<String, Object>> generateEmailReport(final Date startDate, final Date endDate, final String reportType);
	public List<Map<String, Object>> generateXssReport(final List binderIds, final Date startDate, final Date endDate, final String reportType);
	
	public List<LicenseStats> generateLicenseReport(Date startDate, Date endDate);
	public List<User> getUsersActivity(DefinableEntity entity, AuditType type, Date startDate, Date endDate);
	public List<Map<String,Object>> getEntriesViewed(Long ownerId, Date startDate, Date endDate, Integer returnCount);
	public Collection<ActivityInfo> getActivity(AuditType limitType, Date startDate, Date endDate, Binder binder);
	public Collection<ActivityInfo> getActivity(AuditType limitType, Date startDate, Date endDate, Short[] entityTypes, Integer returnCount);
	public Collection<ActivityInfo> getActivity(AuditType limitType, Date startDate, Date endDate, Short[] entityTypes, Integer returnCount, Binder binder);
	public List<Map<String,Object>> getUsersStatuses(Long[] userIds, Date startDate, Date endDate, Integer returnCount);
	public List<Long> getDeletedFolderEntryIds(String family, Date startDate, Date endDate);
	public List<Long> getDeletedFolderEntryIds(long[] folderIds, String family, Date startDate, Date endDate);
	public List<Long> getRestoredFolderEntryIds(long[] folderIds, String family, Date startDate, Date endDate);
	public List<Long> getMovedFolderEntryIds(Date startDate, Date endDate);
	public List<DeletedBinder> getDeletedBinderInfo(Set<Long> binderIds);
	public List<ChangeLog> getDeletedBinderLogs(Set<Long> binderIds);
	public List<ChangeLog> getDeletedEntryLogs(Set<Long> entryIds);
}
