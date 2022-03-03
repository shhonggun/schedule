package com.dsmentoring.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

import com.dsmentoring.source.audit.GetAuditFileList;
import com.dsmentoring.source.audit.ReadPointer;
import com.dsmentoring.source.audit.SavePointer;
import com.dsmentoring.sync.data.toChangeRecords;
import com.dsmentoring.sync.result.SaveErrorDatatoLDIF;
import com.dsmentoring.sync.result.logger.toLDAP;
import com.dsmentoring.sync.data.EditChangeRecords;
import com.dsmentoring.target.ldap.LDAPConnector;
import com.dsmentoring.target.ldap.SynctoLDAP;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.ChangeLogEntry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFChangeRecord;

/**
 * @Title Module to Synchronize Audit Log Data to LDAP
 * @CreateDate 2020-12-10
 * @UpdateDate 2020-12-10
 * @EMail tech@dsmentoring.com
 * @Version 1.0
 * @Description Module to Synchronize Audit Log Data to LDAP
 * @Author Hyun Woo Kim
 * @Copyright Copyright (c) 2020 DSMentoring Co., Ltd. All rights reserved.
 * @Company DSMentoring Co., Ltd.
 */

public class ModuleSynchronizer {
//	private static Logger log = Logger.getLogger(ModuleSynchronizer.class);
	
	protected EnvProperties prop = null;
	protected LDAPConnector LDAP_Conn = null;
	protected ReadPointer Pointer = null;
	
	protected String[] Curr_Pointer = null;
	
	protected SavePointer Save_Pointer = null;
	protected GetAuditFileList Target_Audit_List = null;
	protected toChangeRecords Trans_ChangeRecord = null;
	protected EditChangeRecords Edit_ChangeRecord = null;
	
	protected SynctoLDAP Sync_to_LDAP = null;
	
	protected toLDAP Logger_to_LDAP = null;
	protected SaveErrorDatatoLDIF Err_Data_to_LDIF = null; 
	
	public ModuleSynchronizer() {
		prop = new EnvProperties();
		Logger_to_LDAP = new toLDAP();
	}
	
	protected EnvProperties GetEnv() {
		return prop;
	}
	
	// Data Control - Function //
	protected LinkedList<LDIFChangeRecord> TransformChangeRecord(File LDIFFile){ 
		Trans_ChangeRecord = new toChangeRecords(LDIFFile);
		return Trans_ChangeRecord.getChangeRecords();
	}
	
	protected void EditChangeRecord(LDIFChangeRecord ChangeRecord) {
		Edit_ChangeRecord = new EditChangeRecords(prop, ChangeRecord);
	}
	
	protected boolean CheckTargetYN(LDIFChangeRecord ChangeRecord){
		return Edit_ChangeRecord.CheckTargetYN(ChangeRecord);
	}
	
	protected void RecordToEntry(long ChangeNumber,LDIFChangeRecord ChangeRecord){
		Edit_ChangeRecord.RecordToEntry(ChangeNumber,ChangeRecord);
	}
	
	protected ChangeLogEntry getChangeLogEntry(){
		return Edit_ChangeRecord.getEntry();
	}
	
	protected List<Attribute> getEntryAttributeSet(){
		return Edit_ChangeRecord.getEntryAttributeSet();
	}
	
	protected List<Modification> getEntryModificationSet(){
		return Edit_ChangeRecord.getEntryModificationSet();
	}
	
	protected LDIFChangeRecord EditParentDN(LDIFChangeRecord ChangeRecord){
		return Edit_ChangeRecord.EditParentDN(ChangeRecord);
	}
	
	protected LDIFChangeRecord ExcludeAttribute(LDIFChangeRecord ChangeRecord){
		return Edit_ChangeRecord.ExcludeAttribute(ChangeRecord);
	}
	
	protected LDIFChangeRecord RenameAttribute(LDIFChangeRecord ChangeRecord){
		return Edit_ChangeRecord.RenameAttribute(ChangeRecord);
	}
	
	protected LDIFChangeRecord AddAttribute(LDIFChangeRecord ChangeRecord,String ChangeType){
		return Edit_ChangeRecord.AddAttribute(ChangeRecord,ChangeType);
	}
	// Data Control - Function //
	
	// Source : Audit - Function //
	protected void ReadPoint() {
		Pointer = new ReadPointer(prop);
		Curr_Pointer = Pointer.GetPointer();
	}
	
	protected String[] GetCurrPoint(){
		return Curr_Pointer;
	}
	
	protected void SavePointerValue(String Pointer_Date_to_be_Saved, String Entry_Count){ //
		Save_Pointer = new SavePointer(prop, Pointer_Date_to_be_Saved, Entry_Count);
	}
	
	protected String GetFileLastModifyDate(File AuditFile){ //
		Date Last_Modify = new Date(AuditFile.lastModified());
		String Pattern = "yyyyMMddHHmmssSSS";
		SimpleDateFormat DateFormat = new SimpleDateFormat(Pattern);
		String File_Date = DateFormat.format(Last_Modify);
		
		return File_Date;
	}
	
	protected void MakeAuditFileList(String Curr_Pointer_Date) {
		Target_Audit_List = new GetAuditFileList(prop,Curr_Pointer_Date);
	}
	
	protected List<File> GetAuditLogList(){
		return Target_Audit_List.GetTargetAuditList();
	}
	// Source : Audit - Function //
	
	// Target : LDAP - Function //
	protected void ConnectLDAP(){
		LDAP_Conn = new LDAPConnector(prop);
	}
	
	protected LDAPConnection GetLDAPConnection(){
		return LDAP_Conn.GetLDAPConnection();
	}
	
	protected void DataSync(LDIFChangeRecord ChangeRecord){
		Sync_to_LDAP = new SynctoLDAP(GetLDAPConnection(),ChangeRecord);
	}
	
	protected int getSyncResultCode(){
		return Sync_to_LDAP.getResultCode();
	}
	
	protected String getSyncResultMessage(){
		return Sync_to_LDAP.getResultMessage();
	}
	// Target : LDAP - Function //
	
	// Set Results //
	protected void CountTarget(boolean TargetYN){
		Logger_to_LDAP.CountTarget(TargetYN);
	}
	
	protected void PrintOpResult(LDIFChangeRecord Origin_Record,int resultCode,String resultMessage){
		Logger_to_LDAP.PrintOpResult(Origin_Record, resultCode, resultMessage);
	}
	
	protected void PrintSyncResults(){
		Logger_to_LDAP.PrintSyncResults();
	}
	
	protected boolean SaveErrorEntry(){
		return Logger_to_LDAP.SaveErrorEntryYN();
	}
	
	protected boolean LDAPConnectYN(){
		return Logger_to_LDAP.LDAPConnectYN();
	}
	
	protected void SaveErrorDatatoLDIF(String Path){
		Err_Data_to_LDIF = new SaveErrorDatatoLDIF(Path);
	}
	
	protected void WriteLDIFFile(LDIFChangeRecord ChangeRecord,String ResultMessage){
		Err_Data_to_LDIF.WriteLDIFFile(ChangeRecord, ResultMessage);
	}
	
	protected void CloseLDIFWriter(){
		Err_Data_to_LDIF.CloseLDIFWriter();
	}
	// Set Results//

	}
