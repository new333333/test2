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
package com.sitescape.team;

/**
 * Defines symbols that the core is aware of.
 * 
 * @author Jong Kim
 *
 */
public interface ObjectKeys {
	
    //Default name for the product
	public static final String PRODUCT_NAME_DEFAULT="ICEcore";
	public static final String PRODUCT_TITLE_DEFAULT="ICEcore";
	public static final String PRODUCT_NICKNAME_DEFAULT="ICEcore";
	public static final String PRODUCT_EDITION_DEFAULT="OpenSource";
	public static final String PRODUCT_CONFERENCING_NAME_DEFAULT="Conference";
	public static final String PRODUCT_CONFERENCING_TITLE_DEFAULT="ICEcore Conference";
	
	//Original zoneId for V1 zones
	public static final String DEFAULT_ZONE_ID_FOR_V1="1";
	
	// reserved internalIds for Definitions (not necessarily databaseId)
	public static final String DEFAULT_DISCUSSIONS_WORKSPACE_DEF="402880e619c6250f0119c62794b9000c";
	public static final String DEFAULT_FOLDER_DEF="402883b90cc53079010cc539bf260001";
	public static final String DEFAULT_FOLDER_ENTRY_DEF="402883b90cc53079010cc539bf260002";
	public static final String DEFAULT_WORKSPACE_DEF="402883b90cc53079010cc539bf260005";
	public static final String DEFAULT_USER_WORKSPACE_DEF="402883b90cc53079010cc539bf26000b";
	public static final String DEFAULT_PROFILES_DEF="402883b90cc53079010cc539bf260006";
	public static final String DEFAULT_USER_DEF="402883b90cc53079010cc539bf260007";
	public static final String DEFAULT_GROUP_DEF="402883b90cc53079010cc539bf260008";
	public static final String DEFAULT_APPLICATION_DEF="402883b90cc53079010cc539bf26000c";
	public static final String DEFAULT_APPLICATION_GROUP_DEF="402883b90cc53079010cc539bf26000d";

	//reserved databaseIds for Definitions
	public static final String DEFAULT_ENTRY_TASK_DEF="402883c1129b1f8101129b28bbe50002";
	public static final String DEFAULT_ENTRY_CALENDAR_DEF="402883c6115753d80111577dbe29000d";
	public static final String DEFAULT_ENTRY_SURVEY_DEF="402883c1129b1f8101129b28bbe50004";
	public static final String DEFAULT_ENTRY_SURVEY_VOTE_DEF="40288182131025d101131029d4920002";
	public static final String DEFAULT_ENTRY_MILESTONE_DEF="4028818213118b17011311b1f6210017";

	//reserved internalIds for templates.
	//presence is used to distinquish system templates from customer templates
	public static final String DEFAULT_FOLDER_CONFIG="402883b90d0de1f3010d0df5582b0001";
	public static final String DEFAULT_FOLDER_BLOG_CONFIG="402883b90d0de1f3010d0df5582b000c";
	public static final String DEFAULT_FOLDER_WIKI_CONFIG="402883b90d0de1f3010d0df5582b000d";
	public static final String DEFAULT_FOLDER_CALENDAR_CONFIG="402883b90d0de1f3010d0df5582b000e";
	public static final String DEFAULT_FOLDER_GUESTBOOK_CONFIG="402883b90d0de1f3010d0df5582b000f";
	public static final String DEFAULT_FOLDER_PHOTO_CONFIG="402883b90d0de1f3010d0df5582b0010";
	public static final String DEFAULT_FOLDER_LIBRARY_CONFIG="402883b90d0de1f3010d0df5582b0011";
	public static final String DEFAULT_FOLDER_TASK_CONFIG="402883b90cc53079010cc539bf260009";
	public static final String DEFAULT_FOLDER_MILESTONE_CONFIG="402883b90cc53079010cc539bf260011";
	public static final String DEFAULT_FOLDER_SURVEY_CONFIG="402883b90cc53079010cc539bf260010";
	public static final String DEFAULT_WORKSPACE_CONFIG="402883b90d0de1f3010d0df5582b0002";
	public static final String DEFAULT_USER_WORKSPACE_CONFIG="402883b90d0de1f3010d0df5582b0004";
	public static final String DEFAULT_TEAM_WORKSPACE_CONFIG="402883c1129b1f8101129b28bc620004";
	public static final String DEFAULT_PROJECT_WORKSPACE_CONFIG="402883c1129b1f8101129b28bc620005";
	public static final String DEFAULT_DISCUSSIONS_WORKSPACE_CONFIG="402880e619c6250f0119c62794b9000c";
	
