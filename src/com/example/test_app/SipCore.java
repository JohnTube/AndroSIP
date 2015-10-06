package com.example.test_app;

import org.zoolu.net.IpAddress;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.ui.LoginActivity;

public class SipCore  extends Service {
	private RegisterAgent rA;
	//private SipStack sipStack;
	private SipProvider sipProvider;
	public UA ua;
	private String username = null;
	private String passwd = null;
	private String realm = null;
	private String contact_url = null;
	private String from_url = null;
	
	public SipCore(){
		Log.d("SipCore","SipCore()");
		SipStack.init(null);
        SipStack.debug_level =0;
        SipStack.max_retransmission_timeout = SipStack.default_expires;
        SipStack.default_transport_protocols = new String[1];
        SipStack.default_transport_protocols[0] = "udp";
        SipStack.ua_info="AndroSIP";
	}
	
	public void listen(){
		if (ua!=null && sipProvider!=null)
		ua.UAS();
	}
	
	public void acceptCall(){
		Log.d("SipCore","accepting call");
		ua.accept();
	}
	
	public void rejectCall(){
		ua.reject();
	}
	
	public void call(NameAddress callee){
		ua.UAC(callee);
	}
	
	
	public SipCore(Context context){
		Log.d("SipCore","SipCore(context)");
		SipStack.init(null);
        SipStack.debug_level =0;
        SipStack.max_retransmission_timeout = SipStack.default_expires;
        SipStack.default_transport_protocols = new String[1];
        SipStack.default_transport_protocols[0] = "udp";
        SipStack.ua_info="AndroSIP";
	}
	
	public void init(Context context){
		Log.d("SipCore","init sipCore(context)");
		 sipProvider = new SipProvider(
	                IpAddress.getLocalHostAddress().toString(),0);
	        if (sipProvider==null) Log.d("SipCore","sipProvider=null");
	        //this.ua=new UA();
	        getCredentials(context);
	        String uri="sip:"+username+"@"+realm;
	        //this.rA=new RegisterAgent(username,passwd,realm);
	        this.rA= new RegisterAgent(sipProvider, new SipURL("sip:"+realm), new NameAddress(uri), 
	        		new NameAddress(uri),username,realm,passwd);
	       this.ua=new UA(sipProvider,username,passwd,realm);
	}
	
	public void init(String user,String password,String domain){
		Log.d("SipCore","init(u,p,d)");
		 sipProvider = new SipProvider(
	                IpAddress.getLocalHostAddress().toString(),0);
	        if (sipProvider==null) Log.d("SipCore","sipProvider=null");
	        //this.ua=new UA();
	        //getCredentials(context);
	        setCredentials(user,password,domain);
	        String uri="sip:"+user+"@"+domain;
	        this.rA= new RegisterAgent(sipProvider, new SipURL("sip:"+realm), new NameAddress(uri), 
	        		new NameAddress(uri),user,domain,password);
	        this.ua=new UA(sipProvider,username,realm,passwd);
	       // this.rA= new RegisterAgent(user,domain,password);
	}
	
	public void setCredentials(String user,String password,String domain){
		Log.d("SipCore","setting credentials");
		this.username=user;
		this.passwd=password;
		this.realm=domain;
	}
	
	private void getCredentials(Context context){
		Log.d("SipCore","getting credentials");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			this.username=pref.getString(LoginActivity.PREF_USERNAME, null);
			Log.d("SipCore","username="+username);
			this.realm=pref.getString(LoginActivity.PREF_DOMAIN, null);
			Log.d("SipCore","realm="+realm);
			this.passwd=pref.getString(LoginActivity.PREF_PASSWORD, null);
			Log.d("SipCore","pass="+passwd);
	}
	
    private void init_addresses() {
        if (realm == null && contact_url != null)
                realm = new NameAddress(contact_url).getAddress().getHost();
        if (username == null)
                username = (contact_url != null) ? new NameAddress(contact_url)
                                .getAddress().getUserName() : "user";
}
	
    /**
     * Sets contact_url and from_url with transport information. <p/> This
     * method actually sets contact_url and from_url only if they haven't still
     * been explicitly initialized.
     */
    public void initContactAddress(SipProvider sip_provider) { // contact_url
            if (contact_url == null) {
                    contact_url = "sip:" + username + "@"
                                    + sip_provider.getViaAddress();
                    if (sip_provider.getPort() != SipStack.default_port)
                            contact_url += ":" + sip_provider.getPort();
                    if (!sip_provider.getDefaultTransport().equals(
                                    SipProvider.PROTO_UDP))
                            contact_url += ";transport="
                                            + sip_provider.getDefaultTransport();
            }
            // from_url
            if (from_url == null)
                    from_url = contact_url;
    }
	
	public boolean register(){
		Log.d("SipCore","registering");
		rA.register();
		return (rA.status==RegisterAgent.REGISTERED);
	}

	public boolean isRegistered()
	{return rA.status==RegisterAgent.REGISTERED;}
	
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
	}
	@Override 
	public void onDestroy(){
		super.onDestroy();
		Log.d("SipService","onDestroy()");
	}
}
