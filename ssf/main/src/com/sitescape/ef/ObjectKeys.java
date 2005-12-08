package com.sitescape.ef;

/**
 * Defines symbols for common model names that the core is aware of.
 * Model names are used to associate names (keys) with model objects 
 * (values). Model object are typically returned from business tier
 * to presentation tier as a map. 
 * 
 * @author Jong Kim
 *
 */
public interface ObjectKeys {
    /**
     * Note: Maintain the symbols in alphabetical order. 
     */
    
    public static final String COMMAND_TITLES="ss_commandTitles";
    public static final String DEFINITION_DEFAULT_FORM_NAME = "entryForm";
    public static final String FOLDER_ENTRY="ss_folderEntry";
    public static final String FOLDER_ENTRY_ANCESTORS="ss_folderEntryAncestors";
    public static final String FOLDER_ENTRY_DESCENDANTS="ss_folderEntryDescendants";
    public static final String FOLDER_ENTRY_TREE="ss_folderEntryTree";
    public static final String ENTRY_DEFINITION_ID="definitionId";
    public static final String ENTRY_TITLE="title";
    public static final String FOLDER="ss_folder";
    public static final String FOLDER_ANCESTORS="ss_folderAncestors";
    public static final String FOLDER_DESCENDANTS="ss_folderDescendants";
    public static final String FOLDER_ENTRIES="ss_folderEntries";
    public static final String FOLDER_SEENMAP="folder_seenmap";
    public static final String FORUM_DATA_DEFINITION="definition";
    public static final String FORUM_DATA_DEFINITION_REPLY="definition_reply";
    public static final String FORUM_DATA_DEFINITION_CONFIG="forum_definition_config";
    public static final String FORUM_DATA_FORUM="forum";
    public static final String FORUM_DATA_FORUM_DISPLAY_STYLE="forum_display_style";
    public static final String FORUM_DATA_FORUM_FOLDERS="forum_folders";
    public static final String FORUM_DATA_FORUM_TOOLBAR="forum_toolbar";
    public static final String FORUM_DATA_ENTRY="entry";
    public static final String FORUM_DATA_ENTRY_FILE="entry_file";
    public static final String FORUM_DATA_ENTRY_TOOLBAR="entry_toolbar";
    public static final String FORUM_DATA_FORUM_DEFINITION="forum_definition";
    public static final String FORUM_DATA_ENTRY_DEFINITIONS="entry_definitions";
    public static final String FORUM_DATA_PUBLIC_DEFINITIONS="public_definitions";
    public static final String FORUM_DATA_VIEW_FORUM="forum";
    public static final String FORUM_DATA_VIEW_FORUM_ENTRY="forum_entry";
    public static final String FORUM_DATA_VIEW_WORKSPACE="workspace";
    public static final String FORUM_DEFAULT_ENTRIES="defaultEntries";
    public static final String FORUM_DEFAULT_FORUM_VIEW="defaultForumDefinition";
    public static final String FORUM_NAME = "ss_forumName";
	public static final String FORUM_OPERATION_ADD_ENTRY = "add_entry";
	public static final String FORUM_OPERATION_ADD_REPLY = "add_reply";
	public static final String FORUM_OPERATION_ADMINISTRATION = "administration";
	public static final String FORUM_OPERATION_CONFIGURE_FORUM = "configure_forum";
	public static final String FORUM_OPERATION_DEFINITION_BUILDER = "definition_builder";
	public static final String FORUM_OPERATION_DELETE_ENTRY = "delete_entry";
	public static final String FORUM_OPERATION_DISPLAY_STYLE = "display_style";
	public static final String FORUM_OPERATION_MODIFY_ENTRY = "modify_entry";
	public static final String FORUM_OPERATION_SET_DISPLAY_STYLE = "set_display_style";
	public static final String FORUM_OPERATION_VIEW_FORUM = "view_forum";
	public static final String FORUM_OPERATION_VIEW_ENTRY = "view_entry";
	public static final String FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT = "view_entry_history_next";
	public static final String FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS = "view_entry_history_previous";
	public static final String FORUM_OPERATION_VIEW_ENTRY_NEXT = "view_entry_next";
	public static final String FORUM_OPERATION_VIEW_ENTRY_PREVIOUS = "view_entry_previous";
	public static final String FORUM_OPERATION_VIEW_FILE = "view_file";
	public static final String FORUM_PORTLET_NAME = "ss_forum";
	public static final String FORUM_URL_ENTRY_ID = "entryId";
    public static final String FORUM_URL_ENTRY_TYPE="entryType";
	public static final String FORUM_URL_FILE = "file";
	public static final String FORUM_URL_FORUM = "forum";
	public static final String FORUM_URL_FORUM_ID = "forumId";
	public static final String FORUM_URL_OPERATION = "op";
	public static final String FORUM_URL_STRUTS_ACTION = "struts_action";
	public static final String FORUM_URL_VALUE = "value";
	public static final String FORUM_URL_ZONE = "zone";
	public static final String FORUM_URL_ZONE_ID = "zoneId";
    public static final String INTERNAL_AUTHENTICATION = "ss_internalAuthentication";
    public static final String LOCALE = "ss_locale";
    public static final String PRINCIPAL_SIGNATURE = "ss_principalSignature";
    public static final String SEEN_MAP = "seen_map";
    public static final long SEEN_MAP_TIMEOUT = (long)30*24*60*60*1000;
    public static final long SEEN_HISTORY_MAP_TIMEOUT = (long)7*24*60*60*1000;
    public static final String SESSION_LAST_ENTRY_VIEWED = "last_entry_viewed";
    public static final String SESSION_LAST_HISTORY_ENTRY_VIEWED = "last_history_entry_viewed";
    public static final String USER = "ss_user";
    public static final String USER_NAME = "ss_userName";
    public static final String USER_PROPERTIES = "ss_userProperties";
    public static final String USER_PROPERTY_DISPLAY_STYLE = "display_style";
    public static final String USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL = "display_style_horizontal";
    public static final String USER_PROPERTY_DISPLAY_STYLE_VERTICAL = "display_style_vertical";
    public static final String USER_PROPERTY_DISPLAY_STYLE_ACCESSIBLE = "display_style_accessible";
    public static final String USER_SECURITY_INFO = "ss_userSecurityInfo";    
    public static final String VELOCITY_TEMPLATES = "ss_velocityTemplates";
    public static final String WORKSPACE_DOM_TREE="ss_wsDomTree";
    public static final String WORKSPACE_TREE="ss_wsTree";
    public static final String WORKSPACE="ss_workspace";
    public static final String FOLDER_DOM_TREE="ss_folderDomTree";

}
