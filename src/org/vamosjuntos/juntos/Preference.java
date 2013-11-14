package org.vamosjuntos.juntos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Preference extends PreferenceActivity {

	private static final String TAG = "Preferences";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == R.id.menu_save){
				updateFromPreferences();
				finish();
		} else if(item.getItemId() == R.id.menu_cancel){
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
		
		// update google spreadsheet 
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
		
		// send feedback
		if (feedback != null) {
			
		}

		Preference.this.setResult(RESULT_OK, intent);
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
