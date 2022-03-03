package com.dsmentoring.sync.result;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFWriter;

public class SaveErrorDatatoLDIF {
	private static Logger log = LogManager.getLogger(SaveErrorDatatoLDIF.class);

	private static String File_Path = null;
	private LDIFWriter WriteLDIF = null;
	
	public SaveErrorDatatoLDIF(String Path){
		File_Path = Path;
		
		try{
			File Check_File = new File(File_Path);
			if(Check_File.exists()){
				Date CurrentDate = new Date();
				String Pattern = "yyyyMMddHHmmssSSS";
				SimpleDateFormat DateFormat = new SimpleDateFormat(Pattern);
				String File_Date = DateFormat.format(CurrentDate);
				
				String Rename_Path = File_Path + "_" + File_Date;
				File Rename_File = new File(Rename_Path);
				Check_File.renameTo(Rename_File);
				
				WriteLDIF = new LDIFWriter(File_Path);
			}else{
				WriteLDIF = new LDIFWriter(File_Path);
			}
			log.debug("Entry with an Error is Written to " + File_Path + ".");
		}catch(Exception E){
			log.debug("It is not Possible to Create an LDIF File that Records an Error Entry");
			log.debug(E);
		}
	}
	
	public void WriteLDIFFile(LDIFChangeRecord ChangeRecord,String ResultMessage){
		try{
			WriteLDIF.writeLDIFRecord(ChangeRecord,ResultMessage);
			WriteLDIF.flush();
		}catch(Exception E){
			log.debug("Failed to Write Error Data.");
			log.debug(E);
		}
	}
	
	public void CloseLDIFWriter(){
		try{
			if(null != WriteLDIF){
				WriteLDIF.close();
			}
			log.debug("LDIF Writer has been Terminated Normally.");
		}catch(Exception E){
			log.debug("LDIFWriter could not be Closed.");
			log.debug(E);
		}
	}
}
