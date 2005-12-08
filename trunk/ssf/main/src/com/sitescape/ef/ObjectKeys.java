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
    
    public static final String FORUM_ID="forumId";
    public static final String FOLDER_ENTRY_ANCESTORS="folderEntryAncestors";
    public static final String FOLDER_ENTRY_DESCENDANTS="folderEntryDescendants";
    public static final String FOLDER_ENTRY="folderEntry";
    public static final String FOLDER="folder";
    public static final String FOLDER_ENTRIES="folderEntries";
    public static final long SEEN_MAP_TIMEOUT = (long)30*24*60*60*1000;
    public static final long SEEN_HISTORY_MAP_TIMEOUT = (long)7*24*60*60*1000;
    public static final String USER = "user";
    
    public static final String USER_PROPERTY_DISPLAY_STYLE = "display_style";
    public static final String USER_PROPERTY_DISPLAY_STYLE_ACCESSIBLE = "display_style_accessible";
    public static final String USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL = "display_style_horizontal";
    public static final String USER_PROPERTY_DISPLAY_STYLE_IFRAME = "display_style_iframe";
    public static final String USER_PROPERTY_DISPLAY_STYLE_VERTICAL = "display_style_vertical";

}
