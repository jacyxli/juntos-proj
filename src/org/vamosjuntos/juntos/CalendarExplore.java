package org.vamosjuntos.juntos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

public final class CalendarExplore extends Activity {

	private static final Level LOGGING_LEVEL = Level.OFF;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	static final String TAG = "CalendarSampleActivity";
	private static final int CONTEXT_ADD_TO_LOCAL = 0;
	private static final int CONTEXT_SHARE = 1;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	static final int REQUEST_AUTHORIZATION = 1;
	static final int REQUEST_ACCOUNT_PICKER = 2;
	private final static int ADD_OR_EDIT_CALENDAR_REQUEST = 3;
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = new GsonFactory();
	public GoogleAccountCredential credential;
	public CalendarModel model = new CalendarModel();
	public ArrayAdapter<CalendarInfo> adapter;
	public com.google.api.services.calendar.Calendar client;
	int numAsyncTasks;
	private ListView listView;
	private SimpleDateFormat sdf = new SimpleDateFormat();
	public static final String JUNTOS_ACCOUNT = "vamosjuntosphilly@gmail.com";

	private static final int USER_PREFERENCES = 4;
	private static final int FIRST_LAUNCH_USERINFO = 3;
	public static final String FIRST_LAUNCH = "FIRST_LAUNCH";

	private SharedPreferences prefs;
	MySpreadsheetIntegration spreadsheet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		setContentView(R.layout.fexplore);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean first_launch = prefs.getBoolean(FIRST_LAUNCH, true);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		if (first_launch == true) {
			/**
			Intent firstLaunchIntent = new Intent(CalendarExplore.this, FirstLaunchActivity.class);
			startActivityForResult(firstLaunchIntent, FIRST_LAUNCH_USERINFO);
			first_launch = false;
			**/
			Class<? extends Object> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?    
					PreferencesActivity.class : Preference.class;
			Intent i = new Intent(this, c);
			i.putExtra(FIRST_LAUNCH, first_launch);
			startActivityForResult(i, Home.SHOW_PREFERENCES);
			first_launch = false;
			
		}
		prefsEditor.putBoolean(FIRST_LAUNCH, first_launch);
		prefsEditor.commit();

