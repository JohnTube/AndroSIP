package com.example.test_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;


public class Call extends SherlockActivity {
	//private AudioStream audioStream;
   // private AudioGroup audioGroup;
	
    static Ringtone r=null;
	 static Vibrator v=null;
    
 
    
    BroadcastReceiver callBackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (intent.getAction()=="com.test_app.endInCall"){
        		if (r!=null) {r.stop(); r=null;}
				if (v!=null) {v.cancel(); v=null;}
				finish();}
        
        	
        	else {
        		
        	}
        }
    };

    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	unregisterReceiver(callBackReceiver);
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		
		AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		 switch( audio.getRingerMode() ){
		 case AudioManager.RINGER_MODE_NORMAL:
			Log.d("Receiver","Ring");
			r=RingtoneManager.getRingtone(Call.this,Settings.System.DEFAULT_RINGTONE_URI);
			r.play();

		    break;
		 case AudioManager.RINGER_MODE_SILENT:
			Log.d("Receiver","Silent");
		    break;
		 case AudioManager.RINGER_MODE_VIBRATE:
			Log.d("Receiver","Vibrate");
			v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
			break;
		 }
		
		IntentFilter iF = new IntentFilter();
		iF.addAction("com.test_app.endInCall");
		iF.addAction("com.test_app.startInCall");
		registerReceiver(callBackReceiver, iF);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
	            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
	            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
	            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		setContentView(R.layout.activity_call);
		ActionBar aB = getSupportActionBar(); //com.actionbarsherlock.app.ActionBar
		
		aB.setTitle("Appel Entrant"); // to change the text in the actionBar
		aB.setIcon(R.drawable.ff);
		Log.d("Call","onCreate()");
		final TextView contentView = (TextView) findViewById(R.id.fullscreen_content);
		
		//Log.d("Call",this.getIntent().getStringExtra("CALLEE"));
		contentView.setText(this.getIntent().getStringExtra("CALLEE"));
		
		final Button ack_btn = (Button) findViewById(R.id.accept);
		final Button rej_btn = (Button) findViewById(R.id.reject);
		ack_btn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if (r!=null) {r.stop(); r=null;}
				if (v!=null) {v.cancel(); v=null;}
				Receiver.sipCore.acceptCall();
			   
				
				//Intent intent=new Intent(Call.this,InCall.class);
				//startActivity(intent);
			}
		});
		rej_btn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if (r!=null) {r.stop(); r=null;}
				if (v!=null) {v.cancel(); v=null;}
				Receiver.sipCore.rejectCall();
				Receiver.sipCore.listen();
				finish();
			}
		});
		/*MyPhoneStateListener mPsL = new MyPhoneStateListener();
		TelephonyManager telephony = (TelephonyManager) 
			   this.getSystemService(Context.TELEPHONY_SERVICE);
			    telephony.listen(mPsL,PhoneStateListener.LISTEN_CALL_STATE);*/
	}
	/*
	private class MyPhoneStateListener extends PhoneStateListener {
		  public void onCallStateChanged(int state,String incomingNumber){
		  switch(state){
		    case TelephonyManager.CALL_STATE_IDLE:
		      Log.d("Call", "IDLE");
		      Receiver.sipCore.listen();
		      finish();
		    break;
		    case TelephonyManager.CALL_STATE_OFFHOOK:
		      Log.d("Call", "OFFHOOK");
		    break;
		    case TelephonyManager.CALL_STATE_RINGING:
		      Log.d("Call", "RINGING");
		    break;
		    }
		  } 
		}*/
}
