package org.vamosjuntos.juntos;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class Rights extends Activity {
	private static final int SHOW_PREFERENCES = 0;
	public static String RIGHT_TYPE = "right type";
	public final static int IMMIGRANT_RIGHTS = 0;
	public final static int YOUTH_RIGHTS = 1;
	public final static int PARENT_RIGHTS = 2;
	TextView title;
	TextView context;
	int type = -1;

	SpinnerAdapter mSpinnerAdapter;	
	OnNavigationListener mOnNavigationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rights);
		////////////////////////////////////////////////////
		mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
		mOnNavigationListener = new ActionBar.OnNavigationListener() {
			String[] strings = getResources().getStringArray(R.array.action_list);
			String info_unavailable = getString(R.string.info_unavailable);

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				switch(itemPosition){
				case 0:
				case 1:
				case 2:
					title.setText(strings[itemPosition]);
					context.setText(info_unavailable);
					break;
				case 3:
					String uri = getString(R.string.url_community_resource);
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(browserIntent);
				default:
					break;
				}
				return true;
			}
		};

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);

		//////////////////////////////////


		Bundle extra = getIntent().getExtras();
		if(extra!=null){
			type = extra.getInt(Rights.RIGHT_TYPE);
		} else {
			type = IMMIGRANT_RIGHTS;
		}

		title = (TextView) findViewById(R.id.txt_title_rights);
		context = (TextView) findViewById(R.id.txt_context_rights);

		String info_unavailable = getString(R.string.info_unavailable);
		switch(type){
		case Rights.IMMIGRANT_RIGHTS:
			title.setText("Immigrant Rights");
			context.setText(info_unavailable);
			break;
		case Rights.YOUTH_RIGHTS:
			title.setText("Youth Rights");
			context.setText(info_unavailable);
			break;
		case Rights.PARENT_RIGHTS:
			title.setText("Parent Rights");
			context.setText(info_unavailable);
			break;
		}		
	}

	@Override
	protected void onResume() {
		super.onResume();
		RadioButton btn_home= (RadioButton)findViewById(R.id.btn_home_frag);
		btn_home.setChecked(true);
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
					//read from internet when xml file is ready
				}
			});
			t.start();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