	//reserved internalIds for binders
	public static final String PROFILE_ROOT_INTERNALID="402883b90d0de1f3010d0df5582b0005";
	public static final String TOP_WORKSPACE_INTERNALID="402883b90d0de1f3010d0df5582b0007";
	public static final String TEAM_ROOT_INTERNALID="402883b90d0de1f3010d0df5582b0009";
	public static final String GLOBAL_ROOT_INTERNALID="402883b90d0de1f3010d0df5582b000a";
	//reserverd internalids for alluser group
	public static final String ALL_USERS_GROUP_INTERNALID="402883b90d0de1f3010d0df5582b0006";
	//reserverd internalId user posting agent
	public static final String ANONYMOUS_POSTING_USER_INTERNALID="402883b90d0de1f3010d0df5582b0008";
	//super user bypasses all acl and operation checks
	public static final String SUPER_USER_INTERNALID="402883b90d0de1f3010d0df5582b000b";
	//id to run background jobs under - also acts as a super user but cannot loggin as this person
	public static final String JOB_PROCESSOR_INTERNALID="402883b90d0de1f3010d0df5582b0012";
	//shared guest account
	public static final String GUEST_USER_INTERNALID="402883b90d0de1f3010d0df5582b0013";
	//reserverd internalids for allapplication group
	public static final String ALL_APPLICATIONS_GROUP_INTERNALID="402883b90d0de1f3010d0df5582b0014";
	//reserved id put in acls/membership to indicate owner of object has access
	//search engine stores the ownerId of the entity in place of this
	public static final Long OWNER_USER_ID = Long.valueOf(-1);
	//reserved id put in acls/membership to indicate team members have access
	//search engine stores the actual members of the team in place of this
	public static final Long TEAM_MEMBER_ID = Long.valueOf(-2);

    //custom property prefix from ssf.properties
	public static final String CUSTOM_PROPERTY_PREFIX="custom.";

	//keys for data stored in request session
	public static final String SESSION_USERPROPERTIES="userProperties";
	public static final String SESSION_TABS="userTabs";
	public static final String SESSION_CLIPBOARD="userClipboard";
	public static final String SESSION_SAVE_LOCATION_ID="saveLocationId";

    //map keys returned from modules
    public static final String BINDER="binder";
    public static final String SEARCH_ENTRIES="search_entries";
    public static final String FULL_ENTRIES="database_entries";
    public static final String COMMUNITY_ENTITY_TAGS="community_entries_tags";
    public static final String PERSONAL_ENTITY_TAGS="personal_entries_tags";
    public static final String FOLDER_ENTRY_ANCESTORS="folderEntryAncestors";
    public static final String FOLDER_ENTRY_DESCENDANTS="folderEntryDescendants";
    public static final String FOLDER_ENTRY="folderEntry";
    public static final String SEARCH_COUNT_TOTAL="searchCountTotal";
    public static final String TOTAL_SEARCH_COUNT="totalSearchCount";
    public static final String TOTAL_SEARCH_RECORDS_RETURNED="totalSearchRecordsReturned";
    public static final String FILES_FROM_APPLET_FOR_BINDER="ss_attachFile";
    public static final String FILES_FROM_APPLET_FOLDER_INFO_FOR_BINDER="filesFromAppletFolderInfo";
    public static final String WORKFLOW_QUESTION_TEXT="workflow_questionText";
    public static final String WORKFLOW_QUESTION_RESPONSES="workflow_questionResponses";
 
    //map keys from definition module
    public static final String DEFINITION_ENTRY_DATA="entryData";
    public static final String DEFINITION_FILE_DATA="fileData";
    public static final String DEFINITION_TEAM_TYPE="team";
	public static final String DEFINITION_WORKSPACE_REMOTE_APPLICATION="workspaceRemoteApp";
	public static final String DEFINITION_FOLDER_REMOTE_APPLICATION="folderRemoteApp";
	public static final String DEFINITION_ENTRY_REMOTE_APPLICATION="entryRemoteApp";	
    
