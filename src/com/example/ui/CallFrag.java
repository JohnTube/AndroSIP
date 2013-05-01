package com.example.ui;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.test_app.R;


public class CallFrag extends SherlockFragment {
private String user;
private String domain;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.activity_call_frag, container, false);
		final Button btnDial = (Button)view.findViewById(R.id.btnDial);
		btnDial.setOnClickListener(new View.OnClickListener() {
			@Override
	        public void onClick(View aView)
	        {
	               Intent intent = new Intent(getActivity(),SearchActivity.class);
	               getActivity().startActivityForResult(intent, 5);
	        }});
		final EditText username = (EditText) view.findViewById(R.id.username);
		this.user=username.toString();
		Button chsip = (Button) view.findViewById(R.id.chsip2);
		chsip.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			 LayoutInflater factory = LayoutInflater.from(getActivity());
		    final View alertDialogView = factory.inflate(R.layout.dialog, null);

		    //Création de l'AlertDialog
		    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

		    //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
		    adb.setView(alertDialogView);

		    //On donne un titre à l'AlertDialog
		    adb.setTitle("Choose a SIP Provider");
		    adb.setIcon(R.drawable.expand);
		    final ListView listview2=(ListView) alertDialogView.findViewById(R.id.list2);
	      
		    ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	        HashMap<String, String> map;
	        map = new HashMap<String, String>();
            map.put("titre", "Ekiga");
	        map.put("img", String.valueOf(R.drawable.ekiga));
	        map.put("sip", "@ekiga.net");
	        listItem.add(map);
	        map = new HashMap<String, String>();
	        map.put("titre", "Freephonie");
	        map.put("img", String.valueOf(R.drawable.freephonie));
	        map.put("sip", "@freephonie.net");
	        listItem.add(map);
	 
	        map = new HashMap<String, String>();
	        map.put("titre", "Other");
	        map.put("img", String.valueOf(R.drawable.basic));
	        map.put("sip", "other");
	        listItem.add(map);
	        
	        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
	        SimpleAdapter mSchedule = new SimpleAdapter (getActivity().getBaseContext(), listItem, R.layout.affichage_liste2,
	               new String[] {"img", "titre"}, new int[] {R.id.provider, R.id.sipprov});
	 
	        //On attribut à notre listView l'adapter que l'on vient de créer
	       listview2.setAdapter(mSchedule);
	       listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
	        	@SuppressWarnings("unchecked")
	         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
					//on récupère la HashMap contenant les infos de notre item (titre, description, img)
	        		HashMap<String, String> map = (HashMap<String, String>) listview2.getItemAtPosition(position);
	        		domain=map.get("sip"); 
	        		if (domain=="other")
	        		{LayoutInflater factory = LayoutInflater.from(getActivity());
	        			 final View alertDialogView2 = factory.inflate(R.layout.dialog2, null);
	        			AlertDialog.Builder adb2 = new AlertDialog.Builder(getActivity());
	        			adb2.setView(alertDialogView2);
	        			adb2.setTitle("Donner votre Domaine");
	        			 adb2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        		            public void onClick(DialogInterface dialog, int which) {
	        		 
	        		            	//Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
	        		            	EditText et = (EditText)alertDialogView2.findViewById(R.id.chdom);
	        		            	domain=et.getText().toString();
	        		            
	        		            	Log.d("zamara", domain);
	        		          } });
	        		 
	        		        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
	        		        adb2.setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
	        					public void onClick(DialogInterface dialog,int id) {
	        						domain="";
	        						dialog.cancel();
	        					}
	        				});
	        		        
	        		       
	        		        
	        		        adb2.show();			
	        		}
	        		
	        		Toast.makeText(getActivity(),domain, Toast.LENGTH_SHORT).show();
				}});
	       
	       
		    adb.setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					
					dialog.cancel();
				}
			});
		    
		    		adb.show();

		}});
		
	
		return view;
			
	}
}
	
