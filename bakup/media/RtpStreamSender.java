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

import java.net.InetAddress;
import java.util.Random;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.example.media.net.RtpPacket;
import com.example.media.net.RtpSocket;
import com.example.media.net.SipdroidSocket;
import com.example.test_app.Receiver;

/**
 * RtpStreamSender is a generic stream sender. It takes an InputStream and sends
 * it through RTP.
 */
public class RtpStreamSender extends Thread {
        /** Whether working in debug mode. */
        public static boolean DEBUG = true;

        /** The RtpSocket */
        RtpSocket rtp_socket = null;

        /** Payload type */
       // Codecs.Map p_type;

        /** Number of frame per second */
        int frame_rate;

        /** Number of bytes per frame */
        int frame_size=160;

        /**
         * Whether it works synchronously with a local clock, or it it acts as slave
         * of the InputStream
         */
        boolean do_sync = true;

        /**
         * Synchronization correction value, in milliseconds. It accellarates the
         * sending rate respect to the nominal value, in order to compensate program
         * latencies.
         */
        int sync_adj = 0;

        /** Whether it is running */
        boolean running = false;
        boolean muted = false;
       
        
       
        
        /**
         * Constructs a RtpStreamSender.
         * 
         * @param input_stream
         *            the stream to be sent
         * @param do_sync
         *            whether time synchronization must be performed by the
         *            RtpStreamSender, or it is performed by the InputStream (e.g.
         *            the system audio input)
         * @param payload_type
         *            the payload type
         * @param frame_rate
         *            the frame rate, i.e. the number of frames that should be sent
         *            per second; it is used to calculate the nominal packet time
         *            and,in case of do_sync==true, the next departure time
         * @param frame_size
         *            the size of the payload
         * @param src_socket
         *            the socket used to send the RTP packet
         * @param dest_addr
         *            the destination address
         * @param dest_port
         *            the destination port
         */
        public RtpStreamSender(boolean do_sync,
                               long frame_rate, int frame_size,
                               SipdroidSocket src_socket, String dest_addr,
                               int dest_port) {
                init(do_sync, frame_rate, frame_size,
                                src_socket, dest_addr, dest_port);
               
        }

        /** Inits the RtpStreamSender */
        private void init(boolean do_sync,
                          long frame_rate, int frame_size,
                          SipdroidSocket src_socket, String dest_addr,
                          int dest_port) {
               
                this.frame_rate = (int)frame_rate;
                /*
                        switch (payload_type.codec.number()) {
                        case 0:
                        case 8:
                                this.frame_size = 1024;
                                break;
                        case 9:
                                this.frame_size = 960;
                                break;
                        default:
                                this.frame_size = frame_size;
                                break;
                        }
                else*/
                        this.frame_size = frame_size;
                this.do_sync = do_sync;
                try {
                        rtp_socket = new RtpSocket(src_socket, InetAddress
                                        .getByName(dest_addr), dest_port);
                } catch (Exception e) {
                      Log.d("RtpStreamSender","RtpSocket not created");
                }
        }

        /** Sets the synchronization adjustment time (in milliseconds). */
        public void setSyncAdj(int millisecs) {
                sync_adj = millisecs;
        }

        /** Whether is running */
        public boolean isRunning() {
                return running;
        }
        
        public boolean mute() {
                return muted = !muted;
        }

        public static int delay = 0;
        public static boolean changed;
        
        /** Stops running */
        public void halt() {
                running = false;
        }

        Random random;
        double smin = 200,s;
        int nearend;
        
        void calc(short[] lin,int off,int len) {
                int i,j;
                double sm = 30000,r;
                
                for (i = 0; i < len; i += 5) {
                        j = lin[i+off];
                        s = 0.03*Math.abs(j) + 0.97*s;
                        if (s < sm) sm = s;
                        if (s > smin) nearend = 3000*mu/5;
                        else if (nearend > 0) nearend--;
                }
                r = (double)len/(100000*mu);
                if (sm > 2*smin || sm < smin/2)
                        smin = sm*r + smin*(1-r);
        }

        void calc1(short[] lin,int off,int len) {
                int i,j;
                
                for (i = 0; i < len; i++) {
                        j = lin[i+off];
                        lin[i+off] = (short)(j>>2);
                }
        }

        void calc2(short[] lin,int off,int len) {
                int i,j;
                
                for (i = 0; i < len; i++) {
                        j = lin[i+off];
                        lin[i+off] = (short)(j>>1);
                }
        }

        void calc10(short[] lin,int off,int len) {
                int i,j;
                
                for (i = 0; i < len; i++) {
                        j = lin[i+off];
                        if (j > 16350)
                                lin[i+off] = 16350<<1;
                        else if (j < -16350)
                                lin[i+off] = -16350<<1;
                        else
                                lin[i+off] = (short)(j<<1);
                }
        }

        void noise(short[] lin,int off,int len,double power) {
                int i,r = (int)(power*2);
                short ran;

                if (r == 0) r = 1;
                for (i = 0; i < len; i += 4) {
                        ran = (short)(random.nextInt(r*2)-r);
                        lin[i+off] = ran;
                        lin[i+off+1] = ran;
                        lin[i+off+2] = ran;
                        lin[i+off+3] = ran;
                }
        }
        