    //Default role titles
    public static final String ROLE_TITLE_VISITOR="__role.visitor";
    public static final String ROLE_TITLE_PARTICIPANT="__role.participant";
    public static final String ROLE_TITLE_GUEST_PARTICIPANT="__role.guestParticipant";
    public static final String ROLE_TITLE_TEAM_MEMBER="__role.teamMember";
    public static final String ROLE_TITLE_BINDER_ADMIN="__role.binderAdmin";
    public static final String ROLE_TITLE_WORKSPACE_CREATOR="__role.workspaceCreator";
    public static final String ROLE_TITLE_SITE_ADMIN="__role.siteAdmin";

    //map keys from admin/sendMail
    public static final String SENDMAIL_ERRORS="errors";
    public static final String SENDMAIL_DISTRIBUTION="distributionList";
    public static final String SENDMAIL_STATUS="status";
    public static final String SENDMAIL_STATUS_SENT="sent";
    public static final String SENDMAIL_STATUS_FAILED="failed";
    public static final String SENDMAIL_STATUS_SCHEDULED="scheduled";
    	
    
    public static final int LISTING_MAX_PAGE_SIZE = 100;
    public static final int MAX_TAG_LENGTH = 60;
    public static final int MAX_BINDER_ENTRIES_RESULTS = 2000;
    public static final long SEEN_TIMEOUT_DAYS = (long)30;  //entries older than 30 days are marked seen
    public static final long SEEN_MAP_TIMEOUT = (long)SEEN_TIMEOUT_DAYS*24*60*60*1000;  //older than 30 days return seen
    
    public static final String BINDER_PROPERTY_FOLDER_COLUMNS = "folderColumns";
    public static final String BINDER_PROPERTY_TEAM_MEMBERS = "teamMembers";
    public static final String BINDER_PROPERTY_WIKI_HOMEPAGE = "wikiHomepage";

    public static final String CONFIG_PROPERTY_REPOSITORY = "repository";
    public static final String CONFIG_PROPERTY_REPOSITORIES = "repositories";

    public static final String DASHBOARD_COMPONENT_BLOG_SUMMARY = "blog";
    public static final String DASHBOARD_COMPONENT_CALENDAR_SUMMARY = "calendar";
    public static final String DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY = "guestbook";
    public static final String DASHBOARD_COMPONENT_TASK_SUMMARY = "task";
    public static final String DASHBOARD_COMPONENT_BUDDY_LIST = "buddyList";
    public static final String DASHBOARD_COMPONENT_TEAM_MEMBERS_LIST = "teamMembersList";
    public static final String DASHBOARD_COMPONENT_FOLDER = "folder";
    public static final String DASHBOARD_COMPONENT_SEARCH = "search";
    public static final String DASHBOARD_COMPONENT_GALLERY = "gallery";    
    public static final String DASHBOARD_COMPONENT_WIKI_SUMMARY = "wiki";    
    public static final String DASHBOARD_COMPONENT_WORKSPACE_TREE = "workspaceTree";
    public static final String DASHBOARD_COMPONENT_REMOTE_APPLICATION = "remoteApplication";
    
    public static final String FOLDER_ENTRY_TO_BE_SHOWN = "folder_entry_to_be_shown";
    
    public static final String RELEVANCE_DASHBOARD_ACTIVITIES = "activities";
    public static final String RELEVANCE_DASHBOARD_PROFILE = "profile";
    public static final String RELEVANCE_DASHBOARD_TASKS_AND_CALENDARS = "tasks_and_calendars";
    public static final String RELEVANCE_DASHBOARD_WHATS_NEW = "whats_new";
    public static final String RELEVANCE_DASHBOARD_TRACKED_ITEMS = "trackedItems";
    public static final String RELEVANCE_TRACKED_BINDERS = "trackedBinders";
    public static final String RELEVANCE_TRACKED_CALENDARS = "trackedCalendars";
    public static final String RELEVANCE_TRACKED_PEOPLE = "trackedPeople";
    
    public static final String RELEVANCE_PAGE_ENTRIES_VIEWED = "entriesViewed";
    public static final String RELEVANCE_PAGE_NEW_SITE = "newSite";
    public static final String RELEVANCE_PAGE_NEW_TRACKED = "newTracked";
    public static final String RELEVANCE_PAGE_ACTIVITIES = "activities";
    public static final String RELEVANCE_PAGE_DOCS = "docs";
    public static final String RELEVANCE_PAGE_HOT = "hot";
    public static final String RELEVANCE_PAGE_SHARED = "shared";
    public static final String RELEVANCE_PAGE_TASKS = "tasks";
    public static final String RELEVANCE_PAGE_VISITORS = "visitors";
    
