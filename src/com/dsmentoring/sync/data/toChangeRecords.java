package com.dsmentoring.sync.data;

import java.io.File;
import java.util.LinkedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFReader;

public class toChangeRecords {
	private static Logger log = LogManager.getLogger(toChangeRecords.class);
	private LinkedList<LDIFChangeRecord> Change_Record = new LinkedList<LDIFChangeRecord>();
	
	public toChangeRecords(File LDIFFile){
		try{
			log.debug("Convert " + LDIFFile.getName() + " File to Change Records.");
			
			LDIFReader ReadLDIF = new LDIFReader(LDIFFile);
			while(true){
				LDIFChangeRecord Tmp_CR = ReadLDIF.readChangeRecord();

				if (null == Tmp_CR){
					break;
				}
				Change_Record.add(Tmp_CR);
			}
			ReadLDIF.close();
			log.debug("Conversion to Change Records was Successful.");
		}catch(Exception E){
			log.error("Failed to Convert to ChangeRecord Format. Exit Program.");
			log.error(E);
			System.exit(1);
		}
		log.debug("--------------------------------------------------------------------------------------------");
	}
	
	public LinkedList<LDIFChangeRecord> getChangeRecords(){
		return Change_Record;
	}
}
