package org.vamosjuntos.juntos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class Home extends Activity {

	final Context context = this;
	public static final int SHOW_PREFERENCES = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		Button btn_rights = (Button)findViewById(R.id.btn_rights);
		Button btn_calendar = (Button)findViewById(R.id.btn_calendar);
		Button btn_report = (Button)findViewById(R.id.btn_report);
		Button btn_donate = (Button)findViewById(R.id.btn_donate);
		
		btn_rights.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launching know my rights screen
				Intent intent = new Intent(Home.this, Rights.class);
				intent.putExtra(Rights.RIGHT_TYPE, Rights.IMMIGRANT_RIGHTS);
				startActivity(intent);
			}
		} );

		btn_calendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launching calendar screen
				Intent intent = new Intent(Home.this, MyCalendar.class);
				startActivity(intent);
			}
		} );

		btn_report.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// launching report screen
				Intent intent = new Intent(Home.this, DocumentAbuse.class);
				startActivity(intent);
			}
		} );

		btn_donate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set title
				alertDialogBuilder.setTitle("Directing to Donate page");

				// set dialog message
				alertDialogBuilder
				.setMessage("Are you sure you want to be directed to the donate page?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						// launching donate screen
						String uri = getString(R.string.url_donate);
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
						startActivity(browserIntent);
					}
				})
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		int itemId = item.getItemId();
		if (itemId == (R.id.menu_settings)) {
			Class<? extends Object> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?    
					PreferencesActivity.class : Preference.class;
			Intent i = new Intent(this, c);
			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		}
		return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		RadioButton btn_aboutus = (RadioButton)findViewById(R.id.btn_home_frag);
		btn_aboutus.setChecked(true);
		Log.d("HOME", "onresume");
	}

}
