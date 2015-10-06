/*
 * Copyright (C) 2010 Luca Veltri - University of Parma - Italy
 * 
 * This source code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Luca Veltri (luca.veltri@unipr.it)
 */

package com.example.media;



import java.io.IOException;
import java.net.SocketException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.util.Log;

import com.example.media.net.RtpPacket;
import com.example.media.net.RtpSocket;
import com.example.media.net.SipdroidSocket;
import com.example.test_app.Receiver;



/** RtpStreamReceiver is a generic RTP receiver.
  * It receives packets from RTP and writes media into a given OutputStream.
  */
public class RtpStreamReceiver extends Thread
{

    /** Whether working in debug mode. */
    public static boolean DEBUG = true;



    static String codec = "";

    /** Size of the read buffer */
    public static final int BUFFER_SIZE = 1024;

    /** Maximum blocking time, spent waiting for reading new bytes [milliseconds] */
    public static final int SO_TIMEOUT = 1000;

    /** The RtpSocket */
    RtpSocket rtp_socket = null;

    /** Whether it is running */
    boolean running;
    AudioManager am;
    
    public static int speakermode = -1;
    static int oldvol = -1;
    
    /**
     * Constructs a RtpStreamReceiver.
     * 
     * @param output_stream
     *            the stream sink
     * @param socket
     *            the local receiver SipdroidSocket
     */
    public RtpStreamReceiver(SipdroidSocket socket) {
            init(socket);
          
    }

    /** Inits the RtpStreamReceiver */
    private void init(SipdroidSocket socket) {
            if (socket != null)
                    rtp_socket = new RtpSocket(socket);
    }

    /** Whether is running */
    public boolean isRunning() {
            return running;
    }

    /** Stops running */
    public void halt() {
            running = false;
    }
    
    public static float good, late, lost, loss, loss2;
    double avgheadroom,devheadroom;
    int avgcnt;
    public static int timeout;
    int seq;
    
    void empty() {
            try {
                    rtp_socket.getDatagramSocket().setSoTimeout(1);
                    for (;;)
                            rtp_socket.receive(rtp_packet);
            } catch (SocketException e) {
                Log.d("RtpStreamReceiver","e"+e.getMessage());
            } catch (IOException e1) {
            	 Log.d("RtpStreamReceiver","e1"+e1.getMessage());
            }
            try {
                    rtp_socket.getDatagramSocket().setSoTimeout(SO_TIMEOUT);
            } catch (SocketException e2) {
            	 Log.d("RtpStreamReceiver","e2"+e2.getMessage());
            }
            seq = 0;
    }
    
    static int stream() {
        return speakermode == AudioManager.MODE_IN_CALL?AudioManager.STREAM_VOICE_CALL:AudioManager.STREAM_MUSIC;
}
    
