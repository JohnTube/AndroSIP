package com.example.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.test_app.R;



public class SettingsFrag extends SherlockFragment {
	private Button accBtn;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.activity_settings_frag, container, false);
		accBtn = (Button)view.findViewById(R.id.account_settings_btn);
		accBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),SettingsActivity.class);
	               getActivity().startActivity(intent);
			}});
		return view;
	
	}
	
	

}