    public static final String SEARCH_OFFSET = "offset";
    public static final String SEARCH_MAX_HITS = "maxHits";
    public static final String SEARCH_USER_MAX_HITS = "userMaxHits";
    public static final String SEARCH_USER_OFFSET = "userOffset";
    public static final int SEARCH_MAX_HITS_DEFAULT = 10;
    public static final int SEARCH_RESULTS_TO_CREATE_STATISTICS = 200;
    public static final String SEARCH_SEARCH_FILTER = "searchFilter";
    public static final String SEARCH_SEARCH_DYNAMIC_FILTER = "searchDynamicFilter";
    public static final String SEARCH_SORT_BY = "sortBy";
    public static final String SEARCH_SORT_DESCEND = "sortDescend";
    public static final String SEARCH_START_DATE = "startDate";
    public static final String SEARCH_END_DATE = "endDate";
    public static final String SEARCH_FILTER_AND = "searchFilterAnd";
    public static final String SEARCH_FILTER_OR = "searchFilterOr";
    public static final String SEARCH_DASHBOARD_CURRENT_BINDER_ID = "searchDashboardBinderId";
    public static final String PAGE_ENTRIES_PER_PAGE = "entriesPerPage";
    public static final String SEARCH_PAGE_ENTRIES_PER_PAGE = "entriesPerSearchPage";
    public static final String SEARCH_YEAR_MONTH = "yearMonth";
    public static final String SEARCH_EVENT_DAYS = "eventDays";
    public static final String SEARCH_COMMUNITY_TAG = "searchCommunityTag";
    public static final String SEARCH_PERSONAL_TAG = "searchPersonalTag";
    public static final String SEARCH_TITLE = "searchTitle";
    public static final String SEARCH_LASTACTIVITY_DATE_START = "lastActivityDateStart";
    public static final String SEARCH_LASTACTIVITY_DATE_END = "lastActivityDateEnd";
    public static final String SEARCH_CREATION_DATE_START = "creationDateStart";
    public static final String SEARCH_CREATION_DATE_END = "creationDateEnd";
    
    //Search Result Identifier
    public static final String SEARCH_RESULTS_DISPLAY="ss_searchResultListing";

    //file repository
    public static final String FI_ADAPTER = "fiAdapter";
	public static final String PI_SYNCH_TO_SOURCE = "_synchToSource";
    
    //User properties
    public static final String USER_DISPLAY_STYLE_ACCESSIBLE = "accessible";
    public static final String USER_DISPLAY_STYLE_IFRAME = "iframe";
    public static final String USER_DISPLAY_STYLE_NEWPAGE = "newpage";
    public static final String USER_DISPLAY_STYLE_POPUP = "popup";
    public static final String USER_DISPLAY_STYLE_VERTICAL = "vertical";
    public static final String USER_PROPERTY_CALENDAR_VIEWMODE = "calendarViewMode";
    public static final String USER_PROPERTY_DASHBOARD_GLOBAL = "dashboard_global";
    public static final String USER_PROPERTY_DASHBOARD_SHOW_ALL = "dashboard_show_all";
    public static final String USER_PROPERTY_DEBUG = "debugMode";
    public static final String USER_PROPERTY_DISPLAY_STYLE = "displayStyle";
    public static final String USER_PROPERTY_DISPLAY_DEFINITION = "displayDefinition";
    public static final String USER_PROPERTY_HELP_CPANEL_SHOW = "help_cpanel_show";
    public static final String USER_PROPERTY_RELEVANCE_MAP = "relevanceMap";
    public static final String USER_PROPERTY_SEARCH_FILTERS = "searchFilters";
    public static final String USER_PROPERTY_USER_FILTER = "userFilter";
    public static final String USER_PROPERTY_FAVORITES = "userFavorites";
    public static final String USER_PROPERTY_FOLDER_COLUMNS = "userFolderColumns";
    public static final String USER_PROPERTY_MOBILE_BINDER_IDS = "userMobileBinderIds";
    public static final String USER_PROPERTY_PERMALINK_URL = "userPermalinkUrl";
    public static final String USER_PROPERTY_PORTAL_URL = "userPortalUrl";
    public static final String USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS = "userSearchResultsFolderColumns";
    public static final String USER_PROPERTY_SEARCH_RESULTS_COLUMN_POSITIONS="searchResultsColumnPositions";
    public static final String USER_PROPERTY_SAVED_SEARCH_QUERIES="userSearchQueries";
    public static final String USER_PROPERTY_SIDEBAR_PANEL_PREFIX="sidebarPanelShow_";
    public static final String USER_PROPERTY_SIDEBAR_VISIBILITY="sidebarVisibility";
    public static final String USER_PROPERTY_BUSINESS_CARD_PREFIX="businessCardShow_";
    public static final String USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK="calendarFirstDayOfWeek";
    public static final String USER_PROPERTY_CALENDAR_WORK_DAY_START="calendarWorkDayStart";
    public static final String USER_THEME_DEFAULT="default";
    