		listView = (ListView) findViewById(R.id.calendar_list);
		registerForContextMenu(listView);
		credential = GoogleAccountCredential.usingOAuth2(this, CalendarScopes.CALENDAR);
		client = new com.google.api.services.calendar.Calendar.Builder(
				transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
				.build();

		onItemSelected();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		RadioButton btn_explore = (RadioButton)findViewById(R.id.btn_explore_frag);
		btn_explore.setChecked(true);
		
		if (checkGooglePlayServicesAvailable()) {
			Log.d("GOOGLESERVICE", "check from onResume");
			haveGooglePlayServices();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case REQUEST_GOOGLE_PLAY_SERVICES:
			if (resultCode == Activity.RESULT_OK) {
				Log.d("GOOGLESERVICE", "check from REQUEST_GOOGLE_PLAY_SERVICES");
				haveGooglePlayServices();
			} else {
				checkGooglePlayServicesAvailable();
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				AsyncLoadCalendars.run(this,JUNTOS_ACCOUNT);
			} else {
				Log.d("REQUEST_AUTHORIZATION", "HERE");
				chooseAccount();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					Log.d("REQUEST_ACCOUNT_PICKER", "IN");
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, accountName);
					editor.commit();
				}
				Log.d("REQUEST_ACCOUNT_PICKER", "NOT NULL");
			}
			Log.d("REQUEST_ACCOUNT_PICKER", "OUT");
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.calendar_menu, menu);


		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_settings) {
			Class<? extends Object> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?    
					PreferencesActivity.class : Preference.class;
			Intent i = new Intent(this, c);
			startActivityForResult(i, Home.SHOW_PREFERENCES);
			return true;
		} else if (itemId == R.id.menu_refresh) {
			AsyncLoadCalendars.run(this,JUNTOS_ACCOUNT);
			// sending repeating alarms
			prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String interval = prefs.getString(PreferencesActivity.EVENT_NOTIFICATION, "Every Minute");
			long mm = 60*1000; // 5 seconds
			if (interval.equals("Never")) mm = 0;
			else if (interval.equals("Weekly")) mm = 7*24*60*60*1000;
			else if (interval.equals("Daily")) mm = AlarmManager.INTERVAL_DAY;
			else if (interval.equals("Hourly")) mm = 60*60*1000;
			else if (interval.equals("Every Minute")) mm = 60*1000; // 5 seconds
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(this, NotificationService.class);
			PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
			am.cancel(pi);
			if (mm > 0) {
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + mm, mm, pi);
				//Toast.makeText(this, "sending repeating alarm...", Toast.LENGTH_LONG).show();
			}
		} else if (itemId == R.id.menu_accounts) {
			chooseAccount();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXT_ADD_TO_LOCAL, 0, R.string.menu_add_to_local);
		menu.add(0, CONTEXT_SHARE, 0, R.string.menu_share);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int calendarIndex = (int) info.id;
		if (calendarIndex < adapter.getCount()) {
			final CalendarInfo calendarInfo = adapter.getItem(calendarIndex);
			switch(item.getItemId()){
			case CONTEXT_ADD_TO_LOCAL:
				addEventToLocal(calendarInfo);
				break;
			case CONTEXT_SHARE:
				sendViaEmail(calendarInfo);
				break;
			}
		}
		return super.onContextItemSelected(item);
	}

	void refreshView() {
		adapter = new ArrayAdapter<CalendarInfo>(
				this, R.layout.list_item, model.toSortedArray()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				LinearLayout lo;
				if(convertView == null){
					LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					lo = (LinearLayout)inflater.inflate(R.layout.list_item, null);
				} else {
					lo = (LinearLayout)convertView;
				}
				CalendarInfo calendarInfo = getItem(position);
				TextView title = (TextView)lo.findViewById(R.id.list_event_text1);
				TextView des = (TextView)lo.findViewById(R.id.list_event_text2);
				title.setText(calendarInfo.title);
				Date start, end;
				String text2 = "";

				if(calendarInfo.startTime.getDateTime()!=null && calendarInfo.endTime.getDateTime()!=null){
					start = new Date(calendarInfo.startTime.getDateTime().getValue());
					end = new Date(calendarInfo.endTime.getDateTime().getValue());
					text2 = start + " ~ " + end + "\n";
					//text2 = calendarInfo.startTime.toString() + " ~ " + calendarInfo.endTime.toString() + "\n";	
				} else {
					start = new Date(calendarInfo.startTime.getDate().getValue());
					end = new Date(calendarInfo.endTime.getDate().getValue());
					text2 = start + " ~ " + end + "\n";
				}

				text2 = (text2 == "" ? "Time: TBA\n" : "Time:\t" + text2) + (calendarInfo.location == null ? "" : "Location:\t"+calendarInfo.location);
				des.setText(text2);
				return lo;
			}
		};
		listView.setAdapter(adapter);
	}



	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	private void addEventToLocal(CalendarInfo calendarInfo){
		Event event = new Event();
		event.setSummary(calendarInfo.title);
		event.setLocation(calendarInfo.location);
		event.setStart(calendarInfo.startTime);
		event.setEnd(calendarInfo.endTime);
		new AsyncInsertCalendar(this, event, credential.getSelectedAccountName()).execute();
	}

	// send email
	private void sendViaEmail(CalendarInfo calendarInfo) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_EMAIL, "");
		intent.putExtra(Intent.EXTRA_SUBJECT, "JUNTOS EVENT!!!"); 
		StringBuffer text = new StringBuffer();
		text.append("Come and join me for this exciting event!\n\n");
		text.append(calendarInfo.title + "\n\n");
		text.append("Description:\t"+calendarInfo.description + "\n");
		text.append("Location:\t"+(calendarInfo.location==null ? "TBA":calendarInfo.location) +"\n");
		text.append("Time:\t"+(calendarInfo.startTime==null?"TBA":calendarInfo.startTime) +" ~ "+(calendarInfo.endTime==null?"TBA":calendarInfo.startTime)+"\n");
		intent.putExtra(Intent.EXTRA_TEXT, text.toString());
		startActivity(createEmailOnlyChooserIntent(intent, "Send via email"));
	}

	private Intent createEmailOnlyChooserIntent(Intent source,
			CharSequence chooserTitle) {
		Stack<Intent> intents = new Stack<Intent>();
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				"info@domain.com", null));
		List<ResolveInfo> activities;
		activities = getPackageManager().queryIntentActivities(i, 0);
		for(ResolveInfo ri : activities) {
			Intent target = new Intent(source);
			target.setPackage(ri.activityInfo.packageName);
			intents.add(target);
		}
		if(!intents.isEmpty()) {
			Intent chooserIntent = Intent.createChooser(intents.remove(0), chooserTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					intents.toArray(new Parcelable[intents.size()]));
			return chooserIntent;
		} else {
			return Intent.createChooser(source, chooserTitle);
		}
	}

	/** Listener for when a event is selected **/
	private void onItemSelected() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), DisplayEvent.class);
				CalendarInfo calendarInfo = adapter.getItem(position);
				intent.putExtra("title", calendarInfo.title);
				intent.putExtra("description", calendarInfo.description);
				intent.putExtra("location", calendarInfo.location);
				if(calendarInfo.startTime.getDateTime()!=null && calendarInfo.endTime.getDateTime() != null){
					intent.putExtra("start", calendarInfo.startTime.getDateTime().getValue());
					intent.putExtra("end", calendarInfo.endTime.getDateTime().getValue());
				} else {
					intent.putExtra("start", calendarInfo.startTime.getDate().getValue());
					intent.putExtra("end", calendarInfo.endTime.getDate().getValue());		
				}
				startActivity(intent);
			}
		});
	}

	/***** GOOGLE SERVICE *****/
	void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, CalendarExplore.this, REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}

	/** Check that Google Play services APK is installed and up to date. */
	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	private void haveGooglePlayServices() {
		if (credential.getSelectedAccountName() == null) {
			Log.d("REQUEST_GOOGLEPLAY", "HERE");
		
			SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
			String account = settings.getString(PREF_ACCOUNT_NAME,PREF_ACCOUNT_NAME );
			if(account.equals(PREF_ACCOUNT_NAME)){
				chooseAccount();
			} else {
				credential.setSelectedAccountName(account);
				this.onResume();
			}
		} else {
			// load calendars
			AsyncLoadCalendars.run(this,JUNTOS_ACCOUNT);
		}
	}
}