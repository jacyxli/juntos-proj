package org.vamosjuntos.juntos;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

public class MyCalendar extends Activity {
	CalendarView cv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		cv = (CalendarView)findViewById(R.id.calendarView1);
		onDateSelected();
	}
	void onDateSelected(){
		cv.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				Intent i = new Intent(getApplicationContext(), CalendarExplore.class);
				month = month + 1;
				String calInfo = year+"-"+month+"-"+dayOfMonth;
				i.putExtra("DATELIMIT", calInfo);
				i.putExtra("TYPE", "LOCAL");
				startActivity(i);
			}
		});	
	}
}
