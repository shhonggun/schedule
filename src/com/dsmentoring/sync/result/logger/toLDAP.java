package com.dsmentoring.sync.result.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unboundid.ldif.LDIFChangeRecord;

public class toLDAP {
	private static Logger log = LogManager.getLogger(toLDAP.class);
	
	private int Total_Count = 0;
	private int Target_Count = 0;
	private int Non_Target_Count = 0;
	private int Add_Succ_Count = 0;
	private int Add_Fail_Count = 0;
	private int Mod_Succ_Count = 0;
	private int Mod_Fail_Count = 0;
	private int Del_Succ_Count = 0;
	private int Del_Fail_Count = 0;
	private int Skip_Count = 0;
	
	private boolean SaveErrorEntryYN = false;
	private boolean LDAPConnectYN = true;
	
	public toLDAP(){
		
	}
	
	public void PrintOpResult(LDIFChangeRecord Origin_Record, int resultCode, String resultMessage){
		String DN = Origin_Record.getDN();
		String ChangeType = Origin_Record.getChangeType().toString();
		
		if(resultCode == 0){
			log.debug("[SUCC] " + DN + " Entry " + ChangeType + " Operation Success.");
			if(ChangeType.equalsIgnoreCase("Add")){
				Add_Succ_Count++;
			}else if(ChangeType.equalsIgnoreCase("Modify")){
				Mod_Succ_Count++;
			}else if(ChangeType.equalsIgnoreCase("Delete")){
				Del_Succ_Count++;
			}
		}else if(resultCode == 81 || resultCode == 91){
			log.error("[ERR] LDAP Connection has been Closed. After Saving the Pointer, Stop the Program.");
			LDAPConnectYN = false;
		}else{
			log.error("[FAIL] " + DN + " Entry " + ChangeType + " Operation Fail.");
			
			if(ChangeType.equalsIgnoreCase("Add")){
				if(resultCode == 68){
					Skip_Count++;
					log.error(" --- " + DN + " Entry Already Exist");
				}else{
					Add_Fail_Count++;
					log.error("--- Result Code : " + resultCode + " / Message : " + resultMessage);
					SaveErrorEntryYN = true;
				}
			}else if(ChangeType.equalsIgnoreCase("Modify")){
				if(resultCode == 16){
					Skip_Count++;
					log.error(" --- " + DN + " Entry does not Include the Target Attribute of the Request.");
				}else{
					Mod_Fail_Count++;
					log.error("--- Result Code : " + resultCode + " / Message : " + resultMessage);
					SaveErrorEntryYN = true;
				}
			}else if(ChangeType.equalsIgnoreCase("Delete")){
				if(resultCode == 32){
					Skip_Count++;
					log.error(" --- " + DN + " Entry is not Exist in LDAP");
				}else{
					Del_Fail_Count++;
					log.error("--- Result Code : " + resultCode + " / Message : " + resultMessage);
					SaveErrorEntryYN = true;
				}
			}
		}
	}
	
	public void PrintSyncResults(){
		log.info("===========================================");
		log.info("TOTAL  ENTRY COUNT		: " + Total_Count);
		log.info("TARGET ENTRY COUNT		: " + Target_Count);
		log.info("NON TARGET ENTRY COUNT	: " + Non_Target_Count);
		log.info("-------------------------------------------");
		log.info("TOTAL SUCC  COUNT : " + (Add_Succ_Count + Mod_Succ_Count + Del_Succ_Count));
		log.info("TOTAL FAIL  COUNT : " + (Add_Fail_Count + Mod_Fail_Count + Del_Fail_Count));
		log.info("SKIP        COUNT : " + Skip_Count);
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
	}
	
//	public void initCount(){
//		Total_Count = 0;
//		Target_Count = 0;
//		Non_Target_Count = 0;
//		Add_Succ_Count = 0;
//		Add_Fail_Count = 0;
//		Mod_Succ_Count = 0;
//		Mod_Fail_Count = 0;
//		Del_Succ_Count = 0;
//		Del_Fail_Count = 0;
//	}
	
	public void CountTarget(boolean TargetYN){
		Total_Count++;
		if(TargetYN){
			Target_Count++;
		}else{
			Non_Target_Count++;
		}
	}
	
	public boolean SaveErrorEntryYN(){
		boolean Result = SaveErrorEntryYN;
		SaveErrorEntryYN = false;
		return Result;
	}
	
	public boolean LDAPConnectYN(){
		boolean Result = LDAPConnectYN;
		LDAPConnectYN = true;
		return Result;
	}
}