    //workflow definition names
    public static final String WORKFLOW_START_STATE = "__start_state";
    public static final String WORKFLOW_END_STATE = "__end_state";
    public static final String WORKFLOW_PARALLEL_THREAD_NAME = "name";
    public static final String WORKFLOW_PARALLEL_THREAD_START_STATE = "startState";
    public static final String WORKFLOW_PARALLEL_THREAD_END_TRANSITION = "transitionState";
    
    //reserved input tags
    public static final String INPUT_FIELD_POSTING_FROM="__poster";
    public static final String INPUT_FIELD_ENTITY_ATTACHMENTS="ss_attachFile";
    public static final String INPUT_FIELD_GROUP_PRINCIPAL_MEMBERNAME="memberName";
    public static final String INPUT_FIELD_TAGS="_tags";
    public static final String INPUT_FIELD_ORDER_SUFFIX="__order";
	public static final String INPUT_FIELD_FUNCTIONMEMBERSHIPS="com.sitescape.team.workareaFunctionMemberships";
	public static final String INPUT_FIELD_DEFINITIONS="com.sitescape.team.definitions";
	public static final String INPUT_FIELD_WORKFLOWASSOCIATIONS="com.sitescape.team.workflows";

	/********INPUT options parameters********/
	//Passed to processors to force a database lock.  Used to reduce optimistic lock exceptions
	public static final String INPUT_OPTION_FORCE_LOCK="com.sitescape.team.options.lock";
	//default = Boolean.TRUE if INPUT_OPTION_COPY_NEW_BINDER=TRUE; Preserve old docnumbers when copy to a new folder;
	public static final String INPUT_OPTION_PRESERVE_DOCNUMBER="com.sitescape.team.options.preserve.docnum";
	//Default=Boolean.FALSE; When deleteing a profile entry, delete the associated workspace and all source/sub binders
	public static final String INPUT_OPTION_DELETE_USER_WORKSPACE="com.sitescape.team.options.delete.userworkspace";
	//migration flags
	public static final String INPUT_OPTION_NO_INDEX="com.sitescape.team.options.no_indexing";
    public static final String INPUT_OPTION_NO_WORKFLOW="com.sitescape.team.options.no_workflow_processing";
    public static final String INPUT_OPTION_CREATION_NAME="com.sitescape.team.options.entity.creation.name";
    public static final String INPUT_OPTION_CREATION_DATE="com.sitescape.team.options.entity.creation.timestamp";
    public static final String INPUT_OPTION_MODIFICATION_NAME="com.sitescape.team.options.entity.modification.name";
    public static final String INPUT_OPTION_MODIFICATION_DATE="com.sitescape.team.options.entity.modification.timestamp";
    public static final String INPUT_OPTION_FORCE_WORKFLOW_STATE="com.sitescape.team.options.entity.workflow.state";
    public static final String INPUT_OPTION_NO_MODIFICATION_DATE="com.sitescape.team.options.no_modificationdate_processing";
 	//Reserved data field names
    public static final String FIELD_ID="id";
    public static final String FIELD_ZONE="zoneId";
    public static final String FIELD_INTERNALID="internalId";

    public static final String FIELD_ENTITY_TITLE="title";
    public static final String FIELD_ENTITY_NORMALIZED_TITLE="normalTitle";
    public static final String FIELD_ENTITY_DESCRIPTION="description";
    public static final String FIELD_ENTITY_PARENTBINDER="parentBinder";
    public static final String FIELD_ENTITY_DEFTYPE="definitionType";
    public static final String FIELD_ENTITY_ICONNAME="iconName";
    public static final String FIELD_ENTITY_CREATE_PRINCIPAL="creation.principal";
    public static final String FIELD_ENTITY_MODIFY_PRINCIPAL="modification.principal";
    public static final String FIELD_ENTITY_DELETED="deleted";

    public static final String FIELD_FILE_ID="_fileID";
    
