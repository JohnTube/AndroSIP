package com.example.ui.contacts;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.test_app.R;

public class ContactsFrag extends SherlockFragment {
	public ContactDB dbHelper;
	 private SimpleCursorAdapter dataAdapter;
	
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        dbHelper = new ContactDB(activity);
	    }
	 //final Context context = this.getActivity();
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view= inflater.inflate(R.layout.activity_contacts_frag, container, false);
		 Button add = (Button) view.findViewById(R.id.addcontact);
		 add.setOnClickListener(new View.OnClickListener() {
				@Override
		        public void onClick(View aView)
		        {
		               Intent intent = new Intent(getActivity(),AddconActivity.class);
		               getActivity().startActivityForResult(intent, 5);
		        }});
		return view;
	}
//}

	@Override
	public void onStart(){
		super.onStart();
		displayListView();
	}
	
private void displayListView() {
	dbHelper.open();
	 Cursor cursor = dbHelper.fetchAllContacts();
	 
	  // The desired columns to be bound
	  String[] columns = new String[] {
			  ContactDB.COL_NAME,
			ContactDB.COL_IMAGE,
			ContactDB.COL_SIP
	  };
	 
	  // the XML defined views which the data will be bound to
	  int[] to = new int[] {
	    R.id.namecon,
	  R.id.imagecon,
	  R.id.sipcon
	  };
	 
	  // create the adapter using the cursor pointing to the desired data 
	  //as well as the layout information
	 dataAdapter = new SimpleCursorAdapter(getActivity(), R.layout.affichage_liste, cursor,  columns,  to,0);
ListView listView = (ListView) getActivity().findViewById(R.id.list);
	  
	  // Assign adapter to ListView
	  listView.setAdapter(dataAdapter);
	
	 
	  listView.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> listView, View view, 
		     int position, long id) {
		   // Get the cursor, positioned to the corresponding row in the result set
		   Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		   final int idcontact = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
		  final String namecontact =cursor.getString(cursor.getColumnIndexOrThrow("name"));
		  final String sipcontact =cursor.getString(cursor.getColumnIndexOrThrow("sip"));
		  final String emailcontact =cursor.getString(cursor.getColumnIndexOrThrow("email"));
		  final String imagecontact =cursor.getString(cursor.getColumnIndexOrThrow("image"));
		  Bundle objetbundle = new Bundle();
		  objetbundle.putInt("id", idcontact);
		  objetbundle.putString("NOM",namecontact);
		  objetbundle.putString("SIP",sipcontact);
		  objetbundle.putString("EMAIL",emailcontact);
		  objetbundle.putString("IMAGE",imagecontact);
		  Intent intent = new Intent(getActivity(),ShowContact.class);
		  intent.putExtras(objetbundle);
			getActivity().startActivityForResult(intent,8);
		   }
		  });
	 
	  EditText myFilter = (EditText) getActivity().findViewById(R.id.myFilter);
	  myFilter.setTextColor(Color.parseColor("#FFFFFF"));
	  myFilter.addTextChangedListener(new TextWatcher() {
	 
	   public void afterTextChanged(Editable s) {
	   }
	 
	   public void beforeTextChanged(CharSequence s, int start, 
	     int count, int after) {
	   }
	 
	   public void onTextChanged(CharSequence s, int start, 
	     int before, int count) {
	    dataAdapter.getFilter().filter(s.toString());
	   }
	  });
	   
	  dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
	         public Cursor runQuery(CharSequence constraint) {
	             return dbHelper.fetchContactByName(constraint.toString());
	         }
	     });
	  // added
	 dbHelper.close();
	 }
}

