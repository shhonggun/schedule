package com.dsmentoring.target.ldap;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import com.dsmentoring.util.EnvProperties;
import javax.net.ssl.SSLSocketFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @Title		Module to Synchronize Audit Log Data to LDAP
 * @CreateDate	2020-12-10
 * @UpdateDate	2020-12-10
 * @EMail		tech@dsmentoring.com
 * @Version		1.0
 * @Description	Module to Synchronize Audit Log Data to LDAP
 * @Author		Hyun Woo Kim
 * @Copyright	Copyright (c) 2020 DSMentoring Co., Ltd. All rights reserved.
 * @Company		DSMentoring Co., Ltd.
 */

public class LDAPConnector {
	private static Logger log = LogManager.getLogger(LDAPConnector.class);
	
	protected LDAPConnection _ldapConn	= null;

	public LDAPConnector(EnvProperties prop) {
		log.debug("LDAP_Admin : "+ prop.getValues("LDAP_AdminDN"));
//		log.debug("LDAP_Admin_PW : " + prop.getValues("LDAP_AdminPasswd"));
		
		String LDAPS_Enable = prop.getValues("LDAPS_Enable");
		
		if(LDAPS_Enable.equalsIgnoreCase("true")){
			log.debug("Attempts SSL Communication to LDAP.");
			try {
				SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
			    SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();
			    
			    _ldapConn = new LDAPConnection(sslSocketFactory);
			    _ldapConn.connect(prop.getValues("LDAP_Server_Primary_IP"), Integer.parseInt(prop.getValues("LDAP_Server_Primary_Port")));
			    _ldapConn.bind(prop.getValues("LDAP_AdminDN"), prop.getValues("LDAP_AdminPasswd"));
			    
				log.info("LDAPS Connection Creation Successful. // " + prop.getValues("LDAP_Server_Primary_IP") + ":" + prop.getValues("LDAP_Server_Primary_Port"));
			} catch (Exception ldapAuthEx) {
				log.trace(ldapAuthEx);
				log.error("Primary LDAP Server is Not Connected.  Try Connect to Secondary LDAP.");
				
				try {
					SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
				    SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();
				    
					_ldapConn = new LDAPConnection(sslSocketFactory);
				    _ldapConn.connect(prop.getValues("LDAP_Server_Primary_IP"), Integer.parseInt(prop.getValues("LDAP_Server_Primary_Port")));
				    _ldapConn.bind(prop.getValues("LDAP_AdminDN"), prop.getValues("LDAP_AdminPasswd"));
				    
					log.info("LDAPS Connection Creation Successful. // " + prop.getValues("LDAP_Server_Secondary_IP") + ":" + prop.getValues("LDAP_Server_Secondary_Port"));
				} catch (Exception ldapAuthEx2) {
					log.trace(ldapAuthEx2);
					log.error("Secondary LDAP Server is Not Connected.  Check the Both LDAP Server Status or Connection Info or NetWork.");
					log.error("Exit Program.");
					log.error("============================================================================================");
					System.exit(1);
				}
			}
			
		}else if(LDAPS_Enable.equalsIgnoreCase("false")){
			log.debug("Attempts Plaintext Communication to LDAP.");
			try {
				_ldapConn = new LDAPConnection(prop.getValues("LDAP_Server_Primary_IP"),
								Integer.parseInt(prop.getValues("LDAP_Server_Primary_Port")),
								prop.getValues("LDAP_AdminDN"),
								prop.getValues("LDAP_AdminPasswd"));
				
				log.info("LDAP Connection Creation Successful. // " + prop.getValues("LDAP_Server_Primary_IP") + ":" + prop.getValues("LDAP_Server_Primary_Port"));
			} catch (Exception ldapAuthEx) {
				log.trace(ldapAuthEx);
				log.error("Primary LDAP Server is Not Connected.  Try Connect to Secondary LDAP.");
				
				try {
					_ldapConn = new LDAPConnection(prop.getValues("LDAP_Server_Secondary_IP"),
									Integer.parseInt(prop.getValues("LDAP_Server_Secondary_Port")),
									(prop.getValues("LDAP_AdminDN")),
									(prop.getValues("LDAP_AdminPasswd")));
					
					log.info("LDAP Connection Creation Successful. // " + prop.getValues("LDAP_Server_Secondary_IP") + ":" + prop.getValues("LDAP_Server_Secondary_Port"));
				} catch (Exception ldapAuthEx2) {
					log.trace(ldapAuthEx2);
					log.error("Secondary LDAP Server is Not Connected.  Check the Both LDAP Server Status or Connection Info or NetWork.");
					log.error("Exit Program.");
					log.error("============================================================================================");
					System.exit(1);
				}
			}
		}else{
			log.error("The Value of the Configuration item is Invalid! Check items : ");
			log.error("Exit Program.");
			log.error("============================================================================================");
			System.exit(1);
		}
//		log.info("============================================================================================");
	}

	public LDAPConnection GetLDAPConnection() {
		return _ldapConn;
	}
	
	public void LDAPDisconnect(){
		_ldapConn.close();
		log.debug("The LDAP Connection has been Terminated.");
	}
}
