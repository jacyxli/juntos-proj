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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsLike extends Activity {

	private static final int SHOW_PREFERENCES = 1;
	private static final String TAG = "ABOUT US-LIKE US";


	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_like);

	}

	@Override
	public void onResume() {
		super.onResume();
		RadioButton btn_aboutus = (RadioButton)findViewById(R.id.btn_aboutus_frag);
		btn_aboutus.setChecked(true);
	}
	public void onFacebookClick(View v){
		String uri = getString(R.string.url_facebook);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
	public void onTwitterClick(View v){
		String uri = getString(R.string.url_twitter);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
	public void onGplusClick(View v){
		String uri = getString(R.string.url_gplus);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
	public void onYoutubeClick(View v){
		String uri = getString(R.string.url_youtube);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
	public void onFlickrClick(View v){
		String uri = getString(R.string.url_flickr);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
	public void onRssClick(View v){
		String uri = getString(R.string.url_rss);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(browserIntent);
	}
}
