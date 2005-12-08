package com.sitescape.ef.web;

public class WebKeys {
	// Attribute names reserved by portlet specification
	public static final String JAVAX_PORTLET_CONFIG = "javax.portlet.config";
	public static final String JAVAX_PORTLET_REQUEST = "javax.portlet.request";
	public static final String JAVAX_PORTLET_RESPONSE = "javax.portlet.response";

	// Calendar view stuff
	public static final String CALENDAR_EVENTDATES = "ssEventDates";
	public static final String CALENDAR_VIEWMODE = "ssCalendarViewMode";
	public static final String CALENDAR_VIEW_DAY = "day";
	public static final String CALENDAR_VIEW_WEEK = "week";
	public static final String CALENDAR_VIEW_MONTH = "month";
	public static final String CALENDAR_CURRENT_DATE = "ssCurrentDate";
	public static final String CALENDAR_CURRENT_VIEW_STARTDATE = "ssCalStartDate";
	public static final String CALENDAR_CURRENT_VIEW_ENDDATE = "ssCalEndDate";
	public static final String CALENDAR_DOW = "cal_dow";
	public static final String CALENDAR_DOM = "cal_dom";
	public static final String CALENDAR_ENTRYTITLE = "cal_entrytitle";
	public static final String CALENDAR_STARTTIMESTRING = "cal_starttimestring";
	public static final String CALENDAR_ENDTIMESTRING = "cal_endtimestring";
	public static final String CALENDAR_VIEWBEAN = "ssCalendarViewBean";	
	public static final String CALENDAR_EVENTDATAMAP = "cal_eventdatamap";	
	public static final String CALENDAR_URL_VIEWMODE = "cal_url_viewmode";	
	public static final String CALENDAR_URL_NEWVIEWDATE = "cal_url_newviewdate";	
	
	// miscellaneous
	public static final String DEFINITION_DEFAULT_FORM_NAME = "entryForm";
    public static final String LOCALE = "ss_locale";
    public static final String SESSION_LAST_ENTRY_VIEWED = "last_entry_viewed";
    public static final String SESSION_LAST_HISTORY_ENTRY_VIEWED = "last_history_entry_viewed";
    public static final String EVENT = "event";
    public static final String ENTRY = "entry";
    
    //URL parameters
    public static final String IS_ACTION_URL="actionUrl";
    public static final String FORUM_URL_ATTRIBUTE = "attr";
    public static final String FORUM_URL_ATTRIBUTE_ID = "attrId";
	public static final String FORUM_URL_ENTRY_ID = "entryId";
    public static final String FORUM_URL_ENTRY_TYPE="entryType";
	public static final String FORUM_URL_FILE = "file";
	public static final String FORUM_URL_FILE_ID = "fileId";
	public static final String FORUM_URL_FORUM = "forum";
	public static final String FORUM_URL_FORUM_ID = "forumId";
	public static final String FORUM_URL_OPERATION = "operation";
	public static final String FORUM_URL_VALUE = "value";
	
	//Preferences
	public static final String FORUM_PREF_FORUM_ID_LIST = "forumIdList";
	
    //actions
    public static final String ADMIN_ACTION_CONFIGURE_ROLES = "configure_roles";
    public static final String FORUM_ACTION_ADD_ENTRY = "add_entry";
	public static final String FORUM_ACTION_ADD_REPLY = "add_reply";
	public static final String FORUM_ACTION_CONFIGURE_FORUM = "configure_forum";
	public static final String FORUM_ACTION_DEFINITION_BUILDER = "definition_builder";
	public static final String FORUM_ACTION_DEFINITION_BUILDER_DEFINITION_TYPE = "definition_type";
	public static final String FORUM_ACTION_DELETE_ENTRY = "delete_entry";
	public static final String FORUM_ACTION_MODIFY_ENTRY = "modify_entry";
	public static final String FORUM_ACTION_VIEW_FORUM = "view_forum";
	public static final String FORUM_ACTION_VIEW_ENTRY = "view_entry";
	public static final String FORUM_ACTION_UNSEEN = "view_unseen";
	public static final String LDAP_ACTION_CONFIGURE="configure_ldap";
	public static final String NOTIFY_ACTION_CONFIGURE="configure_notify";
	public static final String POSTING_ACTION_CONFIGURE="configure_posting";
	public static final String POSTINGJOB_ACTION_CONFIGURE="configure_posting_job";
	public static final String SEARCH_INDEX_ACTION_CONFIGURE="configure_search_index";
	
	//oerations
 	public static final String FORUM_OPERATION_ADMINISTRATION = "administration";
	public static final String FORUM_OPERATION_SET_DISPLAY_STYLE = "set_display_style";
	public static final String FORUM_OPERATION_VIEW_ENTRY = "view_entry";
	public static final String FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT = "history_next";
	public static final String FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS = "history_previous";
	public static final String FORUM_OPERATION_VIEW_ENTRY_NEXT = "entry_next";
	public static final String FORUM_OPERATION_VIEW_ENTRY_PREVIOUS = "entry_previous";
	public static final String FORUM_OPERATION_VIEW_FILE = "view_file";
	public static final String FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE = "set_cal_display_mode";
	public static final String FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE = "set_cal_display_date";
	public static final String FORUM_OPERATION_CALENDAR_GOTO_DATE = "cal_goto_date";
	public static final String FORUM_OPERATION_UNSEEN_COUNTS = "unseen_counts";
	public static final String FORUM_OPERATION_UNSEEN_LIST = "unseen_list";
	public static final String OPERATION_POSTING_ALIASES="alias";
	public static final String OPERATION_ADD_POSTING_ALIASES="addAlias";
	public static final String OPERATION_MODIFY_POSTING_ALIASES="modifyAlias";
	
