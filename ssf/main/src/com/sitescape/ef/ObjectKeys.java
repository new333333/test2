package com.sitescape.ef;

/**
 * Defines symbols that the core is aware of.
 * 
 * @author Jong Kim
 *
 */
public interface ObjectKeys {
    /**
     * Note: Maintain the symbols in alphabetical order. 
     */
	
	/**
	 * reserved ids for reserved objects
	 */
	public static final String DEFAULT_FOLDER_DEF="402883b90cc53079010cc539bf260001";
	public static final String DEFAULT_FOLDER_ENTRY_DEF="402883b90cc53079010cc539bf260002";
	public static final String DEFAULT_FILE_FOLDER_DEF="402883b90cc53079010cc539bf260003";
	public static final String DEFAULT_FILE_ENTRY_DEF="402883b90cc53079010cc539bf260004";
	public static final String DEFAULT_WORKSPACE_DEF="402883b90cc53079010cc539bf260005";
	public static final String DEFAULT_PROFILES_DEF="402883b90cc53079010cc539bf260006";
	public static final String DEFAULT_USER_DEF="402883b90cc53079010cc539bf260007";
	public static final String DEFAULT_GROUP_DEF="402883b90cc53079010cc539bf260008";
	public static final String ADMINISTRATOR_ROLE_ID="402883b90cc53079010cc539bf260009";
	public static final String TEAM_MEMBER_ROLE_ID="402883b90cc53079010cc539bf26000a";
	public static final String DEFAULT_USER_WORKSPACE_DEF="402883b90cc53079010cc539bf26000b";
	public static final String DEFAULT_FOLDER_CONFIG="402883b90d0de1f3010d0df5582b0001";
	public static final String DEFAULT_WORKSPACE_CONFIG="402883b90d0de1f3010d0df5582b0002";
	public static final String DEFAULT_FILE_FOLDER_CONFIG="402883b90d0de1f3010d0df5582b0003";
	public static final String DEFAULT_USER_WORKSPACE_CONFIG="402883b90d0de1f3010d0df5582b0004";
	public static final String PROFILE_ROOT_ID="402883b90d0de1f3010d0df5582b0005";
	public static final String ALL_USERS_GROUP_ID="402883b90d0de1f3010d0df5582b0006";
	public static final String TOP_WORKSPACE_ID="402883b90d0de1f3010d0df5582b0007";
	public static final String ANONYMOUS_POSTING_USER_ID="402883b90d0de1f3010d0df5582b0008";
	
    public static final String BINDER="binder";
    public static final String CUSTOM_PROPERTY_PREFIX="custom.";
    public static final String SEARCH_ENTRIES="search_entries";
    public static final String FULL_ENTRIES="database_entries";
    public static final String FOLDER_ENTRY_ANCESTORS="folderEntryAncestors";
    public static final String FOLDER_ENTRY_DESCENDANTS="folderEntryDescendants";
    public static final String FOLDER_ENTRY="folderEntry";
    public static final String TOTAL_SEARCH_COUNT="totalSearchCount";
    public static final int LISTING_MAX_PAGE_SIZE = 100;
    public static final long SEEN_MAP_TIMEOUT = (long)30*24*60*60*1000;
    public static final long SEEN_HISTORY_MAP_TIMEOUT = (long)7*24*60*60*1000;
    public static final String TOOLBAR_QUALIFIER_ONCLICK = "onClick";
    public static final String USER = "user";
    
    public static final String BINDER_PROPERTY_DASHBOARD = "dashboard";

    public static final String CONFIG_PROPERTY_REPOSITORY = "repository";
    public static final String CONFIG_PROPERTY_REPOSITORIES = "repositories";

    public static final String DASHBOARD_COMPONENT_BLOG_SUMMARY = "blog";
    public static final String DASHBOARD_COMPONENT_BUDDY_LIST = "buddyList";
    public static final String DASHBOARD_COMPONENT_FOLDER = "folder";
    public static final String DASHBOARD_COMPONENT_SEARCH = "search";
    public static final String DASHBOARD_COMPONENT_GALLERY = "gallery";    
    public static final String DASHBOARD_COMPONENT_WORKSPACE_TREE = "workspaceTree";
    
