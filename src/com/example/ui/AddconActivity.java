package com.example.ui;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.test_app.R;

public class AddconActivity extends SherlockFragmentActivity {
	private Uri imageUri;
	private String selectedImagePath;
	private ImageView imageView;
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
				    imageUri = Uri.fromFile(file);
				   
				    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				    i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				    
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
                 		  	/* if (selectedImagePath=="")
                 		  	 {
                 		  		Uri uri = Uri.parse("android.resource:com.example.androsip/drawable/unknown");
                 		  		selectedImagePath=uri.toString();
                 		  	 }
                 		  	 else*/
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	   switch(resultCode){
    	case 10:{
	        imageUri = data.getData();
	        selectedImagePath = getRealPathFromURI(imageUri);
	   }
    	case CAMERA_REQUEST:{
    		Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            imageView.setImageBitmap(photo);
            selectedImagePath = getRealPathFromURI(imageUri);
    		}
	    }
	    }

public String getRealPathFromURI(Uri contentUri) {

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
}	    