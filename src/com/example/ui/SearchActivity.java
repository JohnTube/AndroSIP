package com.example.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.test_app.R;
import com.example.ui.contacts.ContactDB;
import com.example.ui.contacts.ShowContact;

// this will replace the contactsFrag without the addContact button and the search button and EditText (filter) will be in the actionBar itself 
public class SearchActivity extends SherlockFragmentActivity {
	private EditText search;
	public ContactDB dbHelper;
	 private SimpleCursorAdapter dataAdapter;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		dbHelper = new ContactDB(this);
		dbHelper.open();
	    setContentView(R.layout.activity_search);
		listView = (ListView)findViewById(R.id.list2);
	}
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	//	final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
	  //  searchView.setQueryHint("Search");
	    
	    menu.add(Menu.NONE,Menu.NONE,1,"Search")
	        .setIcon(R.drawable.abs__ic_search)
	        .setActionView(listView)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	    return super.onCreateOptionsMenu(menu);
	 }

	@Override
	protected void onStart(){
		super.onStart();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		displayListView(listView);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	        if (item.getOrder()==1){
	            	Log.d("ch3ar","searchin");
	                search = (EditText) item.getActionView();
	                search.addTextChangedListener(new TextWatcher() {
	               	 
	             	   public void afterTextChanged(Editable s) {
	             	   }
	             	 
	             	   public void beforeTextChanged(CharSequence s, int start, 
	             	     int count, int after) {
	             		   Log.d("ch3ar","before");
	             	   }
	             	 
	             	   public void onTextChanged(CharSequence s, int start, 
	             	     int before, int count) {
	             		  dataAdapter.getFilter().filter(s.toString());
	             	   }
	             	  });
	        }
	        if (item.getItemId()== android.R.id.home){
	            	finish();            
	        }   
	    	return super.onOptionsItemSelected(item);
	    }
	private void displayListView(ListView listview) {
		Log.d("ch3ar","here1");
		//dbHelper.open();
		Log.d("ch3ar","here2");
		
		Cursor cursor = dbHelper.fetchAllContacts();
		Log.d("ch3ar","here3"); 
		  // The desired columns to be bound
		  String[] columns = new String[] {
				  ContactDB.COL_NAME,
				ContactDB.COL_IMAGE
		  };
		  Log.d("ch3ar","here4"); 
		  // the XML defined views which the data will be bound to
		  int[] to = new int[] {
		    R.id.namecon,
		  R.id.imagecon
		  };
		  Log.d("ch3ar","here5"); 
		  // create the adapter using the cursor pointing to the desired data 
		  //as well as the layout information
		 dataAdapter = new SimpleCursorAdapter(this, R.layout.affichage_liste, cursor,  columns,  to,0);
	
	Log.d("ch3ar","here6"); 
		  // Assign adapter to ListView
	 		if (listView==null) Log.d("ch3ar","listView=null");
		  listView.setAdapter(dataAdapter);
		  Log.d("ch3ar","here7"); 
		 
		  listView.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> listView, View view, 
			     int position, long id) {
			   // Get the cursor, positioned to the corresponding row in the result set
			   Cursor cursor = (Cursor) listView.getItemAtPosition(position);
			  final String namecontact =cursor.getString(cursor.getColumnIndexOrThrow("name"));
			  Intent intent = new Intent(getParent(),ShowContact.class);
			  intent.putExtra("NOM",namecontact);
				startActivityForResult(intent,8);
			   }
			  });
		  Log.d("ch3ar","here8"); 
		  dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
		         public Cursor runQuery(CharSequence constraint) {
		             return dbHelper.fetchContactByName(constraint.toString());
		         }
		     });
		  // added
		  Log.d("ch3ar","here9"); 
		 //dbHelper.close();
		 }

}
