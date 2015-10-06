package com.example.ui;
import org.zoolu.sip.address.NameAddress;

import android.content.Intent;
import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.test_app.Call;
import com.example.test_app.InCall;
import com.example.test_app.R;
import com.example.test_app.Receiver;


public class CallFrag extends SherlockFragment {
private String user;
private String domain;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.activity_call_frag, container, false);
		final EditText username = (EditText) view.findViewById(R.id.callee);
		final Button btnDial = (Button)view.findViewById(R.id.btnDial);
		
		btnDial.setOnClickListener(new View.OnClickListener() {
			@Override
	        public void onClick(View aView)
	        {	   Log.d("CallFrag","callee="+username.getEditableText().toString());
	        
	         if (username.getEditableText().toString()==null  || username.getEditableText().toString()=="" || 
	        		username.getEditableText().toString()==" ")
	        	 Toast.makeText(getActivity(), "Adresse SIP vide", Toast.LENGTH_SHORT).show();
	        	 else if (!username.getEditableText().toString().contains("@"))
        			Toast.makeText(getActivity(), "Adresse SIP invalide", Toast.LENGTH_SHORT).show();
	        	
	        	
	        else {

        		Receiver.sipCore.call(new NameAddress(username.getEditableText().toString()));
        	Intent intent=new Intent(getActivity(),InCall.class);	
		intent.putExtra("CALLEE", username.getEditableText().toString());
	startActivity(intent);
	        }	
	        }});
		
		
		
	
		return view;
			
	}
}
	
