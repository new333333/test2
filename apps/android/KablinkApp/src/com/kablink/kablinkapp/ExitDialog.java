package com.kablink.kablinkapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ExitDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exit_dialog);
        
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.exitAppWarning))
               .setCancelable(false)
               .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
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
