package org.vamosjuntos.juntos;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsHistory extends Activity {
	private static final int SHOW_PREFERENCES = 0;
	private static String TAG = "ABOUT US-HISTORY";
	private String history_txt = "";
	private String history_title = "";
	private Handler handler = new Handler();
	TextView historyTextTitle;
	TextView historyTextContext;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);

		historyTextTitle = (TextView) findViewById(R.id.txt_title);
		historyTextContext = (TextView) findViewById(R.id.txt_context);
		historyTextContext.setMovementMethod(new ScrollingMovementMethod());

		Thread t = new Thread(new Runnable() {
			public void run() {
				refreshHistoryInfo(); 
			}
		});
		t.start();	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		RadioButton btn_aboutus = (RadioButton)findViewById(R.id.btn_aboutus_frag);
		btn_aboutus.setChecked(true);
	}

	public void refreshHistoryInfo() { 
		try {
			String about_us_xml = getString(R.string.xml_about_us);
			URL url = new URL(about_us_xml);

			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// Parse the about us mission feed.
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				// Get a list of each missions entry.
				NodeList nl = docEle.getElementsByTagName("History");
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0 ; i < nl.getLength(); i++) {
						Element mission_node = (Element)nl.item(i);
						Element title = (Element)mission_node.getElementsByTagName("title").item(0);
						Element text = (Element)mission_node.getElementsByTagName("text").item(0);
						history_txt = text.getFirstChild().getNodeValue();
						history_title = title.getFirstChild().getNodeValue();
					}

					handler.post(new Runnable(){
						@Override
						public void run(){
							historyTextTitle.setText(history_title);
							historyTextContext.setText(history_txt);
						}
					});
				}
			}
		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException", e);
		} catch (IOException e) {
			Log.d(TAG, "IOException", e);
			handler.post(new Runnable(){
				@Override
				public void run(){
					String txt = getResources().getString(R.string.msg_no_internet);
					Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG ).show();
				}
			});
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "Parser Configuration Exception", e);
		} catch (SAXException e) {
			Log.d(TAG, "SAX Exception", e);
		} finally {
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_us_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_settings) {
			Class<? extends Object> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?    
					PreferencesActivity.class : Preference.class;
			Intent i = new Intent(this, c);
			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		} else if (itemId == R.id.menu_refresh) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					refreshHistoryInfo(); 
				}
			});
			t.start();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

}
