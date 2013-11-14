package org.vamosjuntos.juntos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class TextReportActivity extends Activity {

	String text = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_text);

	}
	public void submitReport(View button) {
		final CheckBox ifAnonymous = (CheckBox)findViewById(R.id.reportIfAnonymous);
		final CheckBox informOthers = (CheckBox)findViewById(R.id.reportIfInformOthers);
		final EditText reportText = (EditText)findViewById(R.id.reportEditText);
		final EditText username = (EditText)findViewById(R.id.reportUserName);
		final Spinner type = (Spinner)findViewById(R.id.reportTypeSpinner);
		Intent intent = getIntent();
		intent.putExtra(ReportActivity.TEXT_CONTENT, reportText.getText().toString());
		intent.putExtra(ReportActivity.IF_ANONYMOUS, ifAnonymous.isChecked());
		intent.putExtra(ReportActivity.IF_INFORM, informOthers.isChecked());
		intent.putExtra(ReportActivity.REPORT_USER_NAME, username.getText().toString());
		intent.putExtra(ReportActivity.REPORT_CATEGORY, type.getSelectedItem().toString());
		TextReportActivity.this.setResult(RESULT_OK, intent);
		finish();
	}
}