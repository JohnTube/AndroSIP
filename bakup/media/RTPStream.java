package com.example.media;

import android.util.Log;

import com.example.media.net.SipdroidSocket;

public class RTPStream {
	SipdroidSocket socket=null;
	RtpStreamSender sender=null;
	   RtpStreamReceiver receiver=null; 
	   /** Starts media application */
	   public RTPStream(RtpStreamSender rtp_sender,RtpStreamReceiver rtp_receiver){
		   this.receiver=rtp_receiver;
		   this.sender=rtp_sender;
		   
	   }
	   
	   public RTPStream(int local_port, String remote_addr, int remote_port, int mode){
		   try{
			  long frame_rate =50;
					  int frame_size =160;
			   socket = new SipdroidSocket(local_port);
			   if (mode >= 0) {
			   sender = new RtpStreamSender(false,
                       frame_rate, frame_size,
                       socket, remote_addr,
                       remote_port);}
			   if (mode <=0)
			   receiver = new RtpStreamReceiver(socket);
			   
		   }catch (Exception e){
			   Log.d("RTPStream",e.getMessage());
		   }
		   
	   }
	   
	   public boolean startMedia()
	   {  		   
	      if (sender!=null)
	      {  sender.start();
	    	  Log.d("RTPStream","start sending");
	         
	      }
	      if (receiver!=null)
	      {  
	         receiver.start();
	         Log.d("RTPStream","start receiving");
	      }
	      
	      return true;      
	   }
	   
	   /** Stops media application */
	   public boolean stopMedia()
	   {  //printLog("halting java audio..",LogLevel.HIGH);    
	      if (sender!=null && sender.isRunning())
	      {  sender.halt(); sender=null;
	         Log.d("RTPStream","sender halted");
	      }      
	      if (receiver!=null && sender.isRunning())
	      {  receiver.halt(); receiver=null;
	      Log.d("RTPStream","receiver halted");
	      }      
	      if (socket != null) {
	          socket.close();
	          Log.d("RTPStream","socket closed");}
	      return true;
	   }
	
}
