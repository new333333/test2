package com.sitescape.team;

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
	public static final String DEFAULT_WORKSPACE_DEF="402883b90cc53079010cc539bf260005";
	public static final String DEFAULT_USER_WORKSPACE_DEF="402883b90cc53079010cc539bf26000b";
	public static final String DEFAULT_PROFILES_DEF="402883b90cc53079010cc539bf260006";
	public static final String DEFAULT_USER_DEF="402883b90cc53079010cc539bf260007";
	public static final String DEFAULT_GROUP_DEF="402883b90cc53079010cc539bf260008";
	public static final String DEFAULT_FOLDER_BLOG_DEF="402883b90cc53079010cc539bf26000a";
	public static final String DEFAULT_FOLDER_CALENDAR_DEF="402883b90cc53079010cc539bf26000c";
	public static final String DEFAULT_FOLDER_WIKI_DEF="402883b90cc53079010cc539bf26000d";

	
	
	public static final String ADMINISTRATOR_ROLE_ID="402883b90cc53079010cc539bf260009";
	public static final String DEFAULT_WORKSPACE_CONFIG="402883b90d0de1f3010d0df5582b0002";
	public static final String DEFAULT_USER_WORKSPACE_CONFIG="402883b90d0de1f3010d0df5582b0004";
	public static final String DEFAULT_FOLDER_CONFIG="402883b90d0de1f3010d0df5582b0001";
	public static final String DEFAULT_FOLDER_BLOG_CONFIG="402883b90d0de1f3010d0df5582b000c";
	public static final String DEFAULT_FOLDER_WIKI_CONFIG="402883b90d0de1f3010d0df5582b000d";
	public static final String DEFAULT_FOLDER_CALENDAR_CONFIG="402883b90d0de1f3010d0df5582b000e";

	public static final String PROFILE_ROOT_INTERNALID="402883b90d0de1f3010d0df5582b0005";
	public static final String ALL_USERS_GROUP_INTERNALID="402883b90d0de1f3010d0df5582b0006";
	public static final String TOP_WORKSPACE_INTERNALID="402883b90d0de1f3010d0df5582b0007";
	public static final String ANONYMOUS_POSTING_USER_INTERNALID="402883b90d0de1f3010d0df5582b0008";
	public static final String TEAM_ROOT_INTERNALID="402883b90d0de1f3010d0df5582b0009";
	public static final String GLOBAL_ROOT_INTERNALID="402883b90d0de1f3010d0df5582b000a";
	//super user bypasses all acl and operation checks
	public static final String SUPER_USER_INTERNALID="402883b90d0de1f3010d0df5582b000b";
	//reserved id put in acls to indicate owner of object has access
	//search engine stores then ownerId of the entry in place of this
	public static final Long OWNER_USER_ID = Long.valueOf(-1);

    //custom property prefix from ssf.properties
	public static final String CUSTOM_PROPERTY_PREFIX="custom.";

    //map keys returned from modules
    public static final String BINDER="binder";
    public static final String SEARCH_ENTRIES="search_entries";
    public static final String FULL_ENTRIES="database_entries";
    public static final String FOLDER_ENTRY_ANCESTORS="folderEntryAncestors";
    public static final String FOLDER_ENTRY_DESCENDANTS="folderEntryDescendants";
    public static final String FOLDER_ENTRY="folderEntry";
    public static final String SEARCH_COUNT_TOTAL="searchCountTotal";
    public static final String TOTAL_SEARCH_COUNT="totalSearchCount";
    public static final String TOTAL_SEARCH_RECORDS_RETURNED="totalSearchRecordsReturned";
    public static final String FILES_FROM_APPLET_FOR_BINDER="filesFromApplet";
    public static final String FILES_FROM_APPLET_FOLDER_INFO_FOR_BINDER="filesFromAppletFolderInfo";
    
    //map keys from definition module
    public static final String DEFINITION_ENTRY_DATA="entryData";
    public static final String DEFINITION_FILE_DATA="fileData";
    //map keys from admin/sendMail
    public static final String SENDMAIL_ERRORS="errors";
    public static final String SENDMAIL_DISTRIBUTION="distributionList";
    public static final String SENDMAIL_STATUS="status";
    public static final String SENDMAIL_STATUS_SENT="sent";
    public static final String SENDMAIL_STATUS_FAILED="failed";
    public static final String SENDMAIL_STATUS_SCHEDULED="scheduled";
    	
    
    public static final int LISTING_MAX_PAGE_SIZE = 100;
    public static final long SEEN_MAP_TIMEOUT = (long)30*24*60*60*1000;
    public static final long SEEN_HISTORY_MAP_TIMEOUT = (long)7*24*60*60*1000;
    
    public static final String BINDER_PROPERTY_DASHBOARD = "dashboard";
    public static final String BINDER_PROPERTY_WIKI_HOMEPAGE = "wikiHomepage";

    public static final String CONFIG_PROPERTY_REPOSITORY = "repository";
    public static final String CONFIG_PROPERTY_REPOSITORIES = "repositories";

    public static final String DASHBOARD_COMPONENT_BLOG_SUMMARY = "blog";
    public static final String DASHBOARD_COMPONENT_GUESTBOOK_SUMMARY = "guestbook";
    public static final String DASHBOARD_COMPONENT_BUDDY_LIST = "buddyList";
    public static final String DASHBOARD_COMPONENT_TEAM_MEMBERS_LIST = "teamMembersList";
    public static final String DASHBOARD_COMPONENT_FOLDER = "folder";
    public static final String DASHBOARD_COMPONENT_SEARCH = "search";
    public static final String DASHBOARD_COMPONENT_GALLERY = "gallery";    
    public static final String DASHBOARD_COMPONENT_WIKI_SUMMARY = "wiki";    
    public static final String DASHBOARD_COMPONENT_WORKSPACE_TREE = "workspaceTree";
    
    public static final String SEARCH_OFFSET = "offset";
    public static final String SEARCH_MAX_HITS = "maxHits";
    public static final int SEARCH_MAX_HITS_DEFAULT = 10;
    public static final String SEARCH_SEARCH_FILTER = "searchFilter";
    public static final String SEARCH_SORT_BY = "sortBy";
    public static final String SEARCH_SORT_DESCEND = "sortDescend";
    public static final String SEARCH_START_DATE = "startDate";
    public static final String SEARCH_END_DATE = "endDate";
    public static final String SEARCH_FILTER_AND = "searchFilterAnd";
    public static final String SEARCH_FILTER_OR = "searchFilterOr";
    public static final String PAGE_ENTRIES_PER_PAGE = "entriesPerPage";
    public static final String SEARCH_PAGE_ENTRIES_PER_PAGE = "entriesPerSearchPage";
    public static final String SEARCH_YEAR_MONTH = "yearMonth";
    public static final String SEARCH_COMMUNITY_TAG = "searchCommunityTag";
    public static final String SEARCH_PERSONAL_TAG = "searchPersonalTag";
    public static final String SEARCH_TITLE = "searchTitle";
    
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

    
    //workflow definition names
    public static final String WORKFLOW_START_STATE = "__start_state";
    public static final String WORKFLOW_END_STATE = "__end_state";
    public static final String WORKFLOW_PARALLEL_THREAD_NAME = "name";
    public static final String WORKFLOW_PARALLEL_THREAD_START_STATE = "startState";
    public static final String WORKFLOW_PARALLEL_THREAD_END_TRANSITION = "transitionState";
    
    //reserved input tags
    public static final String INPUT_FIELD_POSTING_FROM="__poster";
    public static final String INPUT_FIELD_ENTITY_ATTACHMENTS="ss_attachFile";
    public static final String INPUT_FIELD_GROUP_MEMBERNAME="memberName";

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
    
    public static final String FIELD_BINDER_LIBRARY="library";
    public static final String FIELD_BINDER_UNIQUETITLES="uniqueTitles";

    public static final String FIELD_USER_FIRSTNAME="firstName";
    public static final String FIELD_USER_MIDDLENAME="middleName";
    public static final String FIELD_USER_LASTNAME="lastName";
    public static final String FIELD_USER_DISPLAYSTYLE="displayStyle";
    public static final String FIELD_USER_EMAIL="emailAddress";
    public static final String FIELD_USER_LOCALE="locale";
    public static final String FIELD_USER_TIMEZONE="timeZone";
    public static final String FIELD_PRINCIPAL_FOREIGNNAME="foreignName";
    public static final String FIELD_PRINCIPAL_NAME="name";
    public static final String FIELD_PRINCIPAL_DISABLED="disabled";
    public static final String FIELD_GROUP_MEMBERS="members";
 
    
    //xml tags - used in GenerateLdapList (as strings) - keep in sync
    public static final String XTAG_ID="id";
    public static final String XTAG_NAME="name";
    public static final String XTAG_TYPE="type";
    public static final String XTAG_ATTRIBUTE="attribute";
    public static final String XTAG_ATTRIBUTE_SET="attribute-set";
    public static final String XTAG_PROPERTY="property";
    public static final String XTAG_OPERATION="operation";
    public static final String XTAG_MODIFIEDBY="modifedBy";
    public static final String XTAG_MODIFIEDON="modifedOn";
    
    
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

    public static final String XTAG_HISTORY_BY="author";
    public static final String XTAG_HISTORY_WHEN="when";

    public static final String XTAG_ENTITY_TITLE="title";
    public static final String XTAG_ENTITY_DESCRIPTION="description";
    public static final String XTAG_ENTITY_PARENTBINDER="parentBinder";
    public static final String XTAG_ENTITY_LOGVERSION="logVersion";
    public static final String XTAG_ENTITY_DEFINITION="entryDef";
    public static final String XTAG_ENTITY_ICONNAME="iconName";
    public static final String XTAG_ENTITY_CREATION="created";
    public static final String XTAG_ENTITY_MODIFICATION="modified";
    public static final String XTAG_FIELD_ENTITY_ATTACHMENTS="attachments";
   
    
    public static final String XTAG_PRINCIPAL_FOREIGNNAME="foreignName";
    public static final String XTAG_PRINCIPAL_NAME="name";
    public static final String XTAG_PRINCIPAL_DISABLED="disabled";
    public static final String XTAG_PRINCIPAL_INTERNALID="internalId";
    
    public static final String XTAG_USER_FIRSTNAME="firstName";
    public static final String XTAG_USER_MIDDLENAME="middleName";
    public static final String XTAG_USER_LASTNAME="lastName";
    public static final String XTAG_USER_DISPLAYSTYLE="displayStyle";
    public static final String XTAG_USER_EMAIL="emailAddress";
    public static final String XTAG_USER_LOCALE="locale";
    public static final String XTAG_USER_TIMEZONE="timeZone";
    public static final String XTAG_USER_PASSWORD="password";
    public static final String XTAG_USER_DIGESTSEED="digestSeed";
    public static final String XTAG_USER_ZONNAME="zonName";
    public static final String XTAG_USER_ORGANIZATION="organization";
    public static final String XTAG_USER_LOGINDATE="loginDate";
    public static final String XTAG_USER_PHONE="phone";
    
    public static final String XTAG_GROUP_MEMBERS="members";
    public static final String XTAG_GROUP_MEMBER_NAME="memberName";
    
    public static final String XTAG_FOLDERENTRY_DOCNUMBER="docNumber";
    public static final String XTAG_FOLDERENTRY_TOPENTRY="topEntry";
    public static final String XTAG_FOLDERENTRY_PARENTENTRY="parentEntry";
    public static final String XTAG_FOLDERENTRY_POSTEDBY="postedBy";
    
    public static final String XTAG_FILE_NAME="fileName";
    public static final String XTAG_FILE_LENGTH="fileLength";
    public static final String XTAG_FILE_REPOSITORY="repository";
    public static final String XTAG_FILE_LAST_VERSION="lastVersion";

    public static final String XTAG_FILE_PARENT="parentAttachment";
    public static final String XTAG_FILE_VERSION_NUMBER="versionNumber";
    public static final String XTAG_FILE_VERSION_NAME="versionName";
    
    public static final String XTAG_FILE_ARCHIVE_URI="archiveURI";
    
    public static final String XTAG_WA_FUNCTION="function";
    public static final String XTAG_WA_MEMBERS="members";

    
    public static final String XTAG_BINDER_LIBRARY="library";
    public static final String XTAG_BINDER_INHERITMEMBERSHIP="inheritMembership";
    public static final String XTAG_BINDER_INHERITDEFINITIONS="inheritDefinitions";
    
    public static final String XTAG_WF_CHANGE="workflowChange";
    public static final String XTAG_WFS_DEFINITION="definition";
    public static final String XTAG_WFS_TIMER="timer";
    public static final String XTAG_WFS_THREAD="thread";
     
    public static final String XTAG_WFR_DEFINITION="definition";
    public static final String XTAG_WFR_RESPONDER="responder";
    public static final String XTAG_WFR_RESPONSEDATE="responseDate";
    public static final String XTAG_WFR_RESPONSE="response";
    

     
}