    RtpPacket rtp_packet = null;
    AudioTrack track;
    int maxjitter,minjitter,minjitteradjust;
    int cnt,cnt2,user,luser,luser2,lserver;
    public static int jitter,mu;
    
   
	void setCodec() {
            synchronized (this) {
                    AudioTrack oldtrack;
                    
                   // p_type.codec.init();
                    codec = "PCMU";
                    mu = 8000/8000;
                    maxjitter = AudioTrack.getMinBufferSize(8000, 
                                    AudioFormat.CHANNEL_OUT_MONO, 
                                    AudioFormat.ENCODING_PCM_16BIT);
                    Log.d("RtpStreamReceiver","maxjitter="+maxjitter);
                    
                    if (maxjitter < 2*2*BUFFER_SIZE*3*mu)
                            maxjitter = 2*2*BUFFER_SIZE*3*mu;
                    oldtrack = track;
                    track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                                    maxjitter*2, AudioTrack.MODE_STREAM);
                    if (track==null) Log.d("RtpStreamReceiver","track==null");
                     maxjitter /= 2*2;
                    minjitter = minjitteradjust = 500*mu;
                    jitter = 875*mu;
                    devheadroom = Math.pow(jitter/5, 2);
                    timeout = 1;
                    luser = luser2 = -8000*mu;
                    cnt = cnt2 = user = lserver = 0;
                    if (oldtrack != null) {
                            oldtrack.stop();
                            oldtrack.release();
                    }
            }
    }
    
    void write(short a[],int b,int c) {
        synchronized (this) {
                user += track.write(a,b,c);
        }
    }
    
    void newjitter(boolean inc) {
        if (good == 0 || lost/good > 0.01)
                return;
        int newjitter = (int)Math.sqrt(devheadroom)*5 + (inc?minjitteradjust:0);
        if (newjitter < minjitter)
                newjitter = minjitter;
        if (newjitter > maxjitter)
                newjitter = maxjitter;
        if (!inc && (Math.abs(jitter-newjitter) < minjitteradjust || newjitter >= jitter))
                return;
        if (inc && newjitter <= jitter)
                return;
        jitter = newjitter;
        late = 0;
        avgcnt = 0;
        luser2 = user;
}
    
   /** Runs it in a new Thread. */
    public void run() {
    	
        if (rtp_socket == null) {
                if (DEBUG)
                        Log.d("RtpStreamReceiver","ERROR: RTP socket is null");
                return;
        }
      //  android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        byte[] buffer = new byte[BUFFER_SIZE+12];
        rtp_packet = new RtpPacket(buffer, 0);

      

        running = true;
       

        
        am = (AudioManager) Receiver.mContext.getSystemService(Context.AUDIO_SERVICE);
       // cr = Receiver.mContext.getContentResolver();
       /* saveSettings();
        Settings.System.putInt(cr, Settings.System.WIFI_SLEEP_POLICY,Settings.System.WIFI_SLEEP_POLICY_NEVER);
        am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,AudioManager.VIBRATE_SETTING_OFF);
        am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,AudioManager.VIBRATE_SETTING_OFF);*/
        if (oldvol == -1) oldvol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
       // initMode();
        setCodec();
        short lin[] = new short[BUFFER_SIZE];
        short lin2[] = new short[BUFFER_SIZE];
        int server = 0, headroom, todo, len = 0, m = 1, expseq, getseq, vm = 1, gap, gseq;
        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_VOICE_CALL,(int)(ToneGenerator.MAX_VOLUME*2*0.25));
        track.play();
        //System.gc();
        empty();
       // lockFirst = true;
        while (running) {
              //  lock(true);
         
                try {
                        rtp_socket.receive(rtp_packet);
                        if (timeout != 0) {
                                tg.stopTone();
                                track.pause();
                                for (int i = maxjitter*2; i > 0; i -= BUFFER_SIZE)
                                        write(lin2,0,i>BUFFER_SIZE?BUFFER_SIZE:i);
                                cnt += maxjitter*2;
                                track.play();
                                empty();
                        }
                        timeout = 0;
                } catch (IOException e3) {
                	e3.printStackTrace();
                	Log.d("RtpStreamReceiver","e3 "+e3.toString());
                        if (timeout == 0) {
                                tg.startTone(ToneGenerator.TONE_SUP_RINGTONE);
                        }
                        rtp_socket.getDatagramSocket().disconnect();
                        if (++timeout > 60) {
                           //     Receiver.endCall();
                             
                        	  break;
                        }
                }
                if (running && timeout == 0) {          
                         gseq = rtp_packet.getSequenceNumber();
                         if (seq == gseq) {
                                 m++;
                                 continue;
                         }
                         gap = (gseq - seq) & 0xff;
                         if (gap > 240)
                                 continue;
                         server = track.getPlaybackHeadPosition();
                         headroom = user-server;
                         
                         if (headroom > 2*jitter)
                                 cnt += len;
                         else
                                 cnt = 0;
                         
                         if (lserver == server)
                                 cnt2++;
                         else
                                 cnt2 = 0;

                       
                         
                         avgheadroom = avgheadroom * 0.99 + (double)headroom * 0.01;
                         if (avgcnt++ > 300)
                                 devheadroom = devheadroom * 0.999 + Math.pow(Math.abs(headroom - avgheadroom),2) * 0.001;
                         if (headroom < 250*mu) { 
                                 late++;
                                 avgcnt += 10;
                                 if (avgcnt > 400)
                                         newjitter(true);
                                 todo = jitter - headroom;
                                 write(lin2,0,todo>BUFFER_SIZE?BUFFER_SIZE:todo);
                         }

                         if (cnt > 500*mu && cnt2 < 2) {
                                 todo = headroom - jitter;
                                 if (todo < len)
                                         write(lin,todo,len-todo);
                         } else
                                 write(lin,0,len);
                                                         
                         if (seq != 0) {
                                 getseq = gseq&0xff;
                                 expseq = ++seq&0xff;
                                 if (m == RtpStreamSender.m) vm = m;
                                 gap = (getseq - expseq) & 0xff;
                                 if (gap > 0) {
                                         if (gap > 100) gap = 1;
                                         loss += gap;
                                         lost += gap;
                                         good += gap - 1;
                                         loss2++;
                                 } else {
                                         if (m < vm) {
                                                 loss++;
                                                 loss2++;
                                         }
                                 }
                                 good++;
                                 if (good > 110) {
                                         good *= 0.99;
                                         lost *= 0.99;
                                         loss *= 0.99;
                                         loss2 *= 0.99;
                                         late *= 0.99;
                                 }
                         }
                         m = 1;
                         seq = gseq;
                         /*
                         if (user >= luser + 8000*mu && (
                                         Receiver.call_state == UserAgent.UA_STATE_INCALL ||
                                         Receiver.call_state == UserAgent.UA_STATE_OUTGOING_CALL)) {
                                 if (luser == -8000*mu || getMode() != speakermode) {
                                         saveVolume();
                                         setMode(speakermode);
                                         restoreVolume();
                                 }*/
                                 luser = user;
                                 if (user >= luser2 + 160000*mu)
                                         newjitter(false);
                         }
                         lserver = server;
                }
        
       //lock(false);
        track.stop();
        track.release();
        tg.stopTone();
        tg.release();
        //saveVolume();
        am.setStreamVolume(AudioManager.STREAM_MUSIC,oldvol,0);
        //restoreSettings();
        //enableBluetooth(false);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,oldvol,0);
        oldvol = -1;
        
        rtp_socket.close();
        rtp_socket = null;
        codec = "";
        
   

        if (DEBUG)
               Log.d("RtpStreamReceiver","rtp receiver terminated");

        
}
}


