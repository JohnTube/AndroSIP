package com.example.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.test_app.R;


public class CallFrag extends SherlockFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.activity_call_frag, container, false);
		final Button btnDial = (Button)view.findViewById(R.id.btnDial);
		final EditText username = (EditText) view.findViewById(R.id.username);
		final EditText domain = (EditText) view.findViewById(R.id.domain);
		return view;
			
	}
}
	
