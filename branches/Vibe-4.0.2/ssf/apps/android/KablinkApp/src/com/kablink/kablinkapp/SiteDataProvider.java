package com.kablink.kablinkapp;

import java.util.HashMap;

import com.kablink.kablinkapp.SiteData.SiteColumns;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of sites. Each site has a title, a url,
 * username, password, and a version.
 */
public class SiteDataProvider extends ContentProvider {

    private static final String TAG = "SiteDataProvider";

    private static final String DATABASE_NAME = "kablinkapp.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SITES_TABLE_NAME = "kablink_sites";

    private static HashMap<String, String> sSitesProjectionMap;
    private static UriMatcher sUriMatcher;

    protected static final int SITES = 1;
    protected static final int SITE_ID = 2;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SITES_TABLE_NAME + " ("
                    + SiteColumns._ID + " INTEGER PRIMARY KEY,"
                    + SiteColumns.TITLE + " TEXT,"
                    + SiteColumns.URL + " TEXT,"
                    + SiteColumns.USERNAME + " TEXT,"
                    + SiteColumns.PASSWORD + " TEXT,"
                    + SiteColumns.VERSION + " TEXT"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS sites");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SITES_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case SITES:
            qb.setProjectionMap(sSitesProjectionMap);
            break;

        case SITE_ID:
            qb.setProjectionMap(sSitesProjectionMap);
            qb.appendWhere(SiteColumns._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = SiteColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case SITES:
            return SiteColumns.CONTENT_TYPE;
        
        case SITE_ID:
            return SiteColumns.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != SITES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(SiteColumns.TITLE) == false) {
            values.put(SiteColumns.TITLE, "");
        }

        if (values.containsKey(SiteColumns.URL) == false) {
            values.put(SiteColumns.URL, "");
        }

        if (values.containsKey(SiteColumns.USERNAME) == false) {
        	values.put(SiteColumns.USERNAME, "");
        }

        if (values.containsKey(SiteColumns.PASSWORD) == false) {
        	values.put(SiteColumns.PASSWORD, "");
        }

        if (values.containsKey(SiteColumns.VERSION) == false) {
        	values.put(SiteColumns.VERSION, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(SITES_TABLE_NAME, SiteColumns.URL, values);
        if (rowId > 0) {
            Uri siteUri = ContentUris.withAppendedId(SiteColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(siteUri, null);
            return siteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case SITES:
            //Never delete the whole database. This would be a bug
        	//count = db.delete(SITES_TABLE_NAME, where, whereArgs);
        	count = 0;
            break;

        case SITE_ID:
            String siteId = uri.getPathSegments().get(1);
            count = db.delete(SITES_TABLE_NAME, SiteColumns._ID + "=" + siteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case SITES:
            count = db.update(SITES_TABLE_NAME, values, where, whereArgs);
            break;

        case SITE_ID:
            String siteId = uri.getPathSegments().get(1);
            count = db.update(SITES_TABLE_NAME, values, SiteColumns._ID + "=" + siteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SiteData.AUTHORITY, "sites", SITES);
        sUriMatcher.addURI(SiteData.AUTHORITY, "sites/#", SITE_ID);

        sSitesProjectionMap = new HashMap<String, String>();
        sSitesProjectionMap.put(SiteColumns._ID, SiteColumns._ID);
        sSitesProjectionMap.put(SiteColumns.TITLE, SiteColumns.TITLE);
        sSitesProjectionMap.put(SiteColumns.URL, SiteColumns.URL);
        sSitesProjectionMap.put(SiteColumns.USERNAME, SiteColumns.USERNAME);
        sSitesProjectionMap.put(SiteColumns.PASSWORD, SiteColumns.PASSWORD);
        sSitesProjectionMap.put(SiteColumns.VERSION, SiteColumns.VERSION);

    }
}
