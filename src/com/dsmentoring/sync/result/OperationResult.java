package com.dsmentoring.sync.result;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OperationResult {
	private static Logger log = LogManager.getLogger(OperationResult.class);
	
	public OperationResult(){
		
	}
	
	public void PrintResult(String DN, String ChangeType, int resultCode, String resultMessage){
		if(resultCode == 0){
			log.debug(DN + " Entry " + ChangeType + " Operation Success.");
		}else{
			log.error(DN + " Entry " + ChangeType + " Operation Fail.");
			log.error("--- Result Code : " + resultCode + " / Message : " + resultMessage);
		}
	}
}