        public static int m;
        int mu;
        
        
        
        /** Runs it in a new Thread. */
        public void run() {
               // WifiManager wm = (WifiManager) Receiver.mContext.getSystemService(Context.WIFI_SERVICE);
                long lastscan = 0,lastsent = 0;

                if (rtp_socket == null)
                        return;
                int seqn = 0;
                long time = 0;
                double p = 0;
               // boolean improve = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getBoolean(Settings.PREF_IMPROVE, Settings.DEFAULT_IMPROVE);
               // boolean selectWifi = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_SELECTWIFI, org.sipdroid.sipua.ui.Settings.DEFAULT_SELECTWIFI);
             //   int micgain = 0;
                long last_tx_time = 0;
                long next_tx_delay;
                long now;
                running = true;
                m = 1;
              //  int dtframesize = 4;
                
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                mu = 1;
                int min = AudioRecord.getMinBufferSize(8000, 
                                AudioFormat.CHANNEL_IN_MONO, 
                                AudioFormat.ENCODING_PCM_16BIT);
                Log.d("RtpStreamSender","min="+min);
                if (min == 640) {
                        if (frame_size == 960) frame_size = 320;
                        if (frame_size == 1024) frame_size = 160;
                        min = 4096*3/2;
                } else if (min < 4096) {
                        if (min <= 2048 && frame_size == 1024) frame_size /= 2;
                        min = 4096*3/2;
                } else if (min == 4096) {
                        min *= 3/2;
                        if (frame_size == 960) frame_size = 320;
                } else {
                        if (frame_size == 960) frame_size = 320;
                        if (frame_size == 1024) frame_size = 160; // frame_size *= 2;
                }
                frame_rate = 8000/frame_size;
                long frame_period = 1000 / frame_rate;
                frame_rate *= 1.5;
                byte[] buffer = new byte[frame_size + 12];
                RtpPacket rtp_packet = new RtpPacket(buffer, 0);
                rtp_packet.setPayloadType(0);
              /*  if (DEBUG)
                        println("Reading blocks of " + buffer.length + " bytes");
                
                println("Sample rate  = " + p_type.codec.samp_rate());
                println("Buffer size = " + min);*/

                AudioRecord record = null;
                
                short[] lin = new short[frame_size*(frame_rate+1)];
                int num,ring = 0,pos;
                random = new Random();
               
               // p_type.codec.init();
                while (running) {
                         if (changed || record == null) {
                                if (record != null) {
                                        record.stop();
                                        record.release();
                                        Boolean samsung = Build.MODEL.contains("SAMSUNG") || Build.MODEL.contains("SPH-") ||
                                                Build.MODEL.contains("SGH-") || Build.MODEL.contains("GT-");
                                        if (samsung) {
                                                AudioManager am = (AudioManager) Receiver.mContext.getSystemService(Context.AUDIO_SERVICE);
                                                am.setMode(AudioManager.MODE_IN_CALL);
                                                am.setMode(AudioManager.MODE_NORMAL);
                                        }
                                }
                                changed = false;
                                record = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 
                                                        min);
                                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                                	Log.d("RtpStreamSender","AudioRecord not initialized");
                                   // Receiver.endCall();
                                    record = null;
                                    break;
                            }
                                record.startRecording();
                         }
                         
                         if (frame_size < 480) {
                             now = System.currentTimeMillis();
                             next_tx_delay = frame_period - (now - last_tx_time);
                             last_tx_time = now;
                             if (next_tx_delay > 0) {
                                     try {
                                             sleep(next_tx_delay);
                                     } catch (InterruptedException e1) {
                                     }
                                     last_tx_time += next_tx_delay-sync_adj;
                             }
                     }
                         pos = (ring+delay*frame_rate*frame_size/2)%(frame_size*(frame_rate+1));
                         num = record.read(lin,pos,frame_size);
                         
                         calc(lin,pos,num);
                         noise(lin,pos,num,p/2);
                         calc10(lin,pos,num);
                         
                         ring += frame_size;
                         rtp_packet.setSequenceNumber(seqn++);
                         rtp_packet.setTimestamp(time);
                         rtp_packet.setPayloadLength(num);
                         now = SystemClock.elapsedRealtime();
                         if (RtpStreamReceiver.timeout == 0 ||  now-lastsent > 500)
                                 try {
                                         lastsent = now;
                                         rtp_socket.send(rtp_packet);
                                         if (m > 1 && RtpStreamReceiver.timeout == 0 ){
                                                 for (int i = 1; i < m; i++)
                                                         rtp_socket.send(rtp_packet);}
                                 } catch (Exception e) {
                                	 e.printStackTrace();
                                	 Log.d("RtpStreamSender",e.toString());
                                 }
                         time += frame_size;
                         }
                if (record != null) {
                    record.stop();
                    record.release();
            }
            m = 0;
            
            rtp_socket.close();
            rtp_socket = null;
                }
        }
