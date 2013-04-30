package com.example.test_app;

import com.example.ui.SipAccount;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
	 public static SipCore sipCore;
	 public static Context mContext;
	 public static final String REGISTER_ACTION = "com.example.intent.action.register";
	 public static final String START_SIP_CORE = "com.example.intent.action.startsip"; 
	 public static final String INCOMING_CALL_ACTION = "com.example.intent.action.in_call";
	 //synchronized why ??
	 public static SipCore engine(Context context) {
         if (mContext == null)
                 mContext = context;
         if (sipCore == null) {
                 sipCore = new SipCore(context);
                 if (sipCore!=null) Log.d("Receiver","SipCore created");
                 //sipCore.StartEngine();
                 
         } //else
                 //sipCore.CheckEngine();
        	 // context.startService(new Intent(context,SipService.class));
         return sipCore;
 }
	 
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(INCOMING_CALL_ACTION)) {
		//	mContext.startActivity(new Intent(mContext,SipAccount.class));
		}
	}
		
}
