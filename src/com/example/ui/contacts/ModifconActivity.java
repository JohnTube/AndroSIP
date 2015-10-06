package com.example.ui.contacts;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;
public class ModifconActivity extends SherlockFragmentActivity {
private int ctc_id;
private Uri Uri1;
private Uri Uri2;
private String ImagePath="";
private ContactDB dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifcon);
		final EditText sipmodif = (EditText) findViewById(R.id.sipmodif);
		sipmodif.setTextColor(Color.parseColor("#FFFFFF"));
		final EditText namemodif = (EditText) findViewById(R.id.namemodif);
		namemodif.setTextColor(Color.parseColor("#FFFFFF"));
		final EditText mailmodif = (EditText) findViewById(R.id.mailmodif);
		mailmodif.setTextColor(Color.parseColor("#FFFFFF"));
		Button modifph1 = (Button) findViewById(R.id.modifph1);
		Button modifph2 = (Button) findViewById(R.id.modifph2);
		Button ok = (Button) findViewById(R.id.modifbtn);
		Bundle objetbundle  = this.getIntent().getExtras();
	 if (objetbundle != null && objetbundle.containsKey("id"))
		   {
	        	ctc_id= this.getIntent().getIntExtra("id", 1);
		   }
		 
		modifph1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						 
						startActivityForResult(i,20);
			}
			});
		
		modifph2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				File file = new File(Environment.getExternalStorageDirectory(),  (cal.getTimeInMillis()+".jpg"));
				if(!file.exists()){
				try {
				file.createNewFile();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				}else{
				file.delete();
				try {
				file.createNewFile();
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				}
				Uri2 = Uri.fromFile(file);

				Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri2);

				startActivityForResult(i, 21);
				
			}});
	ok.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dbhelper = new ContactDB(ModifconActivity.this);
			dbhelper.open();
			
			if (namemodif.getText().toString().matches("")) {
				Toast.makeText(ModifconActivity.this, "Nom vide", Toast.LENGTH_SHORT).show();}
			else if (dbhelper.existeName(namemodif.getText().toString())) {
				Toast.makeText(ModifconActivity.this, "Nom déja Existant", Toast.LENGTH_SHORT).show();
			}
			else if (sipmodif.getText().toString().matches("")) {
		Toast.makeText(ModifconActivity.this, "SIP vide", Toast.LENGTH_SHORT).show();}
			else if (!sipmodif.getText().toString().contains("@"))
			{
				Toast.makeText(ModifconActivity.this, "SIP non valide", Toast.LENGTH_SHORT).show();	
			}
		/*	else if (!mailcontact.getText().toString().matches("")){
				if(!checkEmail(mailcontact.getText().toString()))
			{Toast.makeText(AddconActivity.this, "Email non valide", Toast.LENGTH_SHORT).show();}}*/
			else {
		if (ImagePath=="")
		{
		Uri uri = Uri.parse("android.resource://com.example.test_app/"+R.drawable.unknown);
		ImagePath=uri.toString();
		}
			
			Contact c=new Contact(namemodif.getText().toString(),sipmodif.getText().toString(),ImagePath,mailmodif.getText().toString());
			dbhelper.updateContact(ctc_id,c);
			dbhelper.close();
			finish();   
			}}});
	}
		
	
	@Override
    protected void onStart() {
            super.onStart();
            ActionBar aB = getSupportActionBar();
    		aB.setTitle("Modifier contact");
    		aB.setIcon(R.drawable.unknown); 
    aB.setDisplayHomeAsUpEnabled(true);
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode==RESULT_OK)
	{
		if (requestCode==20){
			Uri1 = data.getData();
	      String[] filePathColumn = { MediaStore.Images.Media.DATA };

	      Cursor cursor = getContentResolver().query(Uri1,
	              filePathColumn, null, null, null);
	      cursor.moveToFirst();

	      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	     ImagePath = cursor.getString(columnIndex);
	      cursor.close();
	}
		if (requestCode==21){
		ImagePath= Uri2.toString();
	}
	}
	}
	
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	        "\\@" +
	        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	        "(" +
	        "\\." +
	        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	        ")+"
	    );
	private boolean checkEmail(String email) {
	    return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}	

}
