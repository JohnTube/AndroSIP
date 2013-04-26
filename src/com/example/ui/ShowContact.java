package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.test_app.R;

public class ShowContact extends SherlockFragmentActivity {
	private String contactName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contactName=getIntent().getStringExtra("NAME");
		setContentView(R.layout.activity_show_contact);
		Button modif = (Button)findViewById(R.id.modif);
		Button supp = (Button)findViewById(R.id.supp);
	modif.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View aView) {
				Intent intent = new Intent(getParent(),ShowContact.class);
				getParent().startActivityForResult(intent, 7);
				
			}	
		}
		);
		supp.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View aView) {
				ContactDB dbHelper = new ContactDB(null);
				dbHelper.removeContactwithName(contactName);
				Intent intent = new Intent(getParent(),ShowContact.class);
				getParent().startActivityForResult(intent, 8);
			
		}});
		// Show the Up button in the action bar.
		//setupActionBar();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 *//*
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}*/
	


}
