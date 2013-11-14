package org.vamosjuntos.juntos;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class AboutUsTab extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_tab);
 
        TabHost tabHost = getTabHost();
 
        TabSpec missionSpec = tabHost.newTabSpec("Mission");
        missionSpec.setIndicator("Mission", getResources().getDrawable(R.drawable.mission_tab));
        Intent missionIntent = new Intent(this, AboutUsMission.class);
        missionSpec.setContent(missionIntent);
 
        TabSpec historySpec = tabHost.newTabSpec("History");
        historySpec.setIndicator("History", getResources().getDrawable(R.drawable.mission_tab));
        Intent historyIntent = new Intent(this, AboutUsHistory.class);
        historySpec.setContent(historyIntent);
 
        TabSpec contactSpec = tabHost.newTabSpec("Contact");
        contactSpec.setIndicator("Contact", getResources().getDrawable(R.drawable.mission_tab));
        Intent contactIntent = new Intent(this, AboutUsContact.class);
        contactSpec.setContent(contactIntent);
        
        TabSpec likeUsSpec = tabHost.newTabSpec("Like Us");
        likeUsSpec.setIndicator("Like Us", getResources().getDrawable(R.drawable.mission_tab));
        Intent likeUsIntent = new Intent(this, AboutUsLike.class);
        likeUsSpec.setContent(likeUsIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(missionSpec); // Adding mission tab
        tabHost.addTab(historySpec); // Adding history tab
        tabHost.addTab(contactSpec); // Adding contacts tab
        tabHost.addTab(likeUsSpec); // Adding like us tab
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.about_us_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}