package org.vamosjuntos.juntos;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

class AsyncLoadCalendars extends CalendarAsyncTask {
	private String calendarId;// = CalendarExplore.JUNTOS_ACCOUNT;
	public static final String UPDATE_VERSION = "UPDATE_VERSION";

	AsyncLoadCalendars(CalendarExplore calendarSample, String id) {
		super(calendarSample);
		this.calendarId = id;
		Thread.currentThread().setContextClassLoader(activity.getClassLoader());
	}

	@Override
	protected void doInBackground() throws IOException {
		com.google.api.services.calendar.model.Events feed = client.events().list(calendarId)
				.setFields(CalendarInfo.FEED_FIELDS).execute();
		model.reset(feed.getItems());

		/////---------------->
		List<Event> events = feed.getItems();
		ListIterator<Event> iter = events.listIterator();
		while (iter.hasNext()) {
			onNewEventAdded(iter.next());
		}
		/////<----------------
	}

	private void onNewEventAdded(Event e) {
		ContentValues values = new ContentValues();
		values.put(EventContentProvider.KEY_SUMMARY, e.getSummary());
		values.put(EventContentProvider.KEY_LOCATION, e.getDescription());
		values.put(EventContentProvider.KEY_DESCRIPTION, e.getLocation());
		values.put(EventContentProvider.KEY_START_TIME, e.getStart().toString());
		values.put(EventContentProvider.KEY_END_TIME, e.getEnd().toString());
		values.put(EventContentProvider.KEY_STATUS, NotificationService.STATUS_NEW);
		ContentResolver cr = activity.getContentResolver();

		// search the event in the database
		String s = "%" + e.getSummary() + "%";
		Cursor cursor = cr.query(EventContentProvider.CONTENT_URI, null, 
				EventContentProvider.KEY_SUMMARY + " LIKE ?", new String[] {s}, null);
		// if the event does not exist
		if (cursor.moveToFirst() == false) Log.d("CURSOR", "false" );
		if (cursor != null && cursor.moveToFirst() == false) {
			cr.insert(EventContentProvider.CONTENT_URI, values);
			Log.w("LoadCalendar", (String) values.get(EventContentProvider.KEY_STATUS) + 
					" || add: " + e.getSummary());
		} else {
			Log.d("CURSOR", "cursor is null");
		}
		cursor.close();
	}

	static void run(CalendarExplore calendarSample, String calendarId) {
		new AsyncLoadCalendars(calendarSample, calendarId).execute();
	}
}