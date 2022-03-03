package com.dsmentoring.sync.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.dsmentoring.util.EnvProperties;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.ChangeLogEntry;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.transformations.ExcludeAttributeTransformation;
import com.unboundid.ldap.sdk.transformations.MoveSubtreeTransformation;
import com.unboundid.ldap.sdk.transformations.RenameAttributeTransformation;

public class EditChangeRecords {
	private static Logger log = LogManager.getLogger(EditChangeRecords.class);
	private static EnvProperties prop = null;
	
//	private static LDIFChangeRecord TargetChangeRecord = null;
//	private static String ChagneType = null;
//	private static String TargetDN = null;
	
	private static ChangeLogEntry RecordEntry = null;

	public EditChangeRecords(EnvProperties Env,LDIFChangeRecord ChangeRecord){
		prop = Env;
		log.debug("Start Checking and Editing the Change Record.");
//		TargetChangeRecord = ChangeRecord;
//		ChagneType = TargetChangeRecord.getChangeType().toString();
	}
	
	public boolean CheckTargetYN(LDIFChangeRecord ChangeRecord){
		String Include_Parent_EntryDNs = prop.getValues("Include_ParentEntryDN");
		String[] Include_Parent_EntryDN = Include_Parent_EntryDNs.split(",");
		
		boolean Check_DN = true;
		for(String Tmp : Include_Parent_EntryDN){
			if(ChangeRecord.getDN().toLowerCase().endsWith(Tmp.toLowerCase())){
				Check_DN = true;
			}else{
				Check_DN = false;
			}
		}
		return Check_DN;
	}
	
	public void RecordToEntry(long ChangeNumber,LDIFChangeRecord ChangeRecord){
		try{
			RecordEntry = ChangeLogEntry.constructChangeLogEntry(ChangeNumber,ChangeRecord);
		}catch(Exception E){
//			log.error(E);
		}
	}
	
	public ChangeLogEntry getEntry(){
		return RecordEntry;
	}
	
	public List<Attribute> getEntryAttributeSet(){
		if(RecordEntry.getChangeType().toString().equalsIgnoreCase("Add")){
			List<Attribute> Add_Attribute_List = new ArrayList<Attribute>(RecordEntry.getAddAttributes());
			return Add_Attribute_List;
		}else{
			return null;
		}
	}
	
	public List<Modification> getEntryModificationSet(){
		if(RecordEntry.getChangeType().toString().equalsIgnoreCase("Modify")){
			List<Modification> Mod_Modification_List = new ArrayList<Modification>(RecordEntry.getModifications());
			return Mod_Modification_List;
		}else{
			return null;
		}
	}
	
	public LDIFChangeRecord EditParentDN(LDIFChangeRecord ChangeRecord){
		log.debug("Change the DN Value of Change Record.");
		String Tmp_DNs = prop.getValues("Change_Parent_DN");
		
		for(String Old_New_DNs : Tmp_DNs.split(",")){
			String Old_DN = Old_New_DNs.split(":")[0];
			String New_DN = Old_New_DNs.split(":")[1];
			log.debug("--- Old DN : " + Old_DN + " / New DN : " + New_DN);
			try{
				DN oDN = new DN(Old_DN);
				DN nDN = new DN(New_DN);
				MoveSubtreeTransformation MoveDN_Trans = new MoveSubtreeTransformation(oDN,nDN);
				ChangeRecord = MoveDN_Trans.transformChangeRecord(ChangeRecord);
			}catch(Exception E){
				log.error(E);
				return null;
			}
		}
		return ChangeRecord;
	}
	
	public LDIFChangeRecord ExcludeAttribute(LDIFChangeRecord ChangeRecord){
		log.debug("Exclude some Attributes of Change Record.");
		String Exclude_Attrs = prop.getValues("Exclude_Attrs");
		String[] Exclude_Attr = Exclude_Attrs.split(",");
		
		for(String AttrName : Exclude_Attr){
			log.debug("--- Exclude Attribute : " + AttrName);
		}
		
		ExcludeAttributeTransformation EAT_DN = new ExcludeAttributeTransformation(null,Exclude_Attr);
		ChangeRecord = EAT_DN.transformChangeRecord(ChangeRecord);
		
		return ChangeRecord;
	}
	
	public LDIFChangeRecord RenameAttribute(LDIFChangeRecord ChangeRecord){
		log.debug("Change the Name of some Attributes in the Change Record.");
		String Tmp_Rename_Attrs = prop.getValues("Rename_Attribute");
		
		for(String Rename_Attrs : Tmp_Rename_Attrs.split(",")){
			String OriginValue = Rename_Attrs.split(":")[0];
			String ReNameValue = Rename_Attrs.split(":")[1];
			log.debug("--- Origin Attribute Name : " + OriginValue + " / Rename Value : " + ReNameValue);
			RenameAttributeTransformation RenameAttr_Trans = new RenameAttributeTransformation(null,OriginValue,ReNameValue,true);
			ChangeRecord = RenameAttr_Trans.transformChangeRecord(ChangeRecord);
		}
		
		return ChangeRecord;
	}




	public LDIFChangeRecord AddAttribute(LDIFChangeRecord ChangeRecord,String ChangeType){
		log.debug("Add Attribute to Change Record.");
		String TargetDN = ChangeRecord.getDN();
		List<Attribute> Add_Attribute_List = getEntryAttributeSet();
		List<Modification> Mod_Modification_List = getEntryModificationSet();
		
		String Tmp_Add_Attrs = prop.getValues("Add_Attribute");		
		String[] Tmp_Add_Attr = Tmp_Add_Attrs.split(",");
		for(String Add_Attr : Tmp_Add_Attr){
			String Add_Attr_Name = Add_Attr.split(":")[0];
			String Add_Attr_Value = Add_Attr.split(":")[1];
			
			if(ChangeType.equalsIgnoreCase("Add")){
				for(Attribute Attr : Add_Attribute_List){
					if(Add_Attr_Value.equalsIgnoreCase(Attr.getName())){
						Add_Attr_Value = Attr.getValue();
					}
				}
				log.debug("Attribute Name : " + Add_Attr_Name + " / Attribute Value : " + Add_Attr_Value);
				Attribute Tmp_Attr = new Attribute(Add_Attr_Name, Add_Attr_Value);
				Add_Attribute_List.add(Tmp_Attr);
			}else if(ChangeType.equalsIgnoreCase("Modify")){
				for(Modification Mod : Mod_Modification_List){
					if(Mod.getAttribute().getName().equalsIgnoreCase(Add_Attr_Value)){
						Add_Attr_Value = Mod.getAttribute().getValue();
					}
				}
				log.debug("Attribute Name : " + Add_Attr_Name + " / Attribute Value : " + Add_Attr_Value);
				Modification Tmp_Mod = new Modification(ModificationType.REPLACE,Add_Attr_Name,Add_Attr_Value);
				Mod_Modification_List.add(Tmp_Mod);
			}
		}
		
		if(ChangeType.equalsIgnoreCase("Add")){
			LDIFAddChangeRecord AddChangeRecord =  new LDIFAddChangeRecord(TargetDN,Add_Attribute_List);
			ChangeRecord = AddChangeRecord;
		}else if(ChangeType.equalsIgnoreCase("Modify")){
			LDIFModifyChangeRecord ModChangeRecord =  new LDIFModifyChangeRecord(TargetDN,Mod_Modification_List);
			ChangeRecord = ModChangeRecord;
		}
		
		return ChangeRecord;
	}
}