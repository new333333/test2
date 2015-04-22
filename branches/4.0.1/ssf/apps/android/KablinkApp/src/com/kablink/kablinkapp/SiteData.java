package com.kablink.kablinkapp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for Kablink Sites
 */
public final class SiteData {
    public static final String AUTHORITY = "com.kablink.kablinkapp";

    // This class cannot be instantiated
    private SiteData() {}
    
    /**
     * Sites table
     */
    public static final class SiteColumns implements BaseColumns {
        // This class cannot be instantiated
        private SiteColumns() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/sites");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of sites.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.kablinkapp.site";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single site.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.kablinkapp.site";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "title";

        /**
         * The title of the site
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The site url 
         * <P>Type: TEXT</P>
         */
        public static final String URL = "url";

        /**
         * The username 
         * <P>Type: TEXT</P>
         */
        public static final String USERNAME = "username";

        /**
         * The password 
         * <P>Type: TEXT</P>
         */
        public static final String PASSWORD = "password";

        /**
         * The version of the site 
         * <P>Type: TEXT</P>
         */
        public static final String VERSION = "version";

     }
}
