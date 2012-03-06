/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.search;

public class Constants {

	public static final String FIELD_NAME_ATTRIBUTE = "fieldname";
	public static final String EXACT_PHRASE_ATTRIBUTE = "exactphrase";
	public static final String INCLUSIVE_ATTRIBUTE = "inclusive";
	public static final String DISTANCE_ATTRIBUTE = "distance";
	public static final String ASCENDING_ATTRIBUTE = "ascending";
	public static final String TAG_NAME_ATTRIBUTE = "tagname";
	public static final String LANGUAGE_ATTRIBUTE = "language";
	public static final String VALUE_TYPE_ATTRIBUTE = "valuetype";
	public static final String QUERY_ELEMENT = "QUERY";
	public static final String AND_ELEMENT = "AND";
	public static final String OR_ELEMENT = "OR";
	public static final String NOT_ELEMENT = "NOT";
	public static final String SORTBY_ELEMENT = "SORTBY";
	public static final String RANGE_ELEMENT = "RANGE";
	public static final String RANGE_START = "START";
	public static final String RANGE_FINISH = "FINISH";
	public static final String PERSONALTAGS_ELEMENT = "PERSONALTAGS";
	public static final String FIELD_ELEMENT = "FIELD";
	public static final String FIELD_TERMS_ELEMENT = "TERMS";
	public static final String LANGUAGE_ELEMENT = "LANGUAGE";
	public static final String ASCENDING_TRUE = "TRUE";
	public static final String INCLUSIVE_TRUE = "TRUE";
	public static final String EXACT_PHRASE_TRUE = "true";
	public static final String EXACT_PHRASE_FALSE = "false";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String TAG_ELEMENT = "TAG";
	public static final String SORT_FIELD_PREFIX = "_sort_";
	public static final String UID_FIELD = "_uid";
	public static final String DOC_TYPE_FIELD = "_docType";
	public static final String PRE_DELETED_FIELD = "_preDeleted";
	public static final String PRE_DELETED_BY_ID_FIELD = "_preDeletedById";
	public static final String PRE_DELETED_BY_TITLE_FIELD = "_preDeletedByTitle";
	public static final String PRE_DELETED_WHEN_FIELD = "_preDeletedWhen";
	public static final String PRE_DELETED_FROM_FIELD = "_preDeletedFrom";
	public static final String ATTACHMENT_TYPE_FIELD = "_attType";
	public static final String THIS_CLASS_FIELD = "_class";
	public static final String ALL_TEXT_FIELD = "_allText";
	public static final String TEMP_FILE_CONTENTS_FIELD = "_fileContents";
	public static final String ENTRY_ACL_FIELD = "_entryAcl";
	public static final String FOLDER_ACL_FIELD = "_folderAcl";
	public static final String ENTRY_CONDITION_ACL_FIELD = "_entryConditionAcl";
	public static final String FOLDER_CONDITION_ACL_FIELD = "_folderConditionAcl";
	public static final String CONDITION_ACL_PREFIX = "c";
	public static final String ENTITY_TYPE_FOLDER = "folder";
	public static final String ENTITY_TYPE_FOLDER_ENTRY = "folderEntry";
	public static final String BINDER_OWNER_ACL_FIELD = "_bOwnerAcl";
	public static final String ENTRY_OWNER_ACL_FIELD = "_eOwnerAcl";
	public static final String TEAM_ACL_FIELD = "_teamAcl";
	public static final String TAG_FIELD = "_tagField";
	public static final String ACL_TAG_FIELD = "_aclTagField";
	public static final String TAG_FIELD_TTF = "_tagField_ttf";
	public static final String ACL_TAG_FIELD_TTF = "_aclTagField_ttf";
	public static final String READ_ACL_ALL = "all";
	public static final String READ_ACL_GLOBAL = "global";
	public static final String READ_ACL_ALL_USERS = "allUsers";
	public static final String READ_ACL_TEAM = "team";
	public static final String READ_ACL_BINDER_OWNER = "own";
	public static final String TAG = "TAG";
	public static final String TAG_ACL_PRE = "ACL";
	public static final String GROUP_ANY = "any";
	public static final String EMPTY_ACL_FIELD = "xx";
	public static final String DOC_TYPE_BINDER = "binder";
	public static final String DOC_TYPE_ENTRY = "entry";
	public static final String DOC_TYPE_ATTACHMENT = "attachment";
	public static final String ATTACHMENT_TYPE_BINDER = "binder";
	public static final String ATTACHMENT_TYPE_ENTRY = "entry";
	public static final String ENTRY_TYPE_FIELD = "_entryType";
	public static final String ENTRY_TYPE_ENTRY = "entry";
	public static final String ENTRY_TYPE_REPLY = "reply";
	public static final String ENTRY_TYPE_USER = "user";
	public static final String ENTRY_TYPE_GROUP = "group";
	public static final String ENTRY_TYPE_APPLICATION = "application";
	public static final String ENTRY_TYPE_APPLICATION_GROUP = "applicationGroup";
	public static final String ENTITY_PATH = "_entityPath";
	public static final String ENTITY_FIELD = "_entityType";
	public static final String DOCID_FIELD = "_docId";
	public static final String ENTRY_ANCESTRY = "_entryAncestry";
	public static final String CREATION_DATE_FIELD = "_creationDate";
	public static final String CREATION_DAY_FIELD = "_creationDay";
	public static final String CREATION_YEAR_MONTH_FIELD = "_creationYearMonth";
	public static final String CREATION_YEAR_FIELD = "_creationYear";
	public static final String MODIFICATION_DATE_FIELD = "_modificationDate";
	public static final String MODIFICATION_DAY_FIELD = "_modificationDay";
	public static final String MODIFICATION_YEAR_MONTH_FIELD = "_modificationYearMonth";
	public static final String MODIFICATION_YEAR_FIELD = "_modificationYear";
	public static final String CREATORID_FIELD = "_creatorId";
	public static final String CREATOR_NAME_FIELD = "_creatorName";
	public static final String CREATOR_TITLE_FIELD = "_creatorTitle";
	public static final String SORT_CREATOR_TITLE_FIELD = "_sortCreatorTitle";
	public static final String MODIFICATIONID_FIELD = "_modificationId";
	public static final String MODIFICATION_NAME_FIELD = "_modificationName";
	public static final String MODIFICATION_TITLE_FIELD = "_modificationTitle";
	public static final String RESERVEDBY_ID_FIELD = "_reservedbyId";
	public static final String OWNERID_FIELD = "_ownerId";
	public static final String OWNER_NAME_FIELD = "_ownerName";
	public static final String OWNER_TITLE_FIELD = "_ownerTitle";
	public static final String COMMAND_DEFINITION_FIELD = "_commandDef";
	public static final String CREATED_WITH_DEFINITION_FIELD = "_createdWithDef";
	public static final String ENTRY_DEFINITIONS_FIELD = "_entryDefs";
	public static final String TITLE_FIELD = "title"; // all definable entities
	public static final String SORT_TITLE_FIELD = "_sortTitle"; // all definable entities
	public static final String BINDER_SORT_TITLE_FIELD = "_bsortTitle"; // binders only
	public static final String NORM_TITLE = "_normTitle"; // binders only
	public static final String NORM_TITLE_FIELD = "_normTitleField"; // binders only
	public static final String TITLE1_FIELD = "_title1"; // all definable entities
	public static final String EXTENDED_TITLE_FIELD = "_extendedTitle"; // binders only
	public static final String DESC_FIELD = "_desc"; // Lucene stored field for description element
	public static final String DESC_TEXT_FIELD = "description"; // Lucene indexed field for description element
	public static final String DESC_FORMAT_FIELD = "_desc_format";
	public static final String EVENT_FIELD = "_event";
	public static final String EVENT_FIELD_START_DATE = "StartDate";
	public static final String EVENT_FIELD_START_END = "start_end";
	public static final String EVENT_FIELD_CALC_START_DATE = "CalcStartDate";
	public static final String EVENT_FIELD_LOGICAL_START_DATE = "LogicalStartDate";
	public static final String EVENT_FIELD_TIME_ZONE_ID = "TimeZoneID";
	public static final String EVENT_FIELD_TIME_ZONE_SENSITIVE = "TimeZoneSensitive";
	public static final String EVENT_FIELD_FREE_BUSY = "FreeBusy";
	public static final String EVENT_FIELD_END_DATE = "EndDate";
	public static final String EVENT_FIELD_CALC_END_DATE = "CalcEndDate";
	public static final String EVENT_FIELD_LOGICAL_END_DATE = "LogicalEndDate";
	public static final String EVENT_COUNT_FIELD = "_eventCount";
	public static final String EVENT_DATES_FIELD = "_eventDates";
	public static final String EVENT_RECURRENCE_DATES_FIELD = "RecurrenceDates";
	public static final String EVENT_ID = "ID";
	public static final String EVENT_DATES = "EventDates";
	public static final String EVENT_FIELD_DURATION   = "Duration";
	public static final String DURATION_FIELD_SECONDS = "S";
	public static final String DURATION_FIELD_MINUTES = "M";
	public static final String DURATION_FIELD_HOURS   = "H";
	public static final String DURATION_FIELD_DAYS    = "D";
	public static final String DURATION_FIELD_WEEKS   = "W";
	public static final String WORKFLOW_PROCESS_FIELD = "_workflowProcess";
	public static final String WORKFLOW_STATE_FIELD = "_workflowState";
	public static final String WORKFLOW_STATE_CAPTION_FIELD = "_workflowStateCaption";
	public static final String BINDER_ID_FIELD = "_binderId";
	public static final String ZONE_UUID_FIELD = "_zoneUUID";
	public static final String BINDERS_PARENT_ID_FIELD = "_binderParentId";
	public static final String ENTRY_PARENT_ID_FIELD = "_entryParentId";
	public static final String ENTRY_TOP_ENTRY_ID_FIELD = "_entryTopEntryId";
	public static final String ENTRY_TOP_ENTRY_TITLE_FIELD = "_entryTopEntryTitle";
	public static final String FILENAME_FIELD = "_fileName";
	public static final String FILE_DESCRIPTION_FIELD = "_fileDesc";
	public static final String FILE_STATUS_FIELD = "_fileStatus";
	public static final String FILE_EXT_FIELD = "_fileExt";
	public static final String FILE_TYPE_FIELD = "_fileType";
	public static final String FILE_ID_FIELD = "_fileID";
	public static final String FILE_CREATOR_ID_FIELD = "_fileCreatorId";
	public static final String FILE_SIZE_FIELD = "_fileSize";
	public static final String FILE_TIME_FIELD = "_fileTime";
	public static final String FILENAME_AND_ID_FIELD = "_fileNameId";
	public static final String FILE_SIZE_AND_ID_FIELD = "_fileSizeId";
	public static final String FILE_TIME_AND_ID_FIELD = "_fileTimeId";
	public static final String FILE_UNIQUE_FIELD = "_fileNameUnique";
	public static final String RATING_FIELD = "_rating";
	public static final String DEFINITION_TYPE_FIELD = "_definitionType";
	public static final String FAMILY_FIELD = "_family";
	public static final String FAMILY_FIELD_TASK = "task";
	public static final String FAMILY_FIELD_CALENDAR = "calendar";
	public static final String FAMILY_FIELD_FILE = "file";
	public static final String FAMILY_FIELD_MILESTONE = "milestone";
	public static final String FAMILY_FIELD_MINIBLOG = "miniblog"; 
	public static final String FAMILY_FIELD_PHOTO = "photo";
	public static final String IS_TEAM_FIELD = "_isTeam";
	public static final String TEAM_MEMBERS_FIELD = "_teamMembers";
	public static final String DOCNUMBER_FIELD = "_docNum";
	public static final String SORTNUMBER_FIELD = "_sortNum";
	public static final String TOP_FOLDERID_FIELD = "_topFolderId";
	public static final String RESERVEDBYID_FIELD = "_reservedById";
	public static final String LASTACTIVITY_FIELD = "_lastActivity";
	public static final String LASTACTIVITY_DAY_FIELD = "_lastActivityDay";
	public static final String LASTACTIVITY_YEAR_MONTH_FIELD = "_lastActivityYearMonth";
	public static final String LASTACTIVITY_YEAR_FIELD = "_lastActivityYear";
	public static final String TOTALREPLYCOUNT_FIELD = "_totalReplyCount";
	public static final String LOGINNAME_FIELD = "_loginName";
	public static final String GROUPNAME_FIELD = "_groupName";
	public static final String APPLICATION_NAME_FIELD = "_applicationName";
	public static final String APPLICATION_GROUPNAME_FIELD = "_applicationGroupName";
	public static final String EMAIL_FIELD = "emailAddress";
	public static final String RESERVEDID_FIELD = "_reservedId";
	public static final String WORKSPACE_ID_FIELD = "_workspaceId";
	public static final String UNIQUE_PREFIX = "X_Z_YY_Z";
	public static final String PERSONFLAG_FIELD = "_isPerson";
	public static final String IS_LIBRARY_FIELD = "_isLibrary";
	public static final String IS_MIRRORED_FIELD = "_isMirrored";
	public static final String TASK_COMPLETED_DATE_FIELD = "_taskCompleted";
	public static final String IS_GROUP_DYNAMIC_FIELD = "_isGroupDynamic";

	public Constants() {
		super();
	}

}