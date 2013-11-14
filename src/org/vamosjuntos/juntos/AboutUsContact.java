package org.vamosjuntos.juntos;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class AboutUsContact  extends Activity {

	ArrayAdapter<Contact> adapter;
	ArrayList<Contact> arr;
	Handler handler = new Handler();
	public static final String TAG = "ABOUT US CONTACT";
	ListView lv;
	Contact c;
	public int numContacts = 0;

	private static final int CONTEXT_EMAIL = 0;
	private static final int SHOW_PREFERENCES = 0;

	public void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_contact);
		
		arr = new ArrayList<Contact>();
		lv = (ListView)findViewById(R.id.contact_list);
		registerForContextMenu(lv);
		onItemSelected();

	}
	
	@Override
	public void onResume() {
		super.onResume();
		RadioButton btn_aboutus = (RadioButton)findViewById(R.id.btn_aboutus_frag);
		btn_aboutus.setChecked(true);
		
		ContactAsyncTask.run(this);

	}

	public void refreshContacts() { 
		adapter = new ArrayAdapter<Contact>(getApplicationContext(), R.layout.contact_list_item , arr){
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				LinearLayout lo;
				if(convertView == null){
					LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					lo = (LinearLayout)inflater.inflate(R.layout.contact_list_item, null);
				} else {
					lo = (LinearLayout)convertView;
				}
				Contact c = getItem(position);
				TextView name = (TextView)lo.findViewById(R.id.list_contact_text1);
				TextView title = (TextView)lo.findViewById(R.id.list_contact_text2);
				TextView email = (TextView)lo.findViewById(R.id.list_contact_text3);
				name.setText(c.getName());
				title.setText(c.getTitle());
				email.setText(c.getEmail());
				return lo;
			}
		};
		lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_us_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_settings){
			Class<? extends Object> c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?    
					PreferencesActivity.class : Preference.class;
			Intent i = new Intent(this, c);

			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		} else if (item.getItemId() == R.id.menu_refresh){
			ContactAsyncTask.run(this);
			return true;
		}else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, CONTEXT_EMAIL, 0, R.string.menu_send_email);
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int contactIdx = (int) info.id;
		if (contactIdx < adapter.getCount()) {
			final Contact contactInfo = adapter.getItem(contactIdx);
			switch(item.getItemId()){
			case CONTEXT_EMAIL:
				sendViaEmail(contactInfo.getEmail());
				break;
			}
		}
		return super.onContextItemSelected(item);
	}


	private void sendViaEmail(String receiver) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ receiver }); 
		startActivity(createEmailOnlyChooserIntent(intent, "Send via email"));
	}

	private Intent createEmailOnlyChooserIntent(Intent source,
			CharSequence chooserTitle) {
		Stack<Intent> intents = new Stack<Intent>();
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				"info@domain.com", null));
		List<ResolveInfo> activities;
		activities = getPackageManager().queryIntentActivities(i, 0);
		for(ResolveInfo ri : activities) {
			Intent target = new Intent(source);
			target.setPackage(ri.activityInfo.packageName);
			intents.add(target);
		}
		if(!intents.isEmpty()) {
			Intent chooserIntent = Intent.createChooser(intents.remove(0), chooserTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					intents.toArray(new Parcelable[intents.size()]));
			return chooserIntent;
		} else {
			return Intent.createChooser(source, chooserTitle);
		}
	}

	void onItemSelected(){
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//registerForContextMenu(view);
				view.showContextMenu();
			}
		});
	}
}