package com.example.test_app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.ui.LoginActivity;


public class SipService extends Service {
	Receiver sReceiver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d("SipService","onBind");
		return null;
	}
	@Override
	public void onCreate()
	{	super.onCreate();
		Log.d("SipService","onCreate()");
		Receiver.sipStarted=true;
		Receiver.engine(this);//.register();
		//Receiver.sipCore.listen();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		//hasAccount=pref.getBoolean(LoginActivity.PREF_REGISTERED_ONCE,false);
		if (!pref.getBoolean(LoginActivity.PREF_REGISTERED_ONCE,false)){
			Log.d("MainActivity","first time account");
			Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
            
		}
		else {Receiver.sipCore.init(this);
		Receiver.register();
		Receiver.sipCore.listen();
		}
		
	}
	@Override 
	public void onDestroy(){
		super.onDestroy();
		Log.d("SipService","onDestroy()");
		if (sReceiver!=null	) unregisterReceiver(sReceiver);
	}
	

}
