package com.example.test_app;


import org.zoolu.net.IpAddress;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.authentication.DigestAuthentication;
import org.zoolu.sip.header.AuthorizationHeader;
import org.zoolu.sip.header.ExpiresHeader;
import org.zoolu.sip.header.ViaHeader;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.MessageFactory;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.sip.transaction.TransactionClient;
import org.zoolu.sip.transaction.TransactionClientListener;


import android.util.Log;

public class RegisterAgent implements TransactionClientListener {
	public static final int UNREGISTERED = 1;
	public static final int REGISTERING = 2;
	public static final int REGISTERED = 3;
	
	public int status;
	public String reason;
	

	private NameAddress toAddress = null;
	private NameAddress fromAddress = null;
	private NameAddress contactAddress = null;
	private SipURL sipURL = null;
	private String uri = null;
	private String password = null;
	private String username = null;
	private String realm = null;
	private SipProvider sipProvider=null;
	
	public RegisterAgent(SipProvider sip_provider, SipURL registrar, NameAddress target_url, NameAddress from_url, String username, String realm, String passwd){
		this.username=username;
        this.password=passwd;
        this.realm=realm;
        this.toAddress = target_url;
        this.sipProvider=sip_provider;
        uri="sip:"+username+"@"+realm;
        this.fromAddress = from_url;
        this.contactAddress = new NameAddress(
        		"sip:"+username+"@"+sipProvider.getViaAddress()); //+":"+sipProvider.getPort()
        		
        this.sipURL = registrar;
	}
	
	public RegisterAgent(String username, String password, String realm){
				uri="sip:"+username+"@"+realm;
				SipStack.init(null);
		        SipStack.debug_level =0;
		        //SipStack.log_path = "~/d/log";
		        //int expire_time = 1500;
		     SipStack.max_retransmission_timeout = SipStack.default_expires;
		     SipStack.default_transport_protocols = new String[1];
		     SipStack.default_transport_protocols[0] = "udp";
		     
		        sipProvider = new SipProvider(
		                IpAddress.getLocalHostAddress().toString(),0);  	
        this.username=username;
        this.password=password;
        this.realm=realm;
        this.toAddress = new NameAddress(
               uri);
        
        this.fromAddress = new NameAddress(
                uri);
        this.contactAddress = new NameAddress(
        		"sip:"+username+"@"+sipProvider.getViaAddress()); //+":"+sipProvider.getPort()
        
        this.sipURL = new SipURL("sip:"+realm);

    Log.d("ch3ar","IPv4="+IpAddress.getLocalHostAddress().toString());
	}
	
	public void register(){
<<<<<<< HEAD
		
          	uri="sip:"+username+"@"+realm;
          	
              NameAddress toAddress = new NameAddress(
                     uri);
              
              NameAddress fromAddress = new NameAddress(
                      uri);
              NameAddress contactAddress = new NameAddress(
              		"sip:"+username+"@"+sipProvider.getViaAddress()); //+":"+sipProvider.getPort()
              
              SipURL sipURL = new SipURL("sip:"+realm);
=======
			if (sipProvider==null)
				Log.d("RegisterAgent","sipProvider=null");
			else if (sipURL==null)
				Log.d("RegisterAgent","sipURL=null");
			else if (toAddress==null)
				Log.d("RegisterAgent","toAddress=null");
			else if (fromAddress==null)
				Log.d("RegisterAgent","fromAddress=null");
			else if (contactAddress==null)
			Log.d("RegisterAgent","contactAddress=null");
>>>>>>> branch 'master' of https://github.com/JohnTube/PCD_test.git
                           
              Message rMsg = MessageFactory.createRegisterRequest(
                      sipProvider, 
                      sipURL,
                      toAddress, 
                      fromAddress, 
                      contactAddress);
             
              rMsg.setExpiresHeader(new ExpiresHeader(SipStack.default_expires));
            Log.d("ch3ar","rMsg="+rMsg.toString());
              TransactionClient tC;
              tC = new TransactionClient(sipProvider,rMsg,this);
              tC.request();
	}

	@Override
	public void onTransFailureResponse(TransactionClient transClt, Message resp) {
		// TODO complete other 4xx responses
		int code = resp.getStatusLine().getCode();
		Log.d("ch3ar", "AltResp code="+code+" reason="+resp.getStatusLine().getReason());
		
		if (code==401){  
			
			if (resp.hasWwwAuthenticateHeader())
            {	         
		            Message temp = transClt.getRequestMessage();
		                    
                    String nonce = resp.getWwwAuthenticateHeader().getNonceParam();
                    realm = resp.getWwwAuthenticateHeader().getRealmParam();
                 
                    AuthorizationHeader ah = new AuthorizationHeader("Digest");
                    temp.setCSeqHeader(temp.getCSeqHeader().incSequenceNumber());
                    ViaHeader vh=temp.getViaHeader();
                    String newbranch = SipProvider.pickBranch();
                    vh.setBranch(newbranch);
                    temp.removeViaHeader();
                    temp.addViaHeader(vh);
                                
                    ah.addUsernameParam(username);
                    ah.addAlgorithParam("MD5");
                    ah.addRealmParam(realm);
                    ah.addNonceParam(nonce);
                    ah.addUriParam(uri);
                  
                    DigestAuthentication x=new DigestAuthentication(resp.getTransactionMethod(),
                            ah, null, password);
                    
                    String response = x.getResponse();
                                   
                    ah.addResponseParam(response);
            
                    temp.setAuthorizationHeader(ah);
                                    
                    TransactionClient t = new TransactionClient(sipProvider,temp,this);
                    t.request();
            }
			 
           
          }
if (code==407){  
			Log.d("ch3ar","proxy autho");
				          }
if (code==403){
			Log.d("","");
			}
if (code==403){
	Log.d("","");
	}

	}
	
		public void deregister(){
			/************************************************************************
			 * Alternatively, when your Phone gets killed, it can as well send a
		     * Deregister request as part of the close up tasks.
		     * For DEREGISTER Request, Contains the following header field/values.
		     * Expires: 0 
		     * that's the trick
			 ************************************************************************/
		if (status==REGISTERED) {
  
        Message rMsg;
        
        rMsg = MessageFactory.createRegisterRequest(
                sipProvider, 
                sipURL,
                toAddress, 
                fromAddress, 
                contactAddress);
        
        rMsg.setExpiresHeader(new ExpiresHeader(0));
      //  Log.d("ch3ar","rMsg="+rMsg.toString());
        TransactionClient tC = new TransactionClient(sipProvider,rMsg,this);
        tC.request();
		}
		
	}
	
	
	@Override
	public void onTransProvisionalResponse(TransactionClient arg0, Message arg1) {
		
		Log.d("ch3ar", "Tryin");
			status = REGISTERING;
	}

	@Override
	public void onTransSuccessResponse(TransactionClient arg0, Message arg1) {
				status = REGISTERED;
	}

	@Override
	public void onTransTimeout(TransactionClient arg0) {
		// TODO Auto-generated method stub
		Log.d("ch3ar", "RegistrationTimeOut");
		status = UNREGISTERED;
		reason = "TimeOut";
		
	}



}
