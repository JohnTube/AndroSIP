package com.example.test_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.example.ui.LoginActivity;

public class Receiver extends BroadcastReceiver {
	 public static SipCore sipCore;
	 public static Context mContext;
	 
	 
	 final static String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
     final static String ACTION_DATA_STATE_CHANGED = "android.intent.action.ANY_DATA_STATE";
     final static String ACTION_DOCK_EVENT = "android.intent.action.DOCK_EVENT";
     final static String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE";
     final static String ACTION_SCO_AUDIO_STATE_CHANGED = "android.media.SCO_AUDIO_STATE_CHANGED";
     final static String EXTRA_SCO_AUDIO_STATE = "android.media.extra.SCO_AUDIO_STATE";
     final static String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
     final static String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
	 
	 public static final String REGISTER_ACTION = "com.example.intent.action.register";
	 public static final String START_SIP_CORE = "com.example.intent.action.startsip"; 
	 public static final String FIRST_LOGIN = "com.example.intent.action.first_login";
	 public static final String INCOMING_CALL_ACTION = "com.example.intent.action.in_call";
	 public static boolean sipStarted = false;
	 public static boolean hasAccount = false ;
	 
	 
	 
	 private static String laststate,lastnumber; 
	 public static String pstn_state;
	 
	 //synchronized why ??
	 public static synchronized SipCore engine(Context context) {
		 if (context==null) {
			 sipCore = new SipCore();
			 return sipCore;
		 }
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
	 
	 public static void register(){
		sipCore.register();
	 }
	 

	 

	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("Receiver",intent.getAction().toString());
		if(intent.getAction().equals(INCOMING_CALL_ACTION)) {
			arg0.startActivity(new Intent(arg0,Call.class));
		}
		else if (intent.getAction().equals(FIRST_LOGIN)){
			arg0.startActivity(new Intent(arg0,LoginActivity.class));
			
		}
	}
	
	public boolean isRegistered(){
	return sipCore.isRegistered();
	}
	
	static boolean was_playing;
    
    static void broadcastCallStateChanged(String state,String number) {
            if (state == null) {
                    state = laststate;
                    number = lastnumber;
            }
            Intent intent = new Intent(ACTION_PHONE_STATE_CHANGED);
            intent.putExtra("state",state);
            if (number != null)
                    intent.putExtra("incoming_number", number);
            intent.putExtra(mContext.getString(R.string.app_name), true);
            mContext.sendBroadcast(intent, android.Manifest.permission.READ_PHONE_STATE);
            if (state.equals("IDLE")) {
                    if (was_playing) {
                            if (pstn_state == null || pstn_state.equals("IDLE"))
                                    mContext.sendBroadcast(new Intent(TOGGLEPAUSE_ACTION));
                            was_playing = false;
                    }
            } else {
                    AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    if ((laststate == null || laststate.equals("IDLE")) && (was_playing = am.isMusicActive()))
                            mContext.sendBroadcast(new Intent(PAUSE_ACTION));
            }
            laststate = state;
            lastnumber = number;
    }


	public static void startCall() {
		if(mContext==null) Log.d("SipCore","context null");
		 else {
			 
			 }
			 Intent intent = new Intent("com.test_app.startCall");
			 mContext.sendBroadcast(intent);
		 }
	
	
	/****** Incoming call methods ***********/
	 public static void inCallScreen(String caller){
		 if(mContext==null) Log.d("SipCore","context null");
		 else {
			 
			 
			 Intent inCall = new Intent(mContext,Call.class);
			 inCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 inCall.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			 inCall.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			 inCall.putExtra("CALLEE",caller);
			 mContext.startActivity(inCall);
		 }
	 }
	
		public static void inCallEnd() {
			// TODO Auto-generated method stub
			Intent intent = new Intent("com.test_app.endInCall");
		     mContext.sendBroadcast(intent);
		}

	 
	/****** Outgoing call methods ***********/


	
	public static void outCallAccepted() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.test_app.outCallAccepted");
	     mContext.sendBroadcast(intent);
	   
	}

	public static void outCallEnd() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.test_app.endOutCall");
	     mContext.sendBroadcast(intent);
	}

	public static void outCallConfirmed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.test_app.outCallConfirmed");
	     mContext.sendBroadcast(intent);
	}

	public static void outCallRinging() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.test_app.outCallRinging");
	     mContext.sendBroadcast(intent);
	}

	public static void outCallTimeOut() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.test_app.outCallTimeOut");
	     mContext.sendBroadcast(intent);
	}


	
	
	/*
	 *  public static void alarm(int renew_time,Class <?>cls) {
                if (!Sipdroid.release) Log.i("SipUA:","alarm "+renew_time);
                Intent intent = new Intent(mContext, cls);
                PendingIntent sender = PendingIntent.getBroadcast(mContext,
                        0, intent, 0);
                        AlarmManager am = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
                        am.cancel(sender);
                        if (renew_time > 0)
                                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+renew_time*1000, sender);
                }
                
                public static long expire_time;
                
                public static synchronized void reRegister(int renew_time) {
                        if (renew_time == 0)
                                expire_time = 0;
                        else {
                                if (expire_time != 0 && renew_time*1000 + SystemClock.elapsedRealtime() > expire_time) return;
                                expire_time = renew_time*1000 + SystemClock.elapsedRealtime();
                        }
                alarm(renew_time-15, OneShotAlarm.class);
                }
	 */
	
}
