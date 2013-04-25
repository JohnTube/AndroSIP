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
	

	private String username = null;
	private String passwd = null;
	private String realm = null;
	private String uri=null;
	
	private SipProvider sipProvider=null;
	
	
	public RegisterAgent(String user, String pass, String domain){
		this.username=user;
		this.passwd=pass;
		this.realm=domain;
		SipStack.init(null);
        SipStack.debug_level =0;
        //SipStack.log_path = "~/d/log";
        //int expire_time = 1500;
     SipStack.max_retransmission_timeout = SipStack.default_expires;
     SipStack.default_transport_protocols = new String[1];
     SipStack.default_transport_protocols[0] = "udp";
     
        sipProvider = new SipProvider(
                IpAddress.getLocalHostAddress().toString(),0);
	}
	
	public void register(){
		
          	uri="sip:"+username+"@"+realm;
          	
              
              NameAddress toAddress = new NameAddress(
                     uri);
              
              NameAddress fromAddress = new NameAddress(
                      uri);
              NameAddress contactAddress = new NameAddress(
              		"sip:"+username+"@"+sipProvider.getViaAddress()); //+":"+sipProvider.getPort()
              
              SipURL sipURL = new SipURL("sip:"+realm);
                           
              Message rMsg = MessageFactory.createRegisterRequest(
                      sipProvider, 
                      sipURL,
                      toAddress, 
                      fromAddress, 
                      contactAddress);
              
              rMsg.setExpiresHeader(new ExpiresHeader(SipStack.default_expires));
             // Log.d("MYSIP","rMsg="+rMsg.toString());
              TransactionClient tC;
              tC = new TransactionClient(sipProvider,rMsg,this);
              tC.request();
	}

	@Override
	public void onTransFailureResponse(TransactionClient transClt, Message resp) {
		// TODO complete other 4xx responses
		int code = resp.getStatusLine().getCode();
		Log.d("MYSIP", "AltResp code="+code+" reason="+resp.getStatusLine().getReason());
		Log.d("MYSIP","resp="+resp.toString());
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
                            ah, null, passwd);
                    
                    String response = x.getResponse();
                                   
                    ah.addResponseParam(response);
            
                    temp.setAuthorizationHeader(ah);
                                    
                    TransactionClient t = new TransactionClient(sipProvider,temp,this);
                    t.request();
            }
			 
           
          }
if (code==407){  
			Log.d("MYSIP","proxy autho");
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
			uri="sip:"+username+"@"+realm;
      	
        
        NameAddress toAddress = new NameAddress(
               uri);
        
        NameAddress fromAddress = new NameAddress(
                uri);
        NameAddress contactAddress = new NameAddress(
          		"sip:"+username+"@"+sipProvider.getViaAddress());
        
        SipURL sipURL = new SipURL("sip:"+realm);
                     
       
        Message rMsg;
        
        rMsg = MessageFactory.createRegisterRequest(
                sipProvider, 
                sipURL,
                toAddress, 
                fromAddress, 
                contactAddress);
        
        rMsg.setExpiresHeader(new ExpiresHeader(0));
      //  Log.d("MYSIP","rMsg="+rMsg.toString());
        TransactionClient tC = new TransactionClient(sipProvider,rMsg,this);
        tC.request();
		}
		
	}
	
	
	@Override
	public void onTransProvisionalResponse(TransactionClient arg0, Message arg1) {
		
		Log.d("MYSIP", "Tryin");
			status = REGISTERING;
	}

	@Override
	public void onTransSuccessResponse(TransactionClient arg0, Message arg1) {
				status = REGISTERED;
	}

	@Override
	public void onTransTimeout(TransactionClient arg0) {
		// TODO Auto-generated method stub
		Log.d("MYSIP", "RegistrationTimeOut");
		status = UNREGISTERED;
		reason = "TimeOut";
		
	}



}
