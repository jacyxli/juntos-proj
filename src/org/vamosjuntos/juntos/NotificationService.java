package org.vamosjuntos.juntos;

import java.util.LinkedList;
import java.util.ListIterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {

	private WakeLock mWakeLock;
	private static String summary, location, description, start, end;
	private int numEvents = 0;
	private LinkedList<ContentValues> events;
	public static final String STATUS_NEW = "new";
	public static final String STATUS_OLD = "old";

	public static final String EVENT_STRINGS = "EVENT_STRINGS";
	public static final int EVENT_NOTIFICATION_ID = 2;
	private static final int MAX_DISPLAY = 6;
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// initialize
	private void handleIntent(Intent intent) {
		events = new LinkedList<ContentValues>();
		// obtain the wake lock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
		mWakeLock.acquire();
		// check the global background data setting
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (!cm.getBackgroundDataSetting()) {
			stopSelf();
			return;
		}
		Log.d("Notification", "Notification Service in Use");
		// open a new thread to do the work
		new PollTask().execute();
	}

	// do the actual work, in a separate thread
	private class PollTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(EventContentProvider.CONTENT_URI, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				int statusIdx, summaryIdx, descriptionIdx, locationIdx, startIdx, endIdx;
				int rowIdIdx;
				while(cursor.moveToNext()) {
					// for each event
					statusIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_STATUS);
					String status = cursor.getString(statusIdx);
					if (status.equals(STATUS_NEW)) {
						summaryIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_SUMMARY);
						summary = cursor.getString(summaryIdx);
						descriptionIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_DESCRIPTION);
						description = cursor.getString(descriptionIdx);
						locationIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_LOCATION);
						location = cursor.getString(locationIdx);
						startIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_START_TIME);
						start = cursor.getString(startIdx);
						endIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_END_TIME);
						end = cursor.getString(endIdx);
						// get row id
						rowIdIdx = cursor.getColumnIndexOrThrow(EventContentProvider.KEY_ID);
						String rowId = cursor.getString(rowIdIdx);
						ContentValues values = new ContentValues();
						values.put(EventContentProvider.KEY_SUMMARY, summary);
						values.put(EventContentProvider.KEY_DESCRIPTION, description);
						values.put(EventContentProvider.KEY_LOCATION, location);
						values.put(EventContentProvider.KEY_START_TIME, start);
						values.put(EventContentProvider.KEY_END_TIME, end);
						values.put(EventContentProvider.KEY_STATUS, STATUS_OLD);
						cr.update(EventContentProvider.CONTENT_URI, values, 
								EventContentProvider.KEY_ID + "=" + rowId, null);
						events.add(values);
						numEvents++;
					}

				}
			}
			cursor.close();
			return null;
		}

		// Notifying actions
		@Override
		protected void onPostExecute(Void result) {
			if (numEvents > 0) broadcastEvent();
			stopSelf();
		}
	}

	// support on 2.0-
	@Override
	public void onStart(Intent intent, int startId) {
		handleIntent(intent);
	}

	// support on 2.0+ (API level 5+)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleIntent(intent);
		return START_NOT_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
	}


	public void broadcastEvent() {
		String[] newEvents = new String[numEvents];
		
		ListIterator<ContentValues> iter = events.listIterator();
		int i = 0;
		while(iter.hasNext()) {
			ContentValues values = iter.next();
			String t = (String) values.get(EventContentProvider.KEY_SUMMARY);
			newEvents[i] = t;
			i++;
			Log.d("Notification", "broadcasting new event: " + t);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);  

		Intent ni = new Intent(this, AbuseActivity.class);
		ni.putExtra(ReportActivity.NOTIFICATION_TYPE, 1);
		ni.putExtra(EVENT_STRINGS, newEvents);
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, ni, 0);

		StringBuilder str = new StringBuilder();
		
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("new events updated:");
		for (int j=0; j<numEvents; j++) {
			inboxStyle.addLine("  " + newEvents[j]);
			str.append("  " + newEvents[j] + "\n");
			if (j > MAX_DISPLAY) {
				inboxStyle.addLine("  ...");
				inboxStyle.addLine("  ... Click to view more.");
				break;
			}
		}
		
		builder.setAutoCancel(true)
		.setSmallIcon(R.drawable.ic_action_news)
		.setContentIntent(launchIntent)
		.setContentTitle(Integer.toString(numEvents) + " new events from Juntos")
		.setContentText(str.toString());
		
		builder.setStyle(inboxStyle);
		
		long[] vibrate = new long[] {1000,1000,1000,1000};
		builder.setVibrate(vibrate);
		Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		builder.setSound(ringURI);

		Notification n = builder.getNotification();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(EVENT_NOTIFICATION_ID, n);
	}

}