    public static final String FIELD_APPLICATION_POST_URL="postUrl";
    public static final String FIELD_APPLICATION_TIMEOUT="timeout";
    public static final String FIELD_APPLICATION_TRUSTED="trusted";
    public static final String FIELD_BINDER_LIBRARY="library";
    public static final String FIELD_BINDER_NAME="name";
    public static final String FIELD_BINDER_UNIQUETITLES="uniqueTitles";
    public static final String FIELD_BINDER_SEARCHTITLE="searchTitle";
	public static final String FIELD_BINDER_MIRRORED = "mirrored"; 
	public static final String FIELD_BINDER_INHERITTEAMMEMBERS = "teamMembershipInherited"; 
	public static final String FIELD_BINDER_RESOURCE_DRIVER_NAME = "resourceDriverName";
	public static final String FIELD_BINDER_RESOURCE_PATH = "resourcePath";
    public static final String FIELD_USER_FIRSTNAME="firstName";
    public static final String FIELD_USER_MIDDLENAME="middleName";
    public static final String FIELD_USER_LASTNAME="lastName";
    public static final String FIELD_USER_DISPLAYSTYLE="displayStyle";
    public static final String FIELD_USER_EMAIL="emailAddress";
    public static final String FIELD_USER_EMAIL_TEXT="txtEmailAddress";
    public static final String FIELD_USER_EMAIL_MOBILE="mobileEmailAddress";
    public static final String FIELD_USER_LOCALE="locale";
    public static final String FIELD_USER_SKYPEID="skypeId";
    public static final String FIELD_USER_TWITTERID="twitterId";
    public static final String FIELD_USER_STATUS="status";
    public static final String FIELD_USER_TIMEZONE="timeZone";
    public static final String FIELD_USER_PASSWORD="password";
    public static final String FIELD_PRINCIPAL_FOREIGNNAME="foreignName";
    public static final String FIELD_PRINCIPAL_NAME="name";
    public static final String FIELD_PRINCIPAL_DISABLED="disabled";
    public static final String FIELD_GROUP_PRINCIPAL_MEMBERS="members";
    public static final String FIELD_PRINCIPAL_THEME="theme";
    public static final String TASK_FIELD_ASSIGNMENT="assignment";
    public static final String TASK_FIELD_EVENT="start_end#EndDate";
    public static final String FIELD_TEMPLATE_TITLE="templateTitle";
    public static final String FIELD_TEMPLATE_DESCRIPTION="templateDescription";

    // families of entries
    public static final String FAMILY_TASK = "task";
    public static final String FAMILY_CALENDAR = "calendar";
    
    //xml tags - some used in GenerateLdapList (as strings) - keep in sync
    public static final String XTAG_ATTRIBUTE_DATABASEID="databaseId";
    public static final String XTAG_ATTRIBUTE_NAME="name";
    public static final String XTAG_ATTRIBUTE_STATE_CAPTION="stateCaption";
    public static final String XTAG_ATTRIBUTE_THREAD_CAPTION="threadCaption";
    public static final String XTAG_ATTRIBUTE_TYPE="type";
    public static final String XTAG_ATTRIBUTE_INTERNALID="internalId";
    
