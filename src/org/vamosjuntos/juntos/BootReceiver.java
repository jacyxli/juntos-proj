package org.vamosjuntos.juntos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		String interval = prefs.getString(PreferencesActivity.EVENT_NOTIFICATION, "Every Minute");
		long mm = 5*1000; // 5 seconds
		if (interval.equals("Never")) mm = 0;
		else if (interval.equals("Weekly")) mm = 7*24*60*60*1000;
		else if (interval.equals("Daily")) mm = AlarmManager.INTERVAL_DAY;
		else if (interval.equals("Hourly")) mm = 60*60*1000;
		else if (interval.equals("Every Minute")) mm = 60*1000; 

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, NotificationService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		am.cancel(pi);

		if (mm > 0) {
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + mm, mm, pi);
			
			Toast.makeText(context, "sending repeating alert", Toast.LENGTH_LONG).show();
		}
	}


}