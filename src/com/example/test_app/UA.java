package com.example.test_app;

import java.util.Vector;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.call.CallWatcher;
import org.zoolu.sip.call.CallWatcherListener;
import org.zoolu.sip.call.ExtendedCall;
import org.zoolu.sip.dialog.ExtendedInviteDialog;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;

import android.content.Intent;
import android.util.Log;
import android.content.BroadcastReceiver;



/*
 * This class implements the SIP UserAgent (both UA Server and UA Client)
 */
public class UA implements CallWatcherListener {
	private CallWatcher receptionDeamon;
	//protected SipProvider sipProvider;
    protected ExtendedCall call;
	protected ExtendedInviteDialog extDlg; // 2B or not 2B used
	
	public void UAS(SipProvider sipProvider){
		//call = new ExtendedCall();
		Log.d("UA","listening");
		call = new ExtendedCall(sipProvider,  new NameAddress("sip:test@192.168.1.4"), 
        		"test","192.168.1.4","test",this);
		call.listen();
	}
	
	@Override
	public void onCallAttendedTransfer(ExtendedCall arg0, NameAddress arg1,
			NameAddress arg2, String arg3, Message arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallTransfer(ExtendedCall arg0, NameAddress arg1,
			NameAddress arg2, Message arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallTransferAccepted(ExtendedCall arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallTransferFailure(ExtendedCall arg0, String arg1,
			Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallTransferRefused(ExtendedCall arg0, String arg1,
			Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallTransferSuccess(ExtendedCall arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallAccepted(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallBye(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallCancel(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallClosed(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallConfirmed(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallInvite(Call arg0, NameAddress arg1, NameAddress arg2,
			String arg3, Message arg4) {
		// TODO Auto-generated method stub
		Log.d("UA","new call invite");
	}

	@Override
	public void onCallModify(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallProgress(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallReInviteAccepted(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallReInviteRefused(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallReInviteTimeout(Call arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallRedirected(Call arg0, String arg1, Vector arg2,
			Message arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallRefused(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallRinging(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallTimeout(Call arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewIncomingCall(CallWatcher arg0, ExtendedCall arg1,
			NameAddress arg2, NameAddress arg3, String arg4, Message arg5) {
		// TODO Auto-generated method stub
		Log.d("UA","new incoming call");
		//Receiver.mContext.sendBroadcast(new Intent("com.example.intent.action.in_call"));

	}

}