    //change log element attributes
    public static final String XTAG_ATTRIBUTE_OPERATION="operation";
    public static final String XTAG_ATTRIBUTE_MODIFIEDBY="modifiedBy";
    public static final String XTAG_ATTRIBUTE_MODIFIEDON="modifiedOn";
    public static final String XTAG_ATTRIBUTE_LOGVERSION="logVersion";
    //types of XML elements
    public static final String XTAG_ELEMENT_TYPE_PROPERTY="property";
    public static final String XTAG_ELEMENT_TYPE_HISTORYSTAMP="historyStamp";
    public static final String XTAG_ELEMENT_TYPE_EVENT="event";
    public static final String XTAG_ELEMENT_TYPE_WORKFLOWSTATE="workflowState";
    public static final String XTAG_ELEMENT_TYPE_WORKFLOWREPONSE="workflowResponse";
    public static final String XTAG_ELEMENT_TYPE_FILEARCHIVE="fileArchive";
    public static final String XTAG_ELEMENT_TYPE_VERSIONARCHIVE="versionArchive";
    public static final String XTAG_ELEMENT_TYPE_FILEATTACHMENT="fileAttachment";
    public static final String XTAG_ELEMENT_TYPE_VERSIONATTACHMENT="versionAttachment";
    public static final String XTAG_ELEMENT_TYPE_ATTRIBUTE="attribute";
    public static final String XTAG_ELEMENT_TYPE_ATTRIBUTE_SET="attribute-set";
    public static final String XTAG_ELEMENT_TYPE_DEFINITION="definition";
    public static final String XTAG_ELEMENT_TYPE_TEMPLATE="template";
    public static final String XTAG_ELEMENT_TYPE_DASHBOARD="dashboard";
    public static final String XTAG_ELEMENT_TYPE_DASHBOARD_COMPONENT="component";
    public static final String XTAG_ELEMENT_TYPE_DASHBOARD_COMPONENT_DATA="data";
    public static final String XTAG_ELEMENT_TYPE_DASHBOARD_LAYOUT="layout";
    public static final String XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP="workAreaFunctionMembership";
    // values of type= attribute for element XTAG_ELEMENT_TYPE_ATTRIBUTE
    public static final String XTAG_TYPE_STRING="string";
    public static final String XTAG_TYPE_DESCRIPTION="description";
    public static final String XTAG_TYPE_COMMASEPARATED="commaSeparated";
    public static final String XTAG_TYPE_BOOLEAN="boolean";
    public static final String XTAG_TYPE_LONG="long";
    public static final String XTAG_TYPE_DATE="date";
    public static final String XTAG_TYPE_SERIALIZED="serialized";
    public static final String XTAG_TYPE_XML="xml";
    public static final String XTAG_TYPE_EVENT="event";
    public static final String XTAG_TYPE_FILE="file";
    //attributes for element XTAG_ELEMENT_TYPE_HISTORYSTAMP 
    public static final String XTAG_HISTORY_BY="author";
    public static final String XTAG_HISTORY_WHEN="when";
    //value of name= attribute on XTAG_ELEMENT_TYPE_HISTORYSTAMP 
    public static final String XTAG_ENTITY_CREATION="created";
    public static final String XTAG_ENTITY_MODIFICATION="modified";
    //values of name= attribute on XTAG_ELEMENT_TYPE_ATTRIBUTE for entities
    public static final String XTAG_ENTITY_TITLE="title";
    public static final String XTAG_ENTITY_DESCRIPTION="description";
    public static final String XTAG_ENTITY_DEFINITION_NAME="entryDefName";
    public static final String XTAG_ENTITY_ICONNAME="iconName";
    public static final String XTAG_ENTITY_ATTACHMENTS="attachments";
    //values of name= attribute on XTAG_ELEMENT_TYPE_PROPERTY for entities   
    public static final String XTAG_ENTITY_PARENTBINDER="parentBinder";
    public static final String XTAG_ENTITY_DEFINITION="entryDef";
    //values of name= attributes on XTAG_ELEMENT_TYPE_ATTRIBUTE for principals
    public static final String XTAG_PRINCIPAL_FOREIGNNAME="foreignName";
    public static final String XTAG_PRINCIPAL_NAME="name";
    public static final String XTAG_PRINCIPAL_DISABLED="disabled";
    //values of name= attributes on XTAG_ELEMENT_TYPE_ATTRIBUTE for user
    public static final String XTAG_USER_FIRSTNAME="firstName";
    public static final String XTAG_USER_MIDDLENAME="middleName";
    public static final String XTAG_USER_LASTNAME="lastName";
    public static final String XTAG_USER_EMAIL="emailAddress";
    public static final String XTAG_USER_EMAIL_TEXT="txtEmailAddress";
    public static final String XTAG_USER_EMAIL_MOBILE="mobileEmailAddress";
    public static final String XTAG_USER_ZONNAME="zonName";
    public static final String XTAG_USER_ORGANIZATION="organization";
    public static final String XTAG_USER_PHONE="phone";
    public static final String XTAG_USER_SKYPEID="skypeId";
    public static final String XTAG_USER_TWITTERID="twitterId";
    public static final String XTAG_USER_STATUS="status";
    public static final String XTAG_USER_STATUS_DATE="statusDate";
    public static final String XTAG_USER_TIMEZONE="timeZone";
    //values of name= attributes on XTAG_ELEMENT_TYPE_PROPERTY for user
    public static final String XTAG_USER_DISPLAYSTYLE="displayStyle";
    public static final String XTAG_USER_LOCALE="locale";
    public static final String XTAG_USER_PASSWORD="password";
    public static final String XTAG_USER_DIGESTSEED="digestSeed";
    public static final String XTAG_USER_LOGINDATE="loginDate";
    //values of name= attributes on XTAG_ELEMENT_TYPE_PROPERTY for groups
    public static final String XTAG_GROUP_MEMBERS="members";
    public static final String XTAG_GROUP_MEMBER_NAME="memberName";
    //values of name= attributes on XTAG_ELEMENT_TYPE_ATTRIBUTE for groups
    public static final String XTAG_APPLICATION_POSTURL="postUrl";
    //values of name= attributes on XTAG_ELEMENT_TYPE_PROPERTY for groups
    public static final String XTAG_APPLICATION_GROUP_MEMBERS="appMembers";