    public static final String SEARCH_OFFSET = "offset";
    public static final String SEARCH_MAX_HITS = "maxHits";
    public static final String SEARCH_SEARCH_FILTER = "searchFilter";
    public static final String SEARCH_SORT_BY = "sortBy";
    public static final String SEARCH_SORT_DESCEND = "sortDescend";

    //User properties
    public static final String USER_DISPLAY_STYLE_ACCESSIBLE = "accessible";
    public static final String USER_DISPLAY_STYLE_IFRAME = "iframe";
    public static final String USER_DISPLAY_STYLE_POPUP = "popup";
    public static final String USER_DISPLAY_STYLE_VERTICAL = "vertical";
    public static final String USER_PROPERTY_CALENDAR_VIEWMODE = "calendarViewMode";
    public static final String USER_PROPERTY_DASHBOARD_GLOBAL = "dashboard_global";
    public static final String USER_PROPERTY_DASHBOARD_SHOW_ALL = "dashboard_show_all";
    public static final String USER_PROPERTY_DEBUG = "debugMode";
    public static final String USER_PROPERTY_DISPLAY_STYLE = "displayStyle";
    public static final String USER_PROPERTY_DISPLAY_DEFINITION = "displayDefinition";
    public static final String USER_PROPERTY_SEARCH_FILTERS = "searchFilters";
    public static final String USER_PROPERTY_USER_FILTER = "userFilter";
    public static final String USER_PROPERTY_FAVORITES = "userFavorites";
    public static final String USER_PROPERTY_FOLDER_COLUMNS = "userFolderColumns";
    public static final String USER_PROPERTY_PERMALINK_URL = "userPermalinkUrl";
    public static final String USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS = "userSearchResultsFolderColumns";

    public static final String WORKAREA_OPERATION = "workarea_operation";
    
    //workflow definition names
    public static final String WORKFLOW_START_STATE = "__start_state";
    public static final String WORKFLOW_END_STATE = "__end_state";
    public static final String WORKFLOW_PARALLEL_THREAD_NAME = "name";
    public static final String WORKFLOW_PARALLEL_THREAD_START_STATE = "startState";
    public static final String WORKFLOW_PARALLEL_THREAD_END_TRANSITION = "transitionState";
    
    //Reserved data field names
    public static final String FIELD_POSTING_FROM="__poster";
    public static final String FIELD_ENTRY_TITLE="title";
    public static final String FIELD_ENTRY_DESCRIPTION="description";
    public static final String FIELD_ENTRY_ID="id";
    public static final String FIELD_PARENT_BINDER="parentBinder";
    public static final String FIELD_ENTRY_ATTACHMENTS="ss_attachFile";
    
    public static final String FIELD_PRINCIPAL_FOREIGNNAME="foreignName";
    public static final String FIELD_PRINCIPAL_NAME="name";
    public static final String FIELD_PRINCIPAL_ZONEID="zoneId";
    public static final String FIELD_PRINCIPAL_DISABLED="disabled";
    public static final String FIELD_PRINCIPAL_INTERNALID="internalId";
    
    public static final String FIELD_USER_FIRSTNAME="firstName";
    public static final String FIELD_USER_MIDDLENAME="middleName";
    public static final String FIELD_USER_LASTNAME="lastName";
    public static final String FIELD_USER_DISPLAYSTYLE="displayStyle";
    public static final String FIELD_USER_EMAIL="emailAddress";
    public static final String FIELD_USER_LOCALE="locale";
    public static final String FIELD_USER_TIMEZONE="timeZone";
    //map keys from definition module
    public static final String DEFINITION_ENTRY_DATA="entryData";
    public static final String DEFINITION_FILE_DATA="fileData";
    
}
