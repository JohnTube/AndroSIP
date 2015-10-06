package com.example.test_app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class InCall extends SherlockActivity {
	private long mStartTime=0L;
	private TextView mTimeLabel;
	private Handler mHandler;
	
	BroadcastReceiver callBackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (intent.getAction()=="com.test_app.endOutCall"){
        		Log.d("InCall","Call ending");
        		finish();
        	} 
        	else if (intent.getAction()=="com.test_app.outCallAccepted"){
        		Log.d("InCall","Call Accepted");
        		onCallStarted();
        	}
        	else if (intent.getAction()=="com.test_app.outCallConfirmed"){
        		Log.d("InCall","Call Confirmed");
        	}
        	else if (intent.getAction()=="com.test_app.outCallRinging"){
        		Log.d("InCall","Call Ringing");
        	}
        	else if (intent.getAction()=="com.test_app.outCallTimeOut"){
        		Log.d("InCall","Call Timed Out");
        		Toast.makeText(InCall.this,"Pas de réponse", Toast.LENGTH_SHORT).show();
        		 try
 		        {
 		            Thread.sleep(Toast.LENGTH_SHORT);  
 		        }catch(Exception e){
 		        	Log.d("InCall",e.getMessage());
 		        }
        		 finish();
        	}
        	else {}
        	
        }
        };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_call);
		Button btn = (Button)findViewById(R.id.endCallBtn);
		mTimeLabel = (TextView)findViewById(R.id.mTimeLabel);
		mHandler = new Handler();	
		
		ActionBar aB = getSupportActionBar(); //com.actionbarsherlock.app.ActionBar
		
		aB.setTitle("Appel Sortant"); // to change the text in the actionBar
		aB.setIcon(R.drawable.ff);
		
		TextView contentView = (TextView) findViewById(R.id.callee);
		
		//Log.d("Call",this.getIntent().getStringExtra("CALLEE"));
		contentView.setText(this.getIntent().getStringExtra("CALLEE"));
		btn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Receiver.sipCore.rejectCall();
				mHandler.removeCallbacks(mUpdateTimeTask);
				Receiver.sipCore.listen();
				try{
					Thread.sleep(100);
				}catch(Exception r){}
				finish();
			}});
	}
	@Override
	protected void onStart(){
		 super.onStart();
		 IntentFilter iF = new IntentFilter();
			iF.addAction("com.test_app.endOutCall");
			iF.addAction("com.test_app.outCallConfirmed");
			iF.addAction("com.test_app.outCallRinging");
			iF.addAction("com.test_app.outCallTimeOut");
			iF.addAction("com.test_app.outCallAccepted");
			registerReceiver(callBackReceiver, iF);
	}
	
	@Override
	protected void onStop(){
		super.onDestroy();
    	unregisterReceiver(callBackReceiver);
	}
	
	
	private void onCallStarted(){
		if (mStartTime == 0L) {
            mStartTime = System.currentTimeMillis();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
       }
		
		
	}
	
	
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
		       final long start = mStartTime;
		       long millis = SystemClock.uptimeMillis() - start;
		       int seconds = (int) (millis / 1000);
		       int minutes = seconds / 60;
		       seconds     = seconds % 60;

		       
			if (seconds < 10) {
		           mTimeLabel.setText("" + minutes + ":0" + seconds);
		       } else {
		           mTimeLabel.setText("" + minutes + ":" + seconds);            
		       }
		     
		       mHandler.postAtTime(this,
		               start + (((minutes * 60) + seconds + 1) * 1000));
		   }
		};
	
	
}
