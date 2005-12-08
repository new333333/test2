package com.sitescape.ef;

/**
 * Defines symbols for that the core is aware of.
 * 
 * @author Jong Kim
 *
 */
public interface ObjectKeys {
    /**
     * Note: Maintain the symbols in alphabetical order. 
     */
    
    public static final String FOLDER_ENTRY_ANCESTORS="folderEntryAncestors";
    public static final String FOLDER_ENTRY_DESCENDANTS="folderEntryDescendants";
    public static final String FOLDER_ENTRY="folderEntry";
    public static final String FOLDER="folder";
    public static final String FOLDER_ENTRIES="folderEntries";
    public static final int FOLDER_MAX_PAGE_SIZE = 1000;
    public static final long SEEN_MAP_TIMEOUT = (long)30*24*60*60*1000;
    public static final long SEEN_HISTORY_MAP_TIMEOUT = (long)7*24*60*60*1000;
    public static final String USER = "user";
    
    public static final String USER_DISPLAY_STYLE_ACCESSIBLE = "accessible";
    public static final String USER_DISPLAY_STYLE_HORIZONTAL = "horizontal";
    public static final String USER_DISPLAY_STYLE_IFRAME = "iframe";
    public static final String USER_DISPLAY_STYLE_POPUP = "popup";
    public static final String USER_DISPLAY_STYLE_VERTICAL = "vertical";

    public static final String WORKAREA_OPERATION = "workarea_operation";
    
    public static final String WORKFLOW_START_STATE = "__start_state";
    public static final String WORKFLOW_END_STATE = "__end_state";
}
