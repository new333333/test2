package com.sitescape.ef.web;

public class WebKeys {
    public static final String DEFINITION_DEFAULT_FORM_NAME = "entryForm";
    public static final String LOCALE = "ss_locale";
    public static final String SESSION_LAST_ENTRY_VIEWED = "last_entry_viewed";
    public static final String SESSION_LAST_HISTORY_ENTRY_VIEWED = "last_history_entry_viewed";

    //URL parameters
    public static final String IS_ACTION_URL="actionUrl";
    public static final String FORUM_URL_ATTRIBUTE = "attr";
	public static final String FORUM_URL_ENTRY_ID = "entryId";
    public static final String FORUM_URL_ENTRY_TYPE="entryType";
	public static final String FORUM_URL_FILE = "file";
	public static final String FORUM_URL_FORUM = "forum";
	public static final String FORUM_URL_FORUM_ID = "forumId";
	public static final String FORUM_URL_OPERATION = "operation";
	public static final String FORUM_URL_VALUE = "value";
     //actions
    public static final String FORUM_ACTION_ADD_ENTRY = "add_entry";
	public static final String FORUM_ACTION_ADD_REPLY = "add_reply";
	public static final String FORUM_ACTION_CONFIGURE_FORUM = "configure_forum";
	public static final String FORUM_ACTION_DEFINITION_BUILDER = "definition_builder";
	public static final String FORUM_ACTION_DELETE_ENTRY = "delete_entry";
	public static final String FORUM_ACTION_MODIFY_ENTRY = "modify_entry";
	public static final String FORUM_ACTION_VIEW_FORUM = "view_forum";
	public static final String FORUM_ACTION_VIEW_ENTRY = "view_entry";
	public static final String LDAP_ACTION_CONFIGURE="configure_ldap";
	//oerations
 	public static final String FORUM_OPERATION_ADMINISTRATION = "administration";
	public static final String FORUM_OPERATION_SET_DISPLAY_STYLE = "set_display_style";
	public static final String FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT = "history_next";
	public static final String FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS = "history_previous";
	public static final String FORUM_OPERATION_VIEW_ENTRY_NEXT = "entry_next";
	public static final String FORUM_OPERATION_VIEW_ENTRY_PREVIOUS = "entry_previous";
	public static final String FORUM_OPERATION_VIEW_FILE = "view_file";
		
    // MODEL TAGS
	public static final String ACTION = "action";
    public static final String CONFIG_ELEMENT="ssConfigElement";
    public static final String CONFIG_DEFINITION="ssConfigDefinition";
    public static final String CONFIG_JSP_STYLE="ssConfigJspStyle";
    public static final String DEFINITION="ssDefinition";
    public static final String DEFINITION_ENTRY="ssDefinitionEntry";
    public static final String ENTRY_DEFINITION="ssEntryDefinition";
    public static final String ENTRY_DEFINTION_MAP="ssEntryDefinitionMap";
    public static final String ENTRY_ID = "ssEntryId";
    public static final String FOLDER = "ssFolder";
    public static final String FOLDER_DOM_TREE="ssFolderDomTree";
    public static final String FOLDER_ENTRY="ssFolderEntry";
    public static final String FOLDER_ENTRIES="ssFolderEntries";
    public static final String FOLDER_ENTRY_DESCENDANTS="ssFolderEntryDescendants";
    public static final String FOLDER_ENTRY_ANCESTORS="ssFolderEntryAncestors";
    public static final String FOLDER_ENTRY_TOOLBAR="ssFolderEntryToolbar";
    public static final String FOLDER_TOOLBAR="ssFolderToolbar";
    public static final String HISTORY_CACHE="ssHistoryCache";
    public static final String HISTORY_MAP="ssHistoryMap";
    public static final String PUBLIC_DEFINITIONS="ssPublicDefinitions";
    public static final String PUBLIC_ENTRY_DEFINITIONS="ssPublicEntryDefinitions";
    public static final String PUBLIC_FOLDER_DEFINITIONS="ssPublicFolderDefinitions";
    public static final String SEEN_MAP="ssSeenMap";
    public static final String DEFAULT_FOLDER_DEFINITION="ssDefaultFolderDefinition";
    public static final String DEFAULT_FOLDER_DEFINITION_ID="ssDefaultFolderDefinitionId";
    public static final String USER_PROPERTIES="ssUserProperties";
    public static final String WORKSPACE="ssWorkspace";
    public static final String WORKSPACE_DOM_TREE="ssWsDomTree";

    //View names
    public static final String VIEW_CONFIGURE = "forum/configure";
    public static final String VIEW_NO_DEFINITION="forum/view_default";
    public static final String VIEW_FORUM="forum/view_forum";
    public static final String VIEW="forum/view";
    public static final String VIEW_NO_ENTRY="forum/view_no_entry";
    public static final String VIEW_MODIFY_ENTRY="forum/modify_entry";
    public static final String VIEW_EDIT="forum/edit";
    public static final String VIEW_ADD_ENTRY = "forum/add_entry";
    public static final String VIEW_DEFINITION="definition_builder/view_definition_builder";
}
