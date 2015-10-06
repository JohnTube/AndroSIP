package com.example.ui.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;

public class ShowContact extends SherlockFragmentActivity {
	private int contactid;
	private String contactName;
	private String contactSip;
	private String contactemail;
	private String contactimage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_show_contact);
		Button modif = (Button)findViewById(R.id.modif);
		Button supp = (Button)findViewById(R.id.supp);
		
		final TextView data = (TextView)findViewById(R.id.affichage_contact);
		Bundle objetbundle  = this.getIntent().getExtras();
		ImageView conImage = (ImageView)findViewById(R.id.image_contact);
		
		   if (objetbundle != null && objetbundle.containsKey("NOM") && objetbundle.containsKey("SIP")) 
		   {
			   contactid=this.getIntent().getIntExtra("id",1);
	        	contactName = this.getIntent().getStringExtra("NOM");
	                contactSip = this.getIntent().getStringExtra("SIP");
	                contactemail = this.getIntent().getStringExtra("EMAIL");
	                contactimage = this.getIntent().getStringExtra("IMAGE");

		   }
		   if ((contactemail.matches(""))){data.setText("NAME : "+contactName+"\nSIP :"+contactSip);}
		   
		   else{ data.setText("NAME : "+contactName+"\nSIP :"+contactSip+"\nEmail :"+contactemail);}
	   conImage.setImageURI(Uri.parse(contactimage));
		  
		  // Toast.makeText(ShowContact.this,contactimage, Toast.LENGTH_SHORT).show();
		   modif.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View aView) {
				 Bundle bundle = new Bundle();
				 bundle.putInt("id", contactid);
				Intent intent = new Intent(aView.getContext(),ModifconActivity.class);
				 intent.putExtras(bundle);
				startActivityForResult(intent, 7);
				
			}}
		);
		supp.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View aView) {
				ContactDB dbHelper = new ContactDB(ShowContact.this);
				dbHelper.open();
				dbHelper.removeContactwithName(contactName);
				dbHelper.close();
				finish();
			
		}});
		
	}
	@Override
    protected void onStart() {
            super.onStart();
            ActionBar aB = getSupportActionBar();
    		aB.setTitle("Afficher contact");
    		aB.setIcon(R.drawable.unknown); 
    aB.setDisplayHomeAsUpEnabled(true);
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
