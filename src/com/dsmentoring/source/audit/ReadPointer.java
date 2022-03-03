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

public class ReadPointer {
	private static Logger log = LogManager.getLogger(ReadPointer.class);
	
	String[] Pointer_Read = new String[2];
	
	public ReadPointer(EnvProperties prop) {
		log.debug("Check the applied point just before");
		
		File Last_Point_File = new File(prop.getValues("Audit_Log_Pointer"));
		
		if (Last_Point_File.exists() && Last_Point_File.canRead()) {
			try {
				BufferedReader read = new BufferedReader(new FileReader(Last_Point_File));
				while(true){
					String Line = read.readLine();
					if(null == Line){
						break;
					}
					
					if(Line.toUpperCase().startsWith("#")){
						continue;
					}
					Pointer_Read = Line.split(",");
				}
				
				read.close();
			} catch (Exception ex) {
				log.error("An Error Occurred while Checking the Previous Application Point.");
				log.error(ex.toString());
				log.error("Synchronize All Audit Log Files in the List.");
				initPointers();
			}
		} else {
			log.error("The Pointer File does not Exist or cannot be Read.");
			log.error("Synchronize All Audit Log Files in the List.");
			initPointers();
		}
		log.debug("Last Sync Time(LDAP)	 : " + Pointer_Read[0]);
		log.debug("Last Sync Point(LDAP) : " + Pointer_Read[1]);
		log.info("--------------------------------------------------------------------------------------------");
	}
	
	private void initPointers(){
		Pointer_Read[0] = "00000000000000";
		Pointer_Read[1] = "0";
	}
	
	public String[] GetPointer() {
		return Pointer_Read;
	}
}