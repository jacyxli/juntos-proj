package org.vamosjuntos.juntos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DocumentAbuse extends Activity {
	
	public static final int REPORT_CAMERA = 0;
	public static final int REPORT_TEXT = 1;
	public static final int REPORT_AUDIO = 2;
	public static final String REPORT_TYPE = "REPORT_TYPE";
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		
		Button btn_report_instance = (Button)findViewById(R.id.btn_report_instance);
		Button btn_report_camera = (Button)findViewById(R.id.btn_report_camera);
		Button btn_report_audio = (Button)findViewById(R.id.btn_report_audio);

		btn_report_instance.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DocumentAbuse.this, ReportActivity.class);
				intent.putExtra(REPORT_TYPE, REPORT_TEXT);
				startActivity(intent);
			}
		});
		
		btn_report_camera.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DocumentAbuse.this, ReportActivity.class);
				intent.putExtra(REPORT_TYPE, REPORT_CAMERA);
				startActivity(intent);
			}
		});

		
		btn_report_audio.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DocumentAbuse.this, ReportActivity.class);
				intent.putExtra(REPORT_TYPE, REPORT_AUDIO);
				startActivity(intent);
			}
		});
	}
}