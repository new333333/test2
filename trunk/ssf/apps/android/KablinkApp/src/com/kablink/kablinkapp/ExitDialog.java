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

public class ExitDialog extends Activity {
    private static String TAG = "ExitDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exit_dialog);
        
        final Intent intent = getIntent();
        final String action = intent.getAction();
        
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.exitAppWarning))
               .setCancelable(false)
               .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Kablink.mLastUrlViewed = null;
                	   Intent intent = new Intent(getBaseContext(), Kablink.class);
					   intent.setAction(Intent.ACTION_VIEW);
					   intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					   startActivity(intent);
                   }
               })
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Intent intent = new Intent(getBaseContext(), Kablink.class);
					   intent.setAction(Intent.ACTION_SHUTDOWN);
					   intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					   startActivity(intent);
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
