package org.vamosjuntos.juntos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	
	public static final String USER_FIRSTNAME = "USER_FIRSTNAME";
	public static final String USER_LASTNAME = "USER_LASTNAME";
	public static final String USER_EMAIL = "USER_EMAIL";
	public static final String USER_PHONE = "USER_PHONE";
	public static final String ABUSE_NOTIFICATION = "ABUSE_NOTIFICATION";
	public static final String EVENT_NOTIFICATION = "EVENT_NOTIFICATION";
	public static final String FEEDBACK = "FEEDBACK";
	
	private static final String TAG = "Preferences";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_menu, menu);	
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		
		if(item.getItemId()==R.id.menu_save) {
			updateFromPreferences();
			finish();
		} else if(item.getItemId()==R.id.menu_cancel) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateFromPreferences() {
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String lastname = prefs.getString(PreferencesActivity.USER_LASTNAME, " ");
		String firstname = prefs.getString(PreferencesActivity.USER_FIRSTNAME, " ");
		String email = prefs.getString(PreferencesActivity.USER_EMAIL, " ");
		String phone = prefs.getString(PreferencesActivity.USER_PHONE, " ");
		String feedback = prefs.getString(PreferencesActivity.FEEDBACK, null);
		
		Log.d("TAG", lastname + ", " + firstname + " " + email + " " + phone);
		
		Intent intent = getIntent();
		boolean first_launch = intent.getBooleanExtra(CalendarExplore.FIRST_LAUNCH, false);
		MySpreadsheetIntegration spreadsheet = new MySpreadsheetIntegration(firstname, lastname, email, phone);
		if (first_launch == true) {
			// add a new row
			spreadsheet.addRow();
			Toast.makeText(getApplicationContext(), "User information saved.", Toast.LENGTH_SHORT).show();
		} else {
			// update an existing row
			spreadsheet.updateRow();
			Toast.makeText(getApplicationContext(), "User information updated.", Toast.LENGTH_SHORT).show();
		}

		PreferencesActivity.this.setResult(RESULT_OK, intent);
	}
	
	private boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
