package com.example.ui;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;


public class AddconActivity extends SherlockFragmentActivity {
private Uri imageUri1;
private Uri imageUri2;
private String selectedImagePath="";
private static final int CAMERA_REQUEST = 1888; 
private ContactDB dbHelper;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_addcon);
final EditText sipcontact = (EditText) findViewById(R.id.sipcontact);
final EditText namecontact = (EditText) findViewById(R.id.namecontact);
Button addcon = (Button) findViewById(R.id.add);
Button imagebrowse = (Button) findViewById(R.id.imagebrowse);
Button imagecapture = (Button) findViewById(R.id.imagecapture);
Button chsip = (Button) findViewById(R.id.chsip);

chsip.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View v) {
	LayoutInflater factory = LayoutInflater.from(AddconActivity.this);
    final View alertDialogView = factory.inflate(R.layout.dialog, null);

    //Création de l'AlertDialog
    AlertDialog.Builder adb = new AlertDialog.Builder(AddconActivity.this);

    //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
    adb.setView(alertDialogView);

    //On donne un titre à l'AlertDialog
    adb.setTitle("Choose a SIP Provider");
    adb.setIcon(R.drawable.expand);
    adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	//Lorsque l'on cliquera sur annuler on quittera l'application
        	finish();
      } });
    adb.show();

}});
imagebrowse.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View v) {
Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
photoPickerIntent.setType("image/*");
startActivityForResult(photoPickerIntent, 10);				
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
if (sipcontact.getText().toString().matches("")) {
Toast.makeText(AddconActivity.this, "SIP acount missing", Toast.LENGTH_SHORT).show();}
else if (namecontact.getText().toString().matches("")) {
Toast.makeText(AddconActivity.this, "Name missing", Toast.LENGTH_SHORT).show();}
else {
dbHelper = new ContactDB(AddconActivity.this);
dbHelper.open();
if (selectedImagePath=="")
{
Uri uri = Uri.parse("android.resource://com.example.test_app/"+R.drawable.unknown);
selectedImagePath=uri.toString();
}
dbHelper.createContact(namecontact.getText().toString(), sipcontact.getText().toString(),selectedImagePath);
Bundle objetbunble = new Bundle();
objetbunble.putString("namecon",namecontact.getText().toString() );
objetbunble.putString("sipcon", sipcontact.getText().toString());
Intent intent= new Intent(aView.getContext(),MainActivity.class);
intent.putExtras(objetbunble);
startActivityForResult(intent, 6);    
}
}
});
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

protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
super.onActivityResult(requestCode, resultCode, data);
if (resultCode==RESULT_OK)
{
switch(requestCode){
case 10:{
imageUri1 = data.getData();
selectedImagePath = getRealPathFromURI1(imageUri1);
}
case CAMERA_REQUEST:{
/*Bitmap photo = (Bitmap) data.getExtras().get("data"); 
imageView.setImageBitmap(photo);*/
selectedImagePath = getRealPathFromURI2().toString();
}
}
}}

public String getRealPathFromURI1(Uri contentUri) {

// can post image
String [] proj={MediaStore.Images.Media.DATA};
Cursor cursor = getContentResolver().query(contentUri,
proj, // Which columns to return
null,       // WHERE clause; which rows to return (all rows)
null,       // WHERE clause selection arguments (none)
null); // Order-by clause (ascending by name)
int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
cursor.moveToFirst();

return cursor.getString(column_index);
}
public Uri getRealPathFromURI2() {
Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, Media.DATE_ADDED, null, "date_added ASC");
if(cursor != null && cursor.moveToFirst())
{
do {
imageUri2 = Uri.parse(cursor.getString(cursor.getColumnIndex(Media.DATA)));
selectedImagePath = imageUri2.toString();
}while(cursor.moveToNext());

//cursor.close();
}
return imageUri2;}
}	        