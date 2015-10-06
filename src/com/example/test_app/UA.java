package com.example.test_app;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import org.zoolu.net.IpAddress;
import org.zoolu.sdp.AttributeField;
import org.zoolu.sdp.MediaField;
import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.call.Call;
import org.zoolu.sip.call.CallWatcher;
import org.zoolu.sip.call.CallWatcherListener;
import org.zoolu.sip.call.ExtendedCall;
import org.zoolu.sip.dialog.ExtendedInviteDialog;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.util.Log;


/*
 * This class implements the SIP UserAgent (both UA Server and UA Client)
 */
public class UA implements CallWatcherListener {
	//private CallWatcher receptionDeamon;
	protected SipProvider sipProvider;
    protected ExtendedCall call;
   // protected Call incomingCall;
	 protected ExtendedInviteDialog extDlg; // 2B or not 2B used
	
	protected String user;
	protected String pass;
	protected String realm;
	protected String callee;
	
	protected static int remote_media_port;
	protected static int local_media_port=7777;
	
	protected String remote_addr;
	
	protected SessionDescriptor local_sdp;
	protected SessionDescriptor remote_sdp;
	
	int avp=0;
	String default_codec="PCMU";
	int rate=8000;
		

	public static void setLocalMediaPort(int port){
		UA.local_media_port=port;
		
	}
	
	public static int getRemoteMediaPort(){
		return remote_media_port;
		
	}
	
	
	public UA(SipProvider sipProvider, String u, String p, String r){
		this.sipProvider=sipProvider;
		this.user=u;
		this.pass=p;
		this.realm=r;
		
		
	};
	
	private void addMediaDescriptor(String media, int port, int avp,
            String codec, int rate) {
				String attr_param = String.valueOf(avp);

		if (codec != null)
		{
			attr_param += " " + codec + "/" + rate;
		}
		local_sdp.addMedia(new MediaField(media, port, 0, "RTP/AVP", 
				String.valueOf(avp)), 
				new AttributeField("rtpmap", attr_param));
	}
	
	public void UAS(){
		//this.sipProvider=sipProvider;
		//call = new ExtendedCall();
		Log.d("UA","listening");
		call = new ExtendedCall(sipProvider,  new NameAddress("sip:"+user+"@"+IpAddress.getLocalHostAddress().toString()), 
        		user,realm,pass,this);
		call.listen();
	}
	
	@SuppressLint("NewApi")
	public void accept(){
		//SessionDescriptor local_sdp = new SessionDescriptor();
		//local_sdp.
		//Log.d("UA","accepting call sdp="+local_sdp.toString());
		//Log.d("UA","call remote sdp"+call.getRemoteSessionDescriptor());
		//if (call.isIncoming()) Log.d("UA","call is incoming");
		 try {
		        AudioGroup audioGroup = new AudioGroup();
		                audioGroup.setMode(AudioGroup.MODE_NORMAL);

		                AudioStream audioStream = new AudioStream(InetAddress.getByName(IpAddress.getLocalHostAddress().toString()));
		                audioStream.setCodec(AudioCodec.PCMU);
		                audioStream.setMode(RtpStream.MODE_NORMAL);
		                local_media_port = audioStream.getLocalPort();
		                UA.setLocalMediaPort(local_media_port);
		                Log.d("UA","local_port="+local_media_port);
		        audioStream.associate(InetAddress.getByName(remote_addr), UA.getRemoteMediaPort());
		                audioStream.join(audioGroup);
		                Log.d("UA","callee IP="+remote_addr);
		        AudioManager Audio =  (AudioManager) Receiver.mContext.getSystemService(Context.AUDIO_SERVICE); 
		                    Audio.setMode(AudioManager.MODE_IN_CALL);

		    } catch (SocketException e) {
		                e.printStackTrace();
		                Log.d("UA",e.getLocalizedMessage());
		            } catch (UnknownHostException e) {
		                e.printStackTrace();
		                Log.d("UA",e.getLocalizedMessage());
		            }
		            catch (Exception ex) {
		                ex.printStackTrace();
		                Log.d("UA",ex.getLocalizedMessage());
		            }
		 local_sdp = new SessionDescriptor(
					IpAddress.getLocalHostAddress().toString(),
	                sipProvider.getViaAddress());
			addMediaDescriptor("audio",local_media_port,avp,default_codec,rate);		
			Log.d("UA",local_sdp.toString());
		call.accept(local_sdp.toString());
		//r_s = new RTPStream(local_media_port,remote_addr,remote_media_port,0);
		//r_s.startMedia();
	}
	
	public void reject(){
		//call.refuse();
		Log.d("UA","rejecting call");
		
		if (call.isActive()) 
		{	Log.d("UA","raccrocher");
			call.refuse();
		//	r_s.stopMedia();
		//	r_s=null;
		}
		else if (call.isIncoming()) {call.hangup();
		Log.d("UA","ignorer appel");
		}
		
		else {call.hangup();
		Log.d("UA","annuler appel");
		call.refuse();
		}
		//call.hangup();
		//call.refuse();
	}
	
	@SuppressLint("NewApi")
	public void UAC(NameAddress callee){
		this.callee=callee.getAddress().toString();
		Log.d("UA","calling");
		call = new ExtendedCall(sipProvider,  new NameAddress("sip:"+user+"@"+IpAddress.getLocalHostAddress().toString()), 
        		user,realm,pass,this);
		 try {
		        AudioGroup audioGroup = new AudioGroup();
		                audioGroup.setMode(AudioGroup.MODE_NORMAL);

		                AudioStream audioStream = new AudioStream(InetAddress.getByName(IpAddress.getLocalHostAddress().toString()));
		                audioStream.setCodec(AudioCodec.PCMU);
		                audioStream.setMode(RtpStream.MODE_NORMAL);
		                local_media_port = audioStream.getLocalPort();
		                UA.setLocalMediaPort(local_media_port);
		                Log.d("UA","local_port="+local_media_port);
		        audioStream.associate(InetAddress.getByName(callee.getAddress().getHost()), UA.getRemoteMediaPort());
		                audioStream.join(audioGroup);
		                Log.d("UA","callee IP="+callee.getAddress().getHost());
		        AudioManager Audio =  (AudioManager) Receiver.mContext.getSystemService(Context.AUDIO_SERVICE); 
		                    Audio.setMode(AudioManager.MODE_IN_CALL);

		    } catch (SocketException e) {
		                e.printStackTrace();
		                Log.d("UA",e.getLocalizedMessage());
		            } catch (UnknownHostException e) {
		                e.printStackTrace();
		                Log.d("UA",e.getLocalizedMessage());
		            }
		            catch (Exception ex) {
		                ex.printStackTrace();
		                Log.d("UA",ex.getLocalizedMessage());
		            }
		call.call(callee, "v=0\no=AndroSip 666 777 IN IP4 "+IpAddress.getLocalHostAddress().toString()+"\ns=AndroSip" +
				"\nc=IN IP4 "+IpAddress.getLocalHostAddress().toString()+"\nt=0 0\nm=audio "+local_media_port+" RTP/AVP 112 111 110 3 0 8 101\na=rtpmap:112");
		//call.call(callee);
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
		Log.d("UA","call accepted");
		//Receiver.startCall();
		Receiver.outCallAccepted();
		 
		
	}

	@Override
	public void onCallBye(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		Log.d("UA","call bye");
		UAS();
		if (call.isIncoming())
			Receiver.inCallEnd();
		if (call.isOutgoing())
			Receiver.outCallEnd();
		//Receiver.endCall();
	}

	@Override
	public void onCallCancel(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		Log.d("UA","call canceled");
		UAS();
		Receiver.inCallEnd();
		/*if (call.isIncoming())
			{Log.d("UA","incoming call canceled");
			Receiver.inCallEnd();}
		else if (call.isOutgoing())
			{Log.d("UA","outgoing call canceled");
			Receiver.outCallEnd();
			}
		else {
			Log.d("UA","call canceled");
		}*/
		//Receiver.endCall();
	}

	@Override
	public void onCallClosed(Call arg0, Message arg1) {
		// TODO Auto-generated method stub
		Log.d("UA","call closed");
		UAS();
		if (call.isIncoming())
			Receiver.inCallEnd();
		if (call.isOutgoing())
			Receiver.outCallEnd();
		//Receiver.endCall();
	}

	@Override
	public void onCallConfirmed(Call arg0, String arg1, Message arg2) {
		// TODO Auto-generated method stub
		Log.d("UA","call confirmed");
		Receiver.outCallConfirmed();
	}

	@Override
	public void onCallInvite(Call cal, NameAddress callee, NameAddress caller,
			String sdp, Message msg) {
		// TODO Auto-generated method stub
		/*Log.d("UA","new call invite ");
		Log.d("UA","callee="+callee.toString()+" caller="+caller.toString());
		Log.d("UA","sdp="+sdp);*/
		cal.ring();
		
		remote_sdp=new SessionDescriptor(cal.getRemoteSessionDescriptor());
		remote_media_port=remote_sdp.getMediaDescriptor("audio").getMedia().getPort();
		remote_addr=remote_sdp.getOrigin().getAddress();
		Log.d("UA","address="+remote_addr+" port="+remote_media_port);
		//remote_media_port=7078; // temp
		Receiver.inCallScreen(caller.toString());
		//cal.accept("v=0\no=AndroSip 666 777 IN IP4 192.168.1.4\ns=kallamni" +
			//	"\nc=IN IP4 192.168.1.4\nt=0 0\nm=audio 7078 RTP/AVP 112 111 110 3 0 8 101\na=rtpmap:112 speex/32000");
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
	public void onCallRefused(Call l, String reason, Message resp) {
		
		//Log.d("UA","call refused "+reason+" msg= "+resp.toString());
		// TODO 401 and 407 authorization
		if (resp.getStatusLine().getCode()==486){
			Log.d("UA","call refused");
			Receiver.outCallEnd();
			/*if (l.isIncoming()) {
				Log.d("UA","inCall refused");
			Receiver.inCallEnd();}
			else if (l.isOutgoing()) {
				Log.d("UA","outCall refused");
				Receiver.outCallEnd();
			}*/
				
			
		}
	}

	@Override
	public void onCallRinging(Call call, Message msg) {
		// TODO Auto-generated method stub
		Log.d("UA","CallRinging");
		//this.incomingCall=call;
		Receiver.outCallRinging();
	}

	@Override
	public void onCallTimeout(Call arg0) {
		// TODO Auto-generated method stub
		Log.d("UA","Call Time Out");
		Receiver.outCallTimeOut();
	}

	@Override
	public void onNewIncomingCall(CallWatcher arg0, ExtendedCall arg1,
			NameAddress arg2, NameAddress arg3, String arg4, Message arg5) {
		// TODO Auto-generated method stub
		Log.d("UA","new incoming call");
		//Receiver.mContext.sendBroadcast(new Intent("com.example.intent.action.in_call"));

	}



}
