package com.example.ui;
import android.content.Intent;
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
		btnDial.setOnClickListener(new View.OnClickListener() {
			@Override
	        public void onClick(View aView)
	        {
	               Intent intent = new Intent(getActivity(),SearchActivity.class);
	               getActivity().startActivityForResult(intent, 5);
	        }});
		final EditText username = (EditText) view.findViewById(R.id.username);
		final EditText domain = (EditText) view.findViewById(R.id.domain);
		return view;
			
	}
}
	