	// MODEL TAGS & Attributes
	public static final String ACTION = "action";
	public static final String ADMIN_TREE="ssAdminDomTree";
    public static final String CONFIG_ELEMENT="ssConfigElement";
    public static final String CONFIG_DEFINITION="ssConfigDefinition";
    public static final String CONFIG_JSP_STYLE="ssConfigJspStyle";
    public static final String DEFAULT_FOLDER_DEFINITION="ssDefaultFolderDefinition";
    public static final String DEFAULT_FOLDER_DEFINITION_ID="ssDefaultFolderDefinitionId";
    public static final String DEFINITION="ssDefinition";
    public static final String DEFINITION_ENTRY="ssDefinitionEntry";
    public static final String EMAIL_ALIASES="ssEmailAliases";
    public static final String ENTRY_DEFINITION="ssEntryDefinition";
    public static final String ENTRY_DEFINTION_MAP="ssEntryDefinitionMap";
    public static final String ENTRY_ID = "ssEntryId";
    public static final String FOLDER = "ssFolder";
    public static final String FOLDER_DEFINTION_MAP="ssFolderDefinitionMap";
    public static final String FOLDER_LIST = "ssFolderList";
    public static final String FOLDER_ID_LIST = "ssFolderIdList";
    public static final String FOLDERS = "ssFolders";
    public static final String FOLDER_DOM_TREE="ssFolderDomTree";
    public static final String FOLDER_ENTRY="ssFolderEntry";
    public static final String FOLDER_ENTRIES="ssFolderEntries";
    public static final String FOLDER_ENTRY_DESCENDANTS="ssFolderEntryDescendants";
    public static final String FOLDER_ENTRY_ANCESTORS="ssFolderEntryAncestors";
    public static final String FOLDER_ENTRY_TOOLBAR="ssFolderEntryToolbar";
    public static final String FOLDER_TOOLBAR="ssFolderToolbar";
    public static final String FOLDER_WORKFLOW_ASSOCIATIONS="ssFolderWorkflowAssociations";
    public static final String FORUM_ID_LIST = "ssForumIdList";
    public static final String FORUM_TOOLBAR="ssForumToolbar";
    public static final String GROUPS="ssGroups";
    public static final String HISTORY_CACHE="ssHistoryCache";
    public static final String HISTORY_MAP="ssHistoryMap";
    public static final String LDAP_CONFIG="ssLdapConfig";
    public static final String NOTIFICATION="ssNotification";
    public static final String POSTINGS="ssPostings";
    public static final String PUBLIC_DEFINITIONS="ssPublicDefinitions";
    public static final String PUBLIC_ENTRY_DEFINITIONS="ssPublicEntryDefinitions";
    public static final String PUBLIC_FOLDER_DEFINITIONS="ssPublicFolderDefinitions";
    public static final String PUBLIC_WORKFLOW_DEFINITIONS="ssPublicWorkflowDefinitions";
    public static final String SCHEDULE="ssSchedule";
    public static final String SCHEDULE_INFO="ssScheduleInfo";
    public static final String SEEN_MAP="ssSeenMap";
    public static final String SELECTED_GROUPS="ssSelectedGroups";
    public static final String SELECTED_PRINCIPALS="ssSelectedPrincipals";
    public static final String SELECTED_USERS="ssSelectedUsers";
    public static final String TOOLBAR="ssToolbar";
    public static final String USERS="ssUsers";
	public static final String USER_NAME = "com.sitescape.username";
	public static final String USER_PRINCIPAL = "com.sitescape.principal";
	public static final String USER_ID = "com.sitescape.userId";
    public static final String USER_PROPERTIES="ssUserProperties";
    public static final String WORKSPACE="ssWorkspace";
    public static final String WORKSPACE_DOM_TREE="ssWsDomTree";
	public static final String ZONE_NAME = "com.sitescape.zonename";

    //View names
    public static final String VIEW_CONFIGURE = "forum/configure";
    public static final String VIEW_NO_DEFINITION="forum/view_default";
    public static final String VIEW_FORUM="forum/view_forum";
    public static final String VIEW_ENTRY="forum/view_entry";
    public static final String VIEW="forum/view";
    public static final String VIEW_NO_ENTRY="forum/view_forum_no_entry";
    public static final String VIEW_MODIFY_ENTRY="forum/modify_entry";
    public static final String VIEW_DELETE_ENTRY="forum/delete_entry";
    public static final String VIEW_EDIT="forum/edit";
    public static final String VIEW_ADD_ENTRY = "forum/add_entry";
    public static final String VIEW_DEFINITION="definition_builder/view_definition_builder";
    public static final String VIEW_DEFINITION_OPTION="definition_builder/view_definition_builder_option";
    public static final String VIEW_DEFINITION_XML="definition_builder/view_definition_xml";
    public static final String VIEW_ADMIN_CONFIGURE_LDAP="administration/configureLdap";
    public static final String VIEW_ADMIN_CONFIGURE_NOTIFICATION="administration/configureNotify";
    public static final String VIEW_ADMIN_CONFIGURE_POSTING="administration/configurePosting";
    public static final String VIEW_ADMIN_CONFIGURE_POSTING_JOB="administration/configurePostingJob";
    public static final String VIEW_ADMIN_CONFIGURE_SEARCH_INDEX="administration/configureSearchIndex";
    
    //servlet paths
    public static final String SERVLET_VIEW_FILE="viewFile";
    public static final String SERVLET_VIEW_CSS="viewCss";
    
}
