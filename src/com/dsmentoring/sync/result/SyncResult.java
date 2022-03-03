package com.dsmentoring.sync.result;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SyncResult {
	private static Logger log = LogManager.getLogger(SyncResult.class);
	
	private int Total_Count = 0;
	private int Target_Count = 0;
	private int Non_Target_Count = 0;
	private int Add_Succ_Count = 0;
	private int Add_Fail_Count = 0;
	private int Mod_Succ_Count = 0;
	private int Mod_Fail_Count = 0;
	private int Del_Succ_Count = 0;
	private int Del_Fail_Count = 0;
	
	public SyncResult(){
	}
	
	public void PrintResults(){
		log.info("===========================================");
		log.info("TOTAL  ENTRY COUNT		: " + Total_Count);
		log.info("TARGET ENTRY COUNT		: " + Target_Count);
		log.info("NON TARGET ENTRY COUNT	: " + Non_Target_Count);
		log.info("-------------------------------------------");
		log.info("TOTAL SUCC  COUNT : " + (Add_Succ_Count + Mod_Succ_Count + Del_Succ_Count));
		log.info("TOTAL FAIL  COUNT : " + (Add_Fail_Count + Mod_Fail_Count + Del_Fail_Count));
		log.info("===========================================");
		log.info("SUCC ADD COUNT : " + Add_Succ_Count);
		log.info("FAIL ADD COUNT : " + Add_Fail_Count);
		log.info("-------------------------------------------");
		log.info("SUCC MOD ENTRY : " + Mod_Succ_Count);
		log.info("FAIL MOD ENTRY : " + Mod_Fail_Count);
		log.info("-------------------------------------------");
		log.info("SUCC DEL ENTRY : " + Del_Succ_Count);
		log.info("FAIL DEL ENTRY : " + Del_Fail_Count);
		log.info("===========================================");
		
		Total_Count = 0;
		Target_Count = 0;
		Non_Target_Count = 0;
		Add_Succ_Count = 0;
		Add_Fail_Count = 0;
		Mod_Succ_Count = 0;
		Mod_Fail_Count = 0;
		Del_Succ_Count = 0;
		Del_Fail_Count = 0;
	}
	
	public void IncreaseTotalCount(){
		Total_Count = Total_Count + 1;
	}
	
	public void IncreaseTargetCount(){
		Target_Count = Target_Count + 1;
	}
	
	public void IncreaseNonTargetCount(){
		Non_Target_Count = Non_Target_Count + 1;
	}
	
	public void IncreaseAddSuccCount(){
		Add_Succ_Count = Add_Succ_Count + 1;
	}
	
	public void IncreaseModSuccCount(){
		Mod_Succ_Count = Mod_Succ_Count + 1;
	}
	
	public void IncreaseDelSuccCount(){
		Del_Succ_Count = Del_Succ_Count + 1;
	}
	
	public void IncreaseAddFailCount(){
		Add_Fail_Count = Add_Fail_Count + 1;
	}
	
	public void IncreaseModFailCount(){
		Mod_Fail_Count = Mod_Fail_Count + 1;
	}
	
	public void IncreaseDelFailCount(){
		Del_Fail_Count = Del_Fail_Count + 1;
	}
}
