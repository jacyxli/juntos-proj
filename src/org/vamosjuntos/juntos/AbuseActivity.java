package org.vamosjuntos.juntos;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AbuseActivity extends Activity {

	private static String display;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.abuse);

		TextView tv = (TextView)findViewById(R.id.showAbuseReport);
		Intent intent = getIntent();
		int type = intent.getIntExtra(ReportActivity.NOTIFICATION_TYPE, 1);
		Log.d("TYPE", Integer.toString(type));
		if (type == 0) {
			Log.d("DEBUG", "Display abuse");
			String details, person, category, date, loc;
			String[] contents = intent.getStringArrayExtra(ReportActivity.ABUSE_STRINGS);
			details = contents[0];
			person = contents[1];
			category = contents[2];
			date = contents[4];
			loc = contents[3];
			display = "Date:\t" + date + "\nReported by:\t" + person 
					+ "\nLocation:\t" + loc + "\nCategory:\t" + category + 
					"\nDetails:\n\t" + details;
		} 
		else if (type == 1) {
			String[] contents = intent.getStringArrayExtra(NotificationService.EVENT_STRINGS);
			StringBuilder str = new StringBuilder();
			str.append("The following events are updated from your organizer:\n");
			for (int i=0; i<contents.length; i++) {
				str.append("Event #" + Integer.toString(i) + ": " + contents[i] + "\n");
			}
			str.append("\nBe sure to attend some of them!");
			display= str.toString();
		}
		tv.setText(display);
	}

}