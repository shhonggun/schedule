package com.dsmentoring.target.ldap;

//import org.apache.log4j.Logger;
import com.dsmentoring.util.ModuleSynchronizer;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFChangeRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsmentoring.util.EnvProperties;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class SynctoLDAP extends ModuleSynchronizer {
	private static Logger log = LogManager.getLogger(SynctoLDAP.class);
	
	protected int Result = -1;
	protected String ResultMessage = "";

	private static EnvProperties prop = null;
	private static String BASE_DN = null;
	private static String BASE_ATTR = null;
	private static String RDN_ATTR  = null;
	private static String RDN_COLU  = null;
	private static String PWD_USER_ATTR = null;
	private static String PASSWD_COLU  = null;


	private static String Master_DB_Table = null;
	private static String DB_Columns =null;
	private static String DB_WHERE =null;
	private static String Master_DB_Column [] =null;
	private static String LDAP_Attributes  = null;
	private static String Master_LDAP_Attribute[]  = null;

	private static boolean  DelYN = false ;
	private static int total_count = 0;
	private static int target_count = 0;
	private static int not_count = 0;
	private static int skip_count = 0;
	private static int a_succ_count = 0;
	private static int a_err_count = 0;
	private static int m_succ_count = 0;
	private static int m_err_count = 0;
	private static int d_succ_count = 0;
	private static int d_err_count = 0;

	public SynctoLDAP(LDAPConnection ldap_conn,LDIFChangeRecord ChangeRecord){
		try {
			ChangeRecord.processChange(ldap_conn);
			Result = 0;
		} catch (LDAPException LE) {
			Result = LE.getResultCode().intValue();
			ResultMessage = LE.getMessage();
		}
	}

	public SynctoLDAP()
	{
		prop = GetEnv();
		ConnectLDAP();
	}

	public void Sync2LDAP(String DB_Pk) {

		String key;
		String value;

		HashMap<String,String> map = new HashMap<>();

		try {

			BASE_ATTR = prop.getValues("DEFAULT_USER_VALS");
			String [] def_attr = BASE_ATTR.split(",");
			BASE_DN = prop.getValues("BASE_USER_DN");
			RDN_ATTR = prop.getValues("RDN_USER_ATTR");
			RDN_COLU = prop.getValues("RDN_USER_COLU");

			log.debug("RDN "+RDN_ATTR+" / "+RDN_COLU+" / "+BASE_DN);

			Master_DB_Table 	= prop.getValues("Master_DB_Table_User");
			LDAP_Attributes		= prop.getValues("Master_LDAP_USER_Attribute");
			Master_LDAP_Attribute =  LDAP_Attributes.split(",");

			String entry_dn  = RDN_ATTR+"="+DB_Pk;
			entry_dn = entry_dn+","+BASE_DN ;

			Collection<Attribute> add_attrs =  new ArrayList<Attribute>() ;

			log.debug("  IS TARGET ENTRY !");
			SearchResultEntry Ldap_Entry = GetLDAPConnection().getEntry(entry_dn,Master_LDAP_Attribute);
			Collection<Attribute> attribute = Ldap_Entry.getAttributes();

			for(Attribute attr : attribute){
				map.put(attr.getName(),attr.getValue());
			}

		}
		catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		} // catch end
		finally {

		}
	}

	public HashMap GET2LDAP(String DB_Pk) {

		String key;
		String value;

		HashMap<String,String> map = new HashMap<>();

		try {

			BASE_ATTR = prop.getValues("DEFAULT_USER_VALS");
			String [] def_attr = BASE_ATTR.split(",");
			BASE_DN = prop.getValues("BASE_USER_DN");
			RDN_ATTR = prop.getValues("RDN_USER_ATTR");
			RDN_COLU = prop.getValues("RDN_USER_COLU");

			log.debug("RDN "+RDN_ATTR+" / "+RDN_COLU+" / "+BASE_DN);

			Master_DB_Table 	= prop.getValues("Master_DB_Table_User");
			LDAP_Attributes		= prop.getValues("Master_LDAP_USER_Attribute");
			Master_LDAP_Attribute =  LDAP_Attributes.split(",");

			String entry_dn  = RDN_ATTR+"="+DB_Pk;
			entry_dn = entry_dn+","+BASE_DN ;

			Collection<Attribute> add_attrs =  new ArrayList<Attribute>() ;

			log.debug("  IS TARGET ENTRY !");
			SearchResultEntry Ldap_Entry = GetLDAPConnection().getEntry(entry_dn,Master_LDAP_Attribute);
			Collection<Attribute> attribute = Ldap_Entry.getAttributes();

			for(Attribute attr : attribute){
				map.put(attr.getName(),attr.getValue());
			}

		}
		catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		} // catch end
		finally {

		}

		return map;
	}

	
	public int getResultCode(){
		int Tmp_Code = Result;
		Result = -1;
		return Tmp_Code;
	}
	
	public String getResultMessage(){
		String Tmp_Message = ResultMessage;
		ResultMessage = "";
		return Tmp_Message;
	}
}
