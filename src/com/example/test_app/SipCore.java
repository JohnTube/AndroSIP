package com.example.test_app;

import org.zoolu.net.IpAddress;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import com.example.ui.LoginActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SipCore {
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
		SipStack.init(null);
        SipStack.debug_level =0;
        //SipStack.log_path = "~/d/log";
        //int expire_time = 1500;
        SipStack.max_retransmission_timeout = SipStack.default_expires;
        SipStack.default_transport_protocols = new String[1];
        SipStack.default_transport_protocols[0] = "udp";
	}
	
	public void listen(){
		ua = new UA();
		ua.UAS(sipProvider);
	}
	
	public SipCore(Context context){
		SipStack.init(null);
        SipStack.debug_level =0;
        //SipStack.log_path = "~/d/log";
        //int expire_time = 1500;
        SipStack.max_retransmission_timeout = SipStack.default_expires;
        SipStack.default_transport_protocols = new String[1];
        SipStack.default_transport_protocols[0] = "udp";
		 sipProvider = new SipProvider(
	                IpAddress.getLocalHostAddress().toString(),0);
	        if (sipProvider==null) Log.d("SipCore","sipProvider=null");
	        //this.ua=new UA();
	        getCredentials(context);
	        String uri="sip:"+username+"@"+realm;
	        this.rA= new RegisterAgent(sipProvider, new SipURL("sip:"+realm), new NameAddress(uri), 
	        		new NameAddress(uri),username,realm,passwd);
	}
	
	public void init(Context context){
		 sipProvider = new SipProvider(
	                IpAddress.getLocalHostAddress().toString(),0);
	        if (sipProvider==null) Log.d("SipCore","sipProvider=null");
	        //this.ua=new UA();
	        getCredentials(context);
	        String uri="sip:"+username+"@"+realm;
	        this.rA= new RegisterAgent(sipProvider, new SipURL("sip:"+realm), new NameAddress(uri), 
	        		new NameAddress(uri),username,realm,passwd);
	}
	
	public void init(String user,String password,String domain){
		 sipProvider = new SipProvider(
	                IpAddress.getLocalHostAddress().toString(),0);
	        if (sipProvider==null) Log.d("SipCore","sipProvider=null");
	        //this.ua=new UA();
	        //getCredentials(context);
	        String uri="sip:"+user+"@"+domain;
	        this.rA= new RegisterAgent(sipProvider, new SipURL("sip:"+realm), new NameAddress(uri), 
	        		new NameAddress(uri),user,domain,password);
	}
	
	public void setCredentials(String user,String password,String domain){
		Log.d("SipCore","setting credentials");
		this.username=user;
		this.passwd=password;
		this.realm=domain;
	}
	
	private void getCredentials(Context context){
		/*SharedPreferences pref = context.getSharedPreferences(LoginActivity.ACCOUNT_PREFS_NAME,android.content.Context.MODE_PRIVATE);
		if (pref.getBoolean(LoginActivity.PREF_REGISTERED_ONCE,false)||
				context.getClass().getSimpleName()==LoginActivity.class.getSimpleName())
		{
			this.username=pref.getString(LoginActivity.PREF_USERNAME, null);
			this.realm=pref.getString(LoginActivity.PREF_DOMAIN, null);
			this.passwd=pref.getString(LoginActivity.PREF_PASSWORD, null);
		}*/
		username="test";
		realm="192.168.1.4";
		passwd="test";
		
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
}
