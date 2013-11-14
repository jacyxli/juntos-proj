package org.vamosjuntos.juntos;

import org.vamosjuntos.widget.FormEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class FirstLaunchActivity extends Activity{
	// RequestCode
	private static final int NETWORK_CONNECTION = 0;

	private CheckBox abuseNotification;
	private Spinner eventNotification;
	private Spinner preferredLanguage;
	private EditText firstname, lastname, email, phone;
	private static String _firstname, _lastname, _email, _phone;
	private static boolean abuse_notification = true;
	private static String event_notification; // update interval
	private static String preferred_language;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_launch_userinfo);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		abuseNotification = (CheckBox) findViewById(R.id.launch_checkbox_notification);
		firstname = (EditText) findViewById(R.id.launch_firstname);
		lastname = (EditText) findViewById(R.id.launch_lastname);
		email = (EditText) findViewById(R.id.launch_email);
		phone = (EditText) findViewById(R.id.launch_phone);
		eventNotification = (Spinner) findViewById(R.id.launch_spinner_notification);
		preferredLanguage = (Spinner) findViewById(R.id.launch_spinner_language);
		
		testInput();
		if (!isOnline()) createNetErrorDialog();

		email.setOnEditorActionListener(new EditText.OnEditorActionListener () {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT || 
						actionId == EditorInfo.IME_ACTION_DONE || 
						actionId == EditorInfo.IME_ACTION_GO) {
					String e = prefs.getString(PreferencesActivity.USER_EMAIL, null);
					FormEditText fdt = (FormEditText) findViewById(R.id.launch_email);
					if (fdt.testValidity()) _email = e;
					return true;               
				}
				return false;
			}
		});
		
		phone.setOnEditorActionListener(new EditText.OnEditorActionListener () {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || 
						actionId == EditorInfo.IME_ACTION_NEXT || 
						actionId == EditorInfo.IME_ACTION_GO) {
					String p = prefs.getString(PreferencesActivity.USER_PHONE, null);
					FormEditText fdt = (FormEditText) findViewById(R.id.launch_phone);
					if (fdt.testValidity()) _phone = p;
					return true;               
				}
				return false;
			}
		});

		Button okButton = (Button) findViewById(R.id.launch_okbutton);
		Button cancelButton = (Button) findViewById(R.id.launch_cancelbutton);
		okButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				saveUserInput();
				FirstLaunchActivity.this.setResult(RESULT_OK);
				finish();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				FirstLaunchActivity.this.setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case NETWORK_CONNECTION:
			if (!isOnline()) {
				FirstLaunchActivity.this.setResult(RESULT_CANCELED);
				finish();
			} 
			Button okButton = (Button) findViewById(R.id.launch_okbutton);
			Button cancelButton = (Button) findViewById(R.id.launch_cancelbutton);
			okButton.setOnClickListener(new OnClickListener(){
				public void onClick(View view){
					saveUserInput();
					FirstLaunchActivity.this.setResult(RESULT_OK);
				}
			});
			cancelButton.setOnClickListener(new OnClickListener(){
				public void onClick(View view){
					FirstLaunchActivity.this.setResult(RESULT_CANCELED);
				}
			});
			finish();
			break;
		default:
			break;
		}

	}
	
	@Override
	protected void onResume(){
		super.onResume();
		testInput();
		abuseNotification.setChecked(abuse_notification);
		firstname.setText(_firstname);
		lastname.setText(_lastname);
		phone.setText(_phone);
	}

	private void testInput(){
		FormEditText fdt = (FormEditText) findViewById(R.id.launch_email);
		fdt.testValidity();
		fdt = (FormEditText) findViewById(R.id.launch_phone);
		fdt.testValidity();
	}

	private void saveUserInput() {
		abuse_notification = abuseNotification.isChecked();
		event_notification = eventNotification.getSelectedItem().toString();
		preferred_language = preferredLanguage.getSelectedItem().toString();
		String _firstname = firstname.getText().toString();
		String _lastname = lastname.getText().toString();
		String _email = email.getText().toString();
		String _phone = phone.getText().toString();
		Editor editor = prefs.edit();
		editor.putBoolean(PreferencesActivity.ABUSE_NOTIFICATION, abuse_notification);
		editor.putString(PreferencesActivity.EVENT_NOTIFICATION, event_notification);
		editor.putString(PreferencesActivity.USER_FIRSTNAME, _firstname);
		editor.putString(PreferencesActivity.USER_LASTNAME, _lastname);
		editor.putString(PreferencesActivity.USER_EMAIL, _email);
		editor.putString(PreferencesActivity.USER_PHONE, _phone);
		editor.commit();
	}

	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private void createNetErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You need a network connection to update your user info. Please turn on mobile network or Wi-Fi in Settings.")
		.setTitle("Unable to connect")
		.setCancelable(false)
		.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivityForResult(i, NETWORK_CONNECTION);
			}
		}
				)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SharedPreferences.Editor prefsEditor = prefs.edit();
						prefsEditor.putBoolean(CalendarExplore.FIRST_LAUNCH, false);
						prefsEditor.commit();
						FirstLaunchActivity.this.finish();
					}
				}
						);
		AlertDialog alert = builder.create();
		alert.show();
	}

}