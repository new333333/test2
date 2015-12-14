/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kablink.kablinkapp;

import com.kablink.kablinkapp.R;
import com.kablink.kablinkapp.SiteData.SiteColumns;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SiteEditor extends Activity {
    private static String TAG = "SiteEditor";
    
    private String mDefaultVersion = "3.1";

    private static final String[] PROJECTION = new String[] {
    	SiteColumns._ID, // 0
    	SiteColumns.TITLE, // 1
    	SiteColumns.URL, // 2
    	SiteColumns.USERNAME, // 3
    	SiteColumns.PASSWORD, // 4
    	SiteColumns.VERSION // 5
    };
    
    /** The indexes of the columns */
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_TITLE = 1;
    private static final int COLUMN_INDEX_URL = 2;
    private static final int COLUMN_INDEX_USERNAME = 3;
    private static final int COLUMN_INDEX_PASSWORD = 4;
    private static final int COLUMN_INDEX_VERSION = 5;
    
    // This is our state data that is stored when freezing.
    private static final String ORIGINAL_CONTENT_TITLE = "origContentTitle";
    private static final String ORIGINAL_CONTENT_URL = "origContentUrl";
    private static final String ORIGINAL_CONTENT_USERNAME = "origContentUsername";
    private static final String ORIGINAL_CONTENT_PASSWORD = "origContentPassword";
    private static final String ORIGINAL_CONTENT_VERSION = "origContentVersion";

    // The different distinct states the activity can be run in.
    private static final int STATE_VIEW = 0;
    private static final int STATE_EDIT = 1;
    private static final int STATE_INSERT = 2;
    private static final int STATE_DELETE = 3;

    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mTitle;
    private EditText mUrl;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mVersion;
    private String mOriginalTitle;
    private String mOriginalUrl;
    private String mOriginalUsername;
    private String mOriginalPassword;
    private String mOriginalVersion;
    private static Button mSaveButton;
    private static Button mCancelButton;
    private static Button mDeleteButton;
    private AlertDialog.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        // Do some setup based on the action being performed.

        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            // Requested to edit: set that state, and the data being edited.
            mState = STATE_EDIT;
            mUri = intent.getData();
        } else if (Intent.ACTION_INSERT.equals(action)) {
            // Requested to insert: set that state, and create a new entry
            // in the container.
            mState = STATE_INSERT;
            mUri = getContentResolver().insert(SiteColumns.CONTENT_URI, null);

            // If we were unable to create a new note, then just finish
            // this activity.  A RESULT_CANCELED will be sent back to the
            // original activity if they requested a result.
            if (mUri == null) {
                //Log.e(TAG, "Failed to insert new phrase into " + getIntent().getData());
                finish();
                return;
            }

            // The new entry was created, so assume all will end well and
            // set the result to be returned.
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

        } else if (Intent.ACTION_DELETE.equals(action)) {
            // Requested to delete: set that state, and delete the entry
            mState = STATE_DELETE;
            mUri = intent.getData();
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
            getContentResolver().delete(mUri, null, null);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // The entry was deleted, so assume all will end well and
            // set the result to be returned.
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

        } else {
            // Whoops, unknown action!  Bail.
            //Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

       setContentView(R.layout.site_editor);

       // Get the site record!
       mCursor = managedQuery(mUri, PROJECTION, null, null, null);
       mCursor.moveToFirst();

       // The title view for our phrase, identified by its ID in the XML file.
       mTitle = (EditText) findViewById(R.id.siteTitle);
       if (mCursor.getCount() > 0) mTitle.setText(mCursor.getString(COLUMN_INDEX_TITLE));
       mTitle.requestFocus();

       // The url view for our site, identified by its ID in the XML file.
       mUrl = (EditText) findViewById(R.id.siteUrl);
       mUrl.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
       if (mCursor.getCount() > 0) mUrl.setText(mCursor.getString(COLUMN_INDEX_URL));

       // The username view for our site, identified by its ID in the XML file.
       mUsername = (EditText) findViewById(R.id.loginName);
       mUsername.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
       if (mCursor.getCount() > 0) mUsername.setText(mCursor.getString(COLUMN_INDEX_USERNAME));

       // The password view for our site, identified by its ID in the XML file.
       mPassword = (EditText) findViewById(R.id.password);
       if (mCursor.getCount() > 0) {
    	   String encryptedPassword = mCursor.getString(COLUMN_INDEX_PASSWORD);
    	   String password = "";
		   try {
			   password = SimpleCrypto.decrypt(Kablink.mCryptoSeed, encryptedPassword);
		   } catch (Exception e) {
			   // Error, assume blank
		   }
    	   mPassword.setText(password);
       }

       // The version view for our site, identified by its ID in the XML file.
       //mVersion = (EditText) findViewById(R.id.version);
       //if (mCursor.getCount() > 0) mVersion.setText(mCursor.getString(COLUMN_INDEX_VERSION));


       mSaveButton = (Button) findViewById(R.id.saveButton);
       mSaveButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Intent i = new Intent(v.getContext(), Kablink.class);
               i.setData(intent.getData());
               i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(i);
           }
       });

       mCancelButton = (Button) findViewById(R.id.cancelButton);
       mCancelButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Log.d(TAG, "mDoneButton clicked");
               Intent i = new Intent(v.getContext(), Kablink.class);
               i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(i);
           }
       });

       mDeleteButton = (Button) findViewById(R.id.deleteButton);
       if (Intent.ACTION_INSERT.equals(action)) {
    	   mDeleteButton.setVisibility(Button.GONE);
       }
       mDeleteButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               Log.d(TAG, "mDeleteButton clicked");
               mBuilder = new AlertDialog.Builder(v.getContext());
               mBuilder.setMessage(getString(R.string.deleteSiteWarning))
                      .setCancelable(false)
                      .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int id) {
                        	  deleteSite();
                              Intent i = new Intent(getBaseContext(), Kablink.class);
                              i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                              startActivity(i);
                          }
                      })
                      .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                          }
                      });
		        AlertDialog alert = mBuilder.create();
		        alert.show();
           }
       });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If we didn't have any trouble retrieving the data, it is now
        // time to get at the stuff.
        if (mCursor != null) {
            // Make sure we are at the one and only row in the cursor.
            mCursor.moveToFirst();

            // Modify our overall title depending on the mode we are running in.
            if (mState == STATE_VIEW) {
                setTitle(getText(R.string.title_view));
            } else if (mState == STATE_EDIT) {
                    setTitle(getText(R.string.title_edit));
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.title_create));
            } else if (mState == STATE_DELETE) {
                setTitle(getText(R.string.title_delete));
            }

            // This is a little tricky: we may be resumed after previously being
            // paused/stopped.  We want to put the new text in the text view,
            // but leave the user where they were (retain the cursor position
            // etc).  This version of setText does that for us.
            String url = "";
            if (mCursor.getCount() > 0) url = mCursor.getString(COLUMN_INDEX_URL);
            mUrl.setTextKeepState(url);
            
            String title = "";
            if (mCursor.getCount() > 0) title = mCursor.getString(COLUMN_INDEX_TITLE);
            mTitle.setTextKeepState(title);
            
            String username = "";
            if (mCursor.getCount() > 0) username = mCursor.getString(COLUMN_INDEX_USERNAME);
            
            String password = "";
            if (mCursor.getCount() > 0) password = mCursor.getString(COLUMN_INDEX_PASSWORD);
            
            // If we hadn't previously retrieved the original text, do so
            // now.  This allows the user to revert their changes.
            if (mOriginalTitle == null) {
                mOriginalTitle = title;
            }
            if (mOriginalUrl == null) {
            	mOriginalUrl = url;
            }
            if (mOriginalUsername == null) {
            	mOriginalUsername = username;
            }
            if (mOriginalPassword == null) {
            	mOriginalPassword = password;
            }

        } else {
            setTitle("");
            mTitle.setText("");
            mUrl.setText("");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        outState.putString(ORIGINAL_CONTENT_TITLE, mOriginalTitle);
        outState.putString(ORIGINAL_CONTENT_URL, mOriginalUrl);
        outState.putString(ORIGINAL_CONTENT_USERNAME, mOriginalUsername);
        outState.putString(ORIGINAL_CONTENT_PASSWORD, mOriginalPassword);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // The user is going somewhere else, so make sure their current
        // changes are safely saved away in the provider.  We don't need
        // to do this if only editing.
        if (mCursor != null) {
            String titleText = mTitle.getText().toString();
            int length = titleText.length();

            String url = mUrl.getText().toString();
            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();
            //String version = mVersion.getText().toString();

            // If this activity is finished, and there is no text, then we
            // do something a little special: simply delete the note entry.
            // Note that we do this both for editing and inserting...  it
            // would be reasonable to only do it when inserting.
            if (isFinishing() && (length == 0)) {
                setResult(RESULT_CANCELED);
                deleteSite();

            // Get out updates into the provider.
            } else {
                ContentValues values = new ContentValues();

                // Write our text back into the provider.
                values.put(SiteColumns.TITLE, titleText);
                values.put(SiteColumns.URL, url);
                values.put(SiteColumns.USERNAME, username);
                try {
                	values.put(SiteColumns.PASSWORD, SimpleCrypto.encrypt(Kablink.mCryptoSeed, password));
                } catch(Exception e) {
                	// TODO fix error on encrypting
                }
                values.put(SiteColumns.VERSION, mDefaultVersion);

                // Commit all of our changes to persistent storage. When the update completes
                // the content provider will notify the cursor of the change, which will
                // cause the UI to be updated.
                getContentResolver().update(mUri, values, null, null);
            }
        }
        //Force the database to re-reed the site list
        Kablink.mSites = null;
        Kablink.mCurrentSite = null;
        Kablink.mLastUrlViewed = null;
    }

    /**
     * Take care of deleting a site.  Simply deletes the entry.
     */
    private final void deleteSite() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            mTitle.setText("");
            mUrl.setText("");
            mUsername.setText("");
            mPassword.setText("");
        }
        Kablink.mCurrentSite = null;
        setResult(RESULT_CANCELED);
    }

}
