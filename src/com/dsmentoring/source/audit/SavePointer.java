package com.dsmentoring.source.audit;

import java.io.*;
import com.dsmentoring.util.EnvProperties;

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

public class SavePointer {
	private static Logger log = LogManager.getLogger(SavePointer.class);
	
	public SavePointer(EnvProperties prop, String Pointer_Date_to_be_Saved, String Entry_Count) {
		try{
			log.debug("============================================================================================");
			log.debug("Save Pointer");
			PrintWriter WritePoint = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prop.getValues("Audit_Log_Pointer")))));
			WritePoint.write(Pointer_Date_to_be_Saved + "," + Entry_Count);
			log.debug("Last Modified Time of Audit Log File : " + Pointer_Date_to_be_Saved);
			log.debug("Entry Count : " + Entry_Count);
			WritePoint.flush();
			WritePoint.close();
//			log.debug("============================================================================================");
		}catch(Exception FE){
			log.error("Failed to save Pointer");
			log.error(FE);
		}
	}
}