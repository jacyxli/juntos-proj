/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vamosjuntos.juntos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author jacyli@google.com (Your Name Here)
 *
 */
public class DisplayEvent extends Activity {

	TextView event_title;
	TextView event_location;
	TextView event_description;

	String title  = "";
	String description = "";
	String location = "";
	long start;
	long end;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_page);

		event_title = (TextView) findViewById(R.id.event_title);
		event_location = (TextView) findViewById(R.id.event_location);
		event_description = (TextView) findViewById(R.id.event_description);

		Date startTime = null;
		Date endTime = null;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("title");
			description = extras.getString("description");
			location = extras.getString("location");
			start = extras.getLong("start");
			end = extras.getLong("end");
			startTime = new Date(start);
			endTime = new Date(end);
		}
		StringBuffer info = new StringBuffer();
		info.append("Description:\t"+description + "\n");
		info.append("Time:\t"+(startTime==null?"TBA":startTime) +" ~ "+(endTime==null?"TBA":endTime)+"\n");
		event_title.setText(title);
		event_location.setText(location);
		event_description.setText(info);
		
		////////////////
		Linkify.addLinks(event_description, Linkify.ALL);
		String url = " http://maps.google.co.in/maps?q=905"+location;
        Pattern pattern = Pattern.compile(url);
        Linkify.addLinks(event_location, pattern, "http://");
	}
}

