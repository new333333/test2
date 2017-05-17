package com.kablink.kablinkapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.kablink.kablinkapp.Kablink.KablinkSite;

public class SiteList extends Activity {
    private static String TAG = "SiteList";

    private TableLayout mSiteListTableView;
    private static Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.site_list);
        
        //Show the list of sites
        mSiteListTableView = (TableLayout) findViewById(R.id.siteListTable);
        
     	//Build the site table
        int buttonsPerRow = 1;
    	int buttonCount = 0;
    	TableRow row = null;
        for (KablinkSite site : Kablink.mSites) {
    		//Output the site
    		Button textBtn = new Button(this);
        	textBtn.setText(site.getTitle());
        	textBtn.setTypeface(Typeface.DEFAULT);
        	textBtn.setHorizontallyScrolling(false);
        	textBtn.setHapticFeedbackEnabled(true);
        	textBtn.setTag(R.id.tag_button_site, site);
        	
        	textBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String action = Intent.ACTION_MAIN;
					Button thisButton = (Button) v;
					KablinkSite buttonSite = (KablinkSite)thisButton.getTag(R.id.tag_button_site);
					if (!buttonSite.equals(Kablink.mCurrentSite)) {
						//Changing sites
						Kablink.mCurrentSite = buttonSite;
						Kablink.mViewingSite = false;
						action = Intent.ACTION_GET_CONTENT;
					}
	                Intent i = new Intent(v.getContext(), Kablink.class);
	                i.setAction(action);
	                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                startActivity(i);
				}
			});
	        	
        	if (row == null || buttonCount >= buttonsPerRow) {
        		row = new TableRow(this);
        		row.setPadding(4, 4, 4, 4);
        		mSiteListTableView.addView(row);
        		buttonCount = 0;
        	}
        	buttonCount++;
        	row.addView(textBtn);
        }

        
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d(TAG, "mCancelButton clicked");
                Intent i = new Intent(v.getContext(), Kablink.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }

}
