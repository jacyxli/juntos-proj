package org.vamosjuntos.juntos;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ButtonWidget extends AppWidgetProvider{

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		
		Intent calIntent = new Intent(context, CalendarExplore.class);
		Intent instanceIntent = new Intent(context, TextReportActivity.class);
		Intent cameraIntent = new Intent(context, ReportActivity.class);

		PendingIntent pendingIntent_cal = PendingIntent.getActivity(context, 0, calIntent, 0);
		PendingIntent pendingIntent_instance = PendingIntent.getActivity(context, 0, instanceIntent, 0);
		PendingIntent pendingIntent_camera = PendingIntent.getActivity(context, 0, cameraIntent, 0);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.button_widget);
		views.setOnClickPendingIntent(R.id.btn_to_events, pendingIntent_cal);
		views.setOnClickPendingIntent(R.id.btn_to_report_instance, pendingIntent_instance);
		views.setOnClickPendingIntent(R.id.btn_to_report_camera, pendingIntent_camera);

		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
}