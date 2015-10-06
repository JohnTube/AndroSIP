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
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;


public class AddconActivity extends SherlockFragmentActivity {
private Uri imageUri1;
private Uri imageUri2;
private String selectedImagePath="";
private static final int CAMERA_REQUEST = 1888; 
private static final int BROWSE_REQUEST = 1991; 
private ContactDB dbHelper;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_addcon);
final EditText sipcontact = (EditText) findViewById(R.id.sipcontact);
sipcontact.setTextColor(Color.parseColor("#FFFFFF"));
final EditText namecontact = (EditText) findViewById(R.id.namecontact);
namecontact.setTextColor(Color.parseColor("#FFFFFF"));
final EditText mailcontact = (EditText) findViewById(R.id.mailcontact);
mailcontact.setTextColor(Color.parseColor("#FFFFFF"));
Button addcon = (Button) findViewById(R.id.add);
Button imagebrowse = (Button) findViewById(R.id.imagebrowse);
Button imagecapture = (Button) findViewById(R.id.imagecapture);


imagebrowse.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View v) {
	Intent i = new Intent(
			Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			 
			startActivityForResult(i,BROWSE_REQUEST);
}
});

imagecapture.setOnClickListener(new View.OnClickListener() {

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
imageUri2 = Uri.fromFile(file);

Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);

startActivityForResult(i, CAMERA_REQUEST);		
}
});

addcon.setOnClickListener(new View.OnClickListener()

{
public void onClick(View aView)
{
	dbHelper = new ContactDB(AddconActivity.this);
	dbHelper.open();
	if (namecontact.getText().toString().matches("")) {
		Toast.makeText(AddconActivity.this, "Nom vide", Toast.LENGTH_SHORT).show();}
	else if (dbHelper.existeName(namecontact.getText().toString())) {
		Toast.makeText(AddconActivity.this, "Nom déja Existant", Toast.LENGTH_SHORT).show();
	}
	else if (sipcontact.getText().toString().matches("")) {
Toast.makeText(AddconActivity.this, "SIP vide", Toast.LENGTH_SHORT).show();}
	else if (!sipcontact.getText().toString().contains("@"))
	{
		Toast.makeText(AddconActivity.this, "SIP non valide", Toast.LENGTH_SHORT).show();	
	}
/*	else if (!mailcontact.getText().toString().matches("")){
		if(!checkEmail(mailcontact.getText().toString()))
	{Toast.makeText(AddconActivity.this, "Email non valide", Toast.LENGTH_SHORT).show();}}*/
	else {
if (selectedImagePath=="")
{
Uri uri = Uri.parse("android.resource://com.example.test_app/"+R.drawable.unknown);
selectedImagePath=uri.toString();
}
dbHelper.createContact(namecontact.getText().toString(), sipcontact.getText().toString(),selectedImagePath,mailcontact.getText().toString());
dbHelper.close();
finish();   
}}
});
}

@Override
protected void onStart() {
        super.onStart();
    	ActionBar aB = getSupportActionBar();
		aB.setTitle("Ajouter contact");
		aB.setIcon(R.drawable.addctc); 
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
	if (requestCode==BROWSE_REQUEST){
imageUri1 = data.getData();
      String[] filePathColumn = { MediaStore.Images.Media.DATA };

      Cursor cursor = getContentResolver().query(imageUri1,
              filePathColumn, null, null, null);
      cursor.moveToFirst();

      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      selectedImagePath = cursor.getString(columnIndex);
      cursor.close();
}
	if (requestCode==CAMERA_REQUEST){
	selectedImagePath= imageUri2.toString();
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
}	}        
