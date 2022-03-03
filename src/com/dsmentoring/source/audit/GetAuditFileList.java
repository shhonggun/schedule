package com.dsmentoring.source.audit;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


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

public class GetAuditFileList {
	private static Logger log      = LogManager.getLogger(GetAuditFileList.class);
	protected List<File>	_logList = new ArrayList<File>();

	public GetAuditFileList(EnvProperties prop,String current_pointer_Date) {
		
		log.info("Checking the List of Audit Log Files");
		File[] TmpLogList = null;
		List<File> AuditLogList = new ArrayList<File>();
		
		final String Audit_File_Start_With = prop.getValues("Audit_File_Start_With");
		String Audit_Log_Directory = prop.getValues("Audit_Log_Directory");
		
		File File_in_Path = new File(Audit_Log_Directory);
		log.info("The Path to the Audit Log File is a " + Audit_Log_Directory);
		
		TmpLogList = File_in_Path.listFiles();
		
		for(File AllFile : TmpLogList){

			if (AllFile.getName().equalsIgnoreCase("audit_orignal")){
				continue;
			}


			if(AllFile.isFile()){
				String Pattern = "yyyyMMddHHmmssSSS";
				SimpleDateFormat DateFormat = new SimpleDateFormat(Pattern);
				
				Date Last_Modify = new Date(AllFile.lastModified());
				String File_Last_Mod_Date = DateFormat.format(Last_Modify);
				
				long Pointer_Value = Long.parseLong(current_pointer_Date);
				long File_Value = Long.parseLong(File_Last_Mod_Date);
				
				if(File_Value >= Pointer_Value){
					log.debug(AllFile + " is the Target File");
					String File_Name = AllFile.getName();
					if(File_Name.toLowerCase().startsWith(Audit_File_Start_With.toLowerCase())){
						AuditLogList.add(AllFile);
					}
				}else{
					log.debug(AllFile + " is not a Target File. The Last Modified Date of the File is less than the Value of Pointer.");
				}
			}else{
				log.trace(AllFile.getName() + " is Not a File.");
			}
		}
		
		if(null == AuditLogList || AuditLogList.isEmpty()){
			log.info("Audit Log File does not Exist.");
		}else{
			log.info("The Number of Audit Log Files Collected is " + AuditLogList.size());
			Collections.sort(AuditLogList);
		}
		
		log.debug("List of Audit Log Files");
		if(!(AuditLogList.size() == 0)){
			for (int Tmp_Num = 1; Tmp_Num < AuditLogList.size(); Tmp_Num++) {
				if(!AuditLogList.get(Tmp_Num).canRead()){
					log.error(AuditLogList.get(Tmp_Num) + " Audit Log File Cannot be Read. File is Skipped");
					continue;
				}
				_logList.add(AuditLogList.get(Tmp_Num));
				log.debug(Tmp_Num + " - " + AuditLogList.get(Tmp_Num));
			}
			_logList.add(AuditLogList.get(0));
			log.info(AuditLogList.size() + " - " + AuditLogList.get(0));
		}else{
			log.error("Audit Log Files do not Exist. Check Properties File or Audit Log File Path and Name. Exit Program");
			System.exit(1);
		}
		log.info("--------------------------------------------------------------------------------------------");
	}
	
	public List<File> GetTargetAuditList() {
		return _logList;
	}
}