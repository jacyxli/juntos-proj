package org.vamosjuntos.juntos;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class ButtonbarFragment extends Fragment {
	Button explore;
	Button home;
	Button about_us;
	Context c;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.button_bar_fragment, container,
				false);
		c = view.getContext();

		explore = (Button) view.findViewById(R.id.btn_explore_frag);
		home = (Button) view.findViewById(R.id.btn_home_frag);
		about_us = (Button) view.findViewById(R.id.btn_aboutus_frag);

		explore.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				explore.setSelected(true);
				home.setSelected(false);
				about_us.setSelected(false);

				Intent intent = new Intent(v.getContext(), CalendarExplore.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		home.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				explore.setSelected(false);
				home.setSelected(true);
				about_us.setSelected(false);

				Intent intent = new Intent(v.getContext(), Home.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		about_us.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				explore.setSelected(false);
				home.setSelected(false);
				about_us.setSelected(true);

				Intent intent = new Intent(v.getContext(), AboutUsTab.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});



		return view;

	}
}
