package com.kablink.kablinkapp;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import com.kablink.kablinkapp.R;
import com.kablink.kablinkapp.SiteData.SiteColumns;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Kablink extends Activity {
	
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

    /** Preferences */
    protected static final String PREFS = "preferences";
    protected static final String PREFS_SEED = "seed";

    private WebView mWebView;
    private WebBackForwardList mWebBackForwardList;
    private Uri mUri;
    private Cursor mCursor;
	private Double mOneTimeCodeLong;
	private String mOneTimeCode = "";
	private String mKablinkStartUrl = "/ssf/a/do?p_name=ss_mobile&p_action=0&action=__ajax_mobile&operation=mobile_app_login";
	private String mKablinkRedirectUrl = "/a/c/p_name/ss_mobile/p_action/0/action/__ajax_mobile/operation/mobile_show_front_page/operation2/whatsnew";
	private String mOneTimeCodeUrlPrefix = "&operation2=";
	private String mLoginUrl = "/ssf/s/portalLogin";
	protected static List<KablinkSite> mSites;
	protected static KablinkSite mCurrentSite;
	protected static boolean mViewingSite;
	protected static String mLastUrlViewed;
	protected static String mCryptoSeed = "123456789";
	private Boolean mResumingFromStop = false; 
	
	public class KablinkSite {
		private Long id;
		private String title;
		private String url;
		private String username;
		private String password;
		private String version;
		private Pattern pBaseUrl = Pattern.compile("(?i)^(https?://[^/]*)");
		private Pattern pHost = Pattern.compile("(?i)^https?://([^:/]*)");
		
		public KablinkSite(Long id, String title, String url, String username, String password, String version) {
			this.id = id;
			this.title = title;
			this.url = url.trim();
			this.username = username;
			this.password = password;
			this.version = version;
		}
		public Long getId() {return this.id;}
		public String getTitle() {return this.title;}
		public String getUrl() {return this.url;}
		public String getBaseUrl() {
			Matcher m = pBaseUrl.matcher(this.url);
			if (m.find()) {
				return m.group(1);
			}
			return this.url;
		}
		public String getUsername() {return this.username;}
		public String getPassword() {return this.password;}
		public String getVersion() {return this.version;}
		public String getUrlHost() {
			Matcher m = pHost.matcher(this.url);
			if (m.find()) {
				return m.group(1);
			}
			return null;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String action = getIntent().getAction();
        if (Intent.ACTION_SHUTDOWN.equals(action)) {
        	//This is a request to exit
        	clearCache();
        	mSites = null;
        	mCurrentSite = null;
        	mViewingSite = false;
        	mLastUrlViewed = null;
        	if (savedInstanceState != null) savedInstanceState.clear();
        	finish();
        	
        } else {
        	Intent intent = getIntent();
        	if (Intent.ACTION_GET_CONTENT.equals(action) || mResumingFromStop) {
            	//Switching to new site
            	clearCache();
            	mSites = null;
            	mViewingSite = false;
            	mLastUrlViewed = null;
            	if (savedInstanceState != null) savedInstanceState.clear();
            	mResumingFromStop = false;
        	}
        	intent.setAction(Intent.ACTION_MAIN);
            SharedPreferences preferences = getSharedPreferences(PREFS, 0);
            if (!preferences.contains(PREFS_SEED)) {
            	//Initialize the seed
            	Date now = new Date();
            	String seed = String.valueOf(now.getTime());
            	preferences.edit().putString(PREFS_SEED, seed).commit();
            	preferences = getSharedPreferences(PREFS, 0);
            }
            mCryptoSeed = preferences.getString(PREFS_SEED, "123456789");

	        //Set up the web view and javascript callout
	        mWebView = (WebView) findViewById(R.id.webview);
	        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
	        	//restore the history from before
	        	mWebView.restoreState(savedInstanceState);
	        }
	        mWebView.setWebViewClient(new MyWebViewClient()); 
	        mWebView.getSettings().setJavaScriptEnabled(true);
	        mWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");	
	        
	        
	        // Get the sites!
	        if (mSites == null || mSites.isEmpty()) {
		        mSites = new ArrayList<KablinkSite>();
		        mUri = SiteColumns.CONTENT_URI;
		        mCursor = managedQuery(mUri, PROJECTION, null, null, null);
		        if (mCursor != null) {
			        mCursor.moveToFirst();
		
			        //Build the list of sites
			        for (int i = 0; i < mCursor.getCount(); i++) {
			        	Long id = Long.valueOf(mCursor.getString(COLUMN_INDEX_ID));
			        	String title = mCursor.getString(COLUMN_INDEX_TITLE);
			        	String url = mCursor.getString(COLUMN_INDEX_URL);
			        	String username = mCursor.getString(COLUMN_INDEX_USERNAME);
			        	String encryptedPassword = mCursor.getString(COLUMN_INDEX_PASSWORD);
			        	String version = mCursor.getString(COLUMN_INDEX_VERSION);
			        	if (!title.equals("") && !url.equals("") && !username.equals("") && !encryptedPassword.equals("")) {
			        		String password = "";
			        		try {
			        			password = SimpleCrypto.decrypt(mCryptoSeed, encryptedPassword);
			        		} catch(Exception e) {
			        			// TODO fix error from crypto
			        		}
			        		KablinkSite site = new KablinkSite(id, title, url, username, password, version);
			        		mSites.add(site);
			        	}
			        	mCursor.moveToNext();
			        }
			        mCursor.close();
			        mCursor = null;
		        }
	        }
	        
	        //Depending on how many items are in the db, do various actions
	        if (mSites.size() == 0) {
	        	//There aren't any sites yet, go add one
	            // Launch activity to insert a new item
	            Intent i = new Intent(getBaseContext(), SiteEditor.class);
	            i.setAction(Intent.ACTION_INSERT);
	            startActivity(i);
	            
	        } else if(mCurrentSite != null) {
	            //There is a site already selected
	        	if (mViewingSite && mLastUrlViewed != null) {
	        		//Already viewing a site, just leave it on the current view
	        		mWebView.loadUrl(mLastUrlViewed);
	        	} else {
	        		launchSite(mCurrentSite);
	        	}
	        	
	        } else if (mSites.size() == 1) {
	        	//There is exactly one defined, go open it
	        	launchSite(mSites.get(0));
	        	
	        } else {
	        	//There are more than one. List them.
	            Intent i = new Intent(this, SiteList.class);
	            i.setAction(Intent.ACTION_VIEW);
	            startActivity(i);
	        }
        }
    }
    
    protected void clearCache() {
    	//Clear the cache that is left behind
    	getBaseContext().deleteDatabase("webview.db");
    	getBaseContext().deleteDatabase("webviewCache.db");
    }

    private void launchSite(KablinkSite site) {
    	mCurrentSite = site;
    	mViewingSite = true;

        //Start at the app url
        mOneTimeCode = getOneTimeCode();
    	Uri uri = Uri.parse(mCurrentSite.getBaseUrl() + mKablinkStartUrl + mOneTimeCodeUrlPrefix + mOneTimeCode);
        mWebView.loadUrl(uri.toString());

    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	mLastUrlViewed = url;
        	String urlHost = mCurrentSite.getUrlHost();
            if (urlHost != null && Uri.parse(url).getHost().equals(urlHost)) {
                // This is my web site, so do not override; let my WebView load the page
	            return false;
            } else {
	            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
	            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	            startActivity(intent);
	            return true;
            }
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        	handler.proceed(); 
        } 
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//Grab the back button and make it go back in the history list
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Use the back button to go back on page
        	if (mWebView.canGoBackOrForward(-2)) {
        		mWebView.goBack();
        		mLastUrlViewed = mWebView.getUrl();
        		return true;
        	} else {
        		//There is nothing left in the history list. Ask if the user wants to exit the app
        	    Intent intent = new Intent(getBaseContext(), ExitDialog.class);
			    intent.setAction(Intent.ACTION_SHUTDOWN);
			    startActivity(intent);
    	        return true;
        	}
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kablink_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        switch (item.getItemId()) {
        case R.id.menu_selectSite:
            // Launch activity to view the list of sites
            intent = new Intent(getBaseContext(), SiteList.class);
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
            return true;
        case R.id.menu_addSite:
            // Launch activity to insert a new item
        	mSites = null;
            intent = new Intent(getBaseContext(), SiteEditor.class);
            intent.setAction(Intent.ACTION_INSERT);
            startActivity(intent);
            return true;
        case R.id.menu_editSite:
        	mSites = null;
            intent = new Intent(getBaseContext(), SiteEditor.class);
            intent.setAction(Intent.ACTION_EDIT);
        	Long id = mCurrentSite.getId();
            Uri uri = ContentUris.withAppendedId(SiteColumns.CONTENT_URI, id);
            intent.setData(uri);
            startActivity(intent);
            return true;
        /**
        case R.id.menu_settings:
            // Launch activity to insert a new category
            intent = new Intent(getBaseContext(), PreferenceSettings.class);
            intent.setAction(Intent.ACTION_INSERT);
            startActivity(intent);
            return true;
        */
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        public void doAppLogin(String oneTimeKey) {
        	if (mOneTimeCode.equals(oneTimeKey) && !mOneTimeCode.equals("")) {
	        	//This is a request to login
		        Uri uri = Uri.parse(mCurrentSite.getBaseUrl() + mLoginUrl);
		        Uri uri2 = Uri.parse(mKablinkRedirectUrl);
		        String postData = "j_username="+mCurrentSite.getUsername()+"&j_password="+mCurrentSite.getPassword();
		        postData += "&spring-security-redirect=" + uri2;
		        mWebView.postUrl(uri.toString(), EncodingUtils.getBytes(postData, "BASE64"));
        	}
        	mOneTimeCode = "";
        }
    }
    
    public String getOneTimeCode() {
    	if (mOneTimeCodeLong == null) {
    		mOneTimeCodeLong = Math.random();
    	}
    	mOneTimeCodeLong++;
    	return String.valueOf(mOneTimeCodeLong);
    }
    
    protected void onSaveInstanceState (Bundle outState) {
    	//Save the history
    	mWebBackForwardList = mWebView.saveState(outState);
    }
    
    @Override
    protected void onResume() {
        //if (Config.LOGD) Log.d(TAG, "onResume");
        super.onResume();
        if (mResumingFromStop) {
        	mSites = null;
        	mViewingSite = false;
        	mLastUrlViewed = null;
        	mResumingFromStop = false;

	        Intent i = new Intent(getBaseContext(), Kablink.class);
	        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(i);
        }
    }
    
    @Override
    protected void onPause() {
        //Log.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //On restart, make sure to go through the normal startup
    	mResumingFromStop = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