    //values of name= attributes on XTAG_ELEMENT_TYPE_PROPERTY for folderentry
    public static final String XTAG_FOLDERENTRY_DOCNUMBER="docNumber";
    public static final String XTAG_FOLDERENTRY_TOPENTRY="topEntry";
    public static final String XTAG_FOLDERENTRY_PARENTENTRY="parentEntry";
    public static final String XTAG_FOLDERENTRY_POSTEDBY="postedBy";
    //value of name= attributes on properties of XTAG_ELEMENT_TYPE_FILEATTACHMENT 
    public static final String XTAG_FILE_NAME="fileName";
    public static final String XTAG_FILE_LENGTH="fileLength";
    public static final String XTAG_FILE_REPOSITORY="repository";
    public static final String XTAG_FILE_LAST_VERSION="lastVersion";
    //value of name= attributes on properties of XTAG_ELEMENT_TYPE_VERSIONATTACHMENT 
    public static final String XTAG_FILE_PARENT="parentAttachment";
    public static final String XTAG_FILE_VERSION_NUMBER="versionNumber";
    public static final String XTAG_FILE_VERSION_NAME="versionName";
    //values of attributes on element XTAG_ELEMENT_TYPE_FILEARCHIVE
    public static final String XTAG_FILE_ARCHIVE_STORE_NAME="archiveStoreName";
    //values of attributes on element XTAG_ELEMENT_TYPE_VERSIONARCHIVE
    public static final String XTAG_FILE_ARCHIVE_URI="archiveURI";
    //values of name= attribute on XTAG_ELEMENT_TYPE_PROPERTY for element XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP
    public static final String XTAG_WA_FUNCTION="function";
    public static final String XTAG_WA_FUNCTION_NAME="functionName";
    public static final String XTAG_WA_MEMBERS="members";
    public static final String XTAG_WA_MEMBER_NAME="memberName";

    //value of name= attribue on XTAG_ELEMENT_TYPE_ATTRIBUTE for binders
    public static final String XTAG_BINDER_NAME="name";
    public static final String XTAG_TEMPLATE_TITLE="templateTitle";
    public static final String XTAG_TEMPLATE_DESCRIPTION="templateDescription";
   //value of name= attribue on XTAG_ELEMENT_TYPE_PROPERTY for binders
    public static final String XTAG_BINDER_UNIQUETITLES="uniqueTitle";
    public static final String XTAG_BINDER_LIBRARY="library";
    public static final String XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP="inheritFunctionMembership";
    public static final String XTAG_BINDER_INHERITDEFINITIONS="inheritDefinitions";
    public static final String XTAG_BINDER_INHERITTEAMMEMBERS="inheritTeamMembers";
    public static final String XTAG_BINDER_TEAMMEMBERS="teamMembers";
    public static final String XTAG_BINDER_TEAMMEMBER_NAME="teamMemberName";
     
    //value of name= attribute on XTAG_ELEMENT_TYPE_HISTORYSTAMP 
    public static final String XTAG_WF_CHANGE="workflowChange";
    //value of name= attribute XTAG_ELEMENT_TYPE_PROPERTY element XTAG_ELEMENT_TYPE_WORKFLOWSTATE
    public static final String XTAG_WFS_DEFINITION="definition";
    public static final String XTAG_WFS_TIMER="timer";
    public static final String XTAG_WFS_THREAD="thread";
    public static final String XTAG_WFS_PROCESS="process";
     
    //value of name= attribute XTAG_ELEMENT_TYPE_PROPERTY element XTAG_ELEMENT_TYPE_WORKFLOWRESPONSE
    public static final String XTAG_WFR_DEFINITION="definition";
    public static final String XTAG_WFR_RESPONDER="responder";
    public static final String XTAG_WFR_RESPONSEDATE="responseDate";
    public static final String XTAG_WFR_RESPONSE="response";
    

	
}
