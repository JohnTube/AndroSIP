package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;

public class ShowContact extends SherlockFragmentActivity {
	private String contactName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_show_contact);
		Button modif = (Button)findViewById(R.id.modif);
		Button supp = (Button)findViewById(R.id.supp);
		contactName=getIntent().getStringExtra("NOM");
		//Log.d("ch3ar",contactName);
	modif.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View aView) {
				
				Intent intent = new Intent(aView.getContext(),ModifconActivity.class);
				startActivityForResult(intent, 7);
				
			}	
		}
		);
		supp.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View aView) {
				ContactDB dbHelper = new ContactDB(ShowContact.this);
				dbHelper.open();
				dbHelper.removeContactwithName(contactName);
				dbHelper.close();
				Intent intent = new Intent(aView.getContext(),MainActivity.class);
				startActivityForResult(intent, 8);
			
		}});
		
	}
	@Override
    protected void onStart() {
            super.onStart();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
             finish();
             return true;
        }

    return super.onOptionsItemSelected(item);
    }

	


}
