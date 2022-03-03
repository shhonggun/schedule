package com.dsmentoring.sync.main;

import com.dsmentoring.target.db.SynctoDB;
import com.dsmentoring.target.ldap.SynctoLDAP;
import com.dsmentoring.util.EnvProperties;
import com.dsmentoring.util.ModuleSynchronizer;
import com.dsmentoring.util.StringUtil;
import com.dsmentoring.util.staticCode;
import com.sun.corba.se.impl.orb.ORBConfiguratorImpl;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.dsmentoring.sync.main.BatchMain.getSyncdb;
import static com.dsmentoring.sync.main.BatchMain.getSyncldap;

public class SyncMain extends ModuleSynchronizer implements Job {

    private static Logger log = LogManager.getLogger(SyncMain.class);
    private static EnvProperties prop = null;

    public static String DB_Row 	="";
    public static String TmpDB_Row 	="";
    public static String Rename_Row ="";
    public static String DB_Value 	="";
    public static String TmpPK_Row  ="";
    public static String DB_Pk  	="";
    public static String DB_update  ="";
    public static String DB_Pk_value="";
    public static String sMsg        ="";
    public SyncMain Sync;

    public static SynctoDB syncdb;
    public static SynctoLDAP syncldap;

    public static HashMap<String,String> map ;
    public static HashMap<String,String> pkmap ;
    public static HashMap<String,String> ldapmap ;



    public void execute(JobExecutionContext jobExecutionContext){

        long beforeTime = System.currentTimeMillis();
        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime)/1000;

        log.info("beforeTime = "+ beforeTime);
        log.info("afterTime = "+ afterTime);

        System.out.println("Start...." + new Date());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("End...." + new Date());

        try {
            BatchMain.getinstance().shutdown();
            log.info("scheduler을 종료한다.");


            BatchMain.getinstance().scheduleJob();


            log.info("scheduler을 시작한다.");

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        System.out.println("시간차이(m) : "+secDiffTime);

/*
        Sync = new SyncMain();
        syncdb =  getSyncdb();
        syncldap = getSyncldap();
        Sync.run();*/
    }

    public static void main (String[] args){
        PropertyConfigurator.configure("conf\\log4j.properties");
        log.info("=-=-=-=-= Audit to LDAP Sync Module Start =-=-=-=-=");
        SyncMain Sync = new SyncMain();
        syncdb =  getSyncdb();
        syncldap = getSyncldap();
        Sync.run();
    }


    public void run(){
        String Source_Type = prop.getValues("Source");

        DB_Row 		= new String();
        DB_Pk 		= new String();
        DB_Value 	= new String();
        DB_update	= new String();
        DB_Pk_value = new String();
        TmpDB_Row 	= new String();
        TmpPK_Row   = new String();
        Rename_Row	= new String();


        boolean 	bExclude;
        boolean		bPK;

        log.info("### Check the Source Data Type. ###");
        switch (Source_Type.toUpperCase()){
            case "AUDIT" :
                log.info("The Source Data was Set as an Audit File.");
                log.info("Check which Point of Data to Synchronize to LDAP.");
                ReadPoint();

                String Pointer_Date = GetCurrPoint()[0];
                String Pointer_EntryCount_String = GetCurrPoint()[1];
                int Pointer_EntryCount = Integer.parseInt(Pointer_EntryCount_String);

                StringUtil stringUtil = new StringUtil();
                stringUtil.SetStringUtil(stringUtil);
                stringUtil.InitRenameColumn();
                //stringUtil.InitExcludeColumn();
                stringUtil.InitIncludeColumn();
                stringUtil.InitPKColumn();


                MakeAuditFileList(Pointer_Date);
                List<File> Tmp_File_List = GetAuditLogList();

                staticCode.Total_Count    = 0;
                staticCode.Add_Fail_Count = 0;
                staticCode.Add_Succ_Count = 0;
                staticCode.Add_Fail_Count = 0;
                staticCode.Mod_Succ_Count = 0;
                staticCode.Mod_Fail_Count = 0;
                staticCode.Del_Succ_Count = 0;
                staticCode.Del_Fail_Count = 0;
                staticCode.Skip_Count = 0;

                for(File Tmp_File : Tmp_File_List){
                    int EntryCount = 0;
                    List<LDIFChangeRecord> ChangeRecord_List = TransformChangeRecord(Tmp_File);

                    if(ChangeRecord_List.size() == Pointer_EntryCount){
                        log.info("No Additional Sync Data.");
                    }


                    for(LDIFChangeRecord ChangeRecord : ChangeRecord_List){
                        EntryCount++;


                        if(EntryCount <= Pointer_EntryCount){
                            continue;
                        }

                        staticCode.Total_Count++;

                        ChangeType recordType = ChangeRecord.getChangeType();

                        DB_Pk     = "";
                        DB_Value  = "";
                        DB_update = "";
                        DB_Pk_value ="";

                        if (recordType.getName().equalsIgnoreCase("modify")) {

                            ModifyRequest modifications = ((LDIFModifyChangeRecord) ChangeRecord).toModifyRequest();
                            List<Modification> mods = modifications.getModifications();
                            Modification[] modifys = new Modification[mods.size()];
                            Iterator<Modification> iterator = mods.iterator();
                            map = new HashMap<>();
                            pkmap = new HashMap<>();

                            String strDn = modifications.getDN();

                            DB_Pk = stringUtil.GET_PK_COLUMN(strDn);
                            DB_Pk_value = stringUtil.GET_PK_VALUE(strDn);

                            for (int i = 0; i < modifys.length; i++) {
                                modifys[i] = iterator.next();
                                TmpDB_Row = modifys[i].getAttribute().getName();
                                bExclude = stringUtil.ComparedIncludeColumn(TmpDB_Row);

                                if (bExclude == true) {
                                    Rename_Row  = stringUtil.ComparedRenameColumn(TmpDB_Row);
                                    map.put(Rename_Row,modifys[i].getAttribute().getValue());
                                }
                            }


                            try {

                                if(map.size() == 0){
                                    staticCode.Skip_Count++;
                                    continue;
                                }

                                DB_Row = stringUtil.GET_DB_ROW(map);
                                DB_Value = stringUtil.GET_DB_VALUE(map);
                                DB_update = stringUtil.GET_DB_UPDATE(map);

                                boolean rtUpdate = syncdb.DBUpdate(DB_Row,DB_update,DB_Pk);

                                if ( rtUpdate == false)
                                {
                                    /* DB에 데이터가 없을 경우 LDAP에서 데이터를 가지고 와서 DB에 다시 넣어준다.*/
                                    map.clear();
                                    /* 기존의 모든 데이터를 삭제한다*/
                                    ldapmap = new HashMap<>();
                                    ldapmap = syncldap.GET2LDAP(DB_Pk_value);

                                    for(Map.Entry<String,String> entry:ldapmap.entrySet()){
                                        bExclude = stringUtil.ComparedIncludeColumn(entry.getKey());

                                        if (bExclude == true) {
                                            Rename_Row  = stringUtil.ComparedRenameColumn(entry.getKey());

                                            map.put(Rename_Row,entry.getValue());
                                        }
                                    }

                                    DB_Row = stringUtil.GET_DB_ROW(map);
                                    DB_Value = stringUtil.GET_DB_VALUE(map);
                                    boolean  rtInsert = syncdb.DBInsert(DB_Row,DB_Value);

                                    if (rtInsert == true)
                                    {
                                        staticCode.Add_Succ_Count++;
                                        log.info(DB_Pk+" Insert Success");
                                    }
                                    else
                                    {
                                        staticCode.Add_Fail_Count ++;
                                        log.info(DB_Pk+" Insert Fail");
                                    }

                                }
                                else
                                {
                                    staticCode.Mod_Succ_Count++;
                                    log.info(DB_Pk+" Modify Success");
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                                staticCode.Mod_Fail_Count ++;
                                log.info(DB_Pk+" Modify Fail");
                            }


                        }else if(recordType.getName().equalsIgnoreCase("add")) {
                            Attribute[] attributes   = ((LDIFAddChangeRecord)ChangeRecord).getAttributes();

                            map = new HashMap<>();
                            pkmap = new HashMap<>();

                            for(int i =0 ; i < attributes.length ; i++){

                                TmpDB_Row = attributes[i].getName();
                                bExclude = stringUtil.ComparedIncludeColumn(TmpDB_Row);

                                if (bExclude == true) {

                                    Rename_Row  = stringUtil.ComparedRenameColumn(TmpDB_Row);
                                    map.put(Rename_Row,attributes[i].getValue());
                                    bPK = stringUtil.ComparedPkColumn(TmpDB_Row);

                                    if(bPK == true){
                                        pkmap .put(Rename_Row,attributes[i].getValue());
                                    }
                                }
                            }

                            DB_Pk = stringUtil.GET_DB_PK(pkmap);

                           // System.out.println ("DB_Row="+DB_Row +", DB_Value =" +DB_Value);
                           // System.out.println ("DB_PK="+DB_Pk);

                            if (map.size() == 0){
                                staticCode.Skip_Count++;
                                continue;
                            }

                            DB_Row = stringUtil.GET_DB_ROW(map);
                            DB_Value = stringUtil.GET_DB_VALUE(map);
                            DB_update = stringUtil.GET_DB_UPDATE(map);

//                            log.info("DB_Row =" +DB_Row);
//                            log.info("DB_Value=" +DB_Value);
//                            log.info("DB_update=" +DB_update);
//                            log.info("DB_Pk =" +DB_Pk);

                            syncdb.DB_Add(DB_Row,DB_Value,DB_update,DB_Pk);
                            //log.info("cn = "+DB_Pk +"success" );

                        }

                        log.debug("--------------------------------------------------------------------------------------------");
                    }

                    Pointer_EntryCount = 0;

                    String Pointer_Date_to_be_Saved = GetFileLastModifyDate(Tmp_File);
                    SavePointerValue(Pointer_Date_to_be_Saved, Integer.toString(EntryCount));

                }
                break;
            case "CSV" :
                log.info("The Source Data was Set as an CSV File.");
                break;
            case "LDAP" :
                log.info("The Source Data was Set to LDAP.");
                break;
            case "DB" :
                log.info("The Source Data was Set to DB.");
                break;
            default :
                log.info("Invalid Source Data Type. Exit Program.");
                log.info("============================================================================================");
                System.exit(1);
        }

        PrintSyncDBResults();
        log.info("");
    }

    public SyncMain(){
        prop = GetEnv();
        log.debug("--------------------------------------------------------------------------------------------");
        ConnectLDAP();
        log.info("--------------------------------------------------------------------------------------------");
        String Err_Data_File_Path = prop.getValues("Error_Entry_Stored_In_LDIF_File");
        SaveErrorDatatoLDIF(Err_Data_File_Path);
        log.debug("--------------------------------------------------------------------------------------------");
    }


    public void PrintSyncDBResults(){
        log.info("===========================================");
        log.info("TOTAL  ENTRY COUNT		: " + staticCode.Total_Count);
        log.info("-------------------------------------------");
        log.info("TOTAL SUCC  COUNT : " + (staticCode.Add_Succ_Count + staticCode.Mod_Succ_Count + staticCode.Del_Succ_Count));
        log.info("TOTAL FAIL  COUNT : " + (staticCode.Add_Fail_Count + staticCode.Mod_Fail_Count + staticCode.Del_Fail_Count));
        log.info("SKIP        COUNT : " + staticCode.Skip_Count);
        log.info("===========================================");
        log.info("SUCC ADD COUNT : " + staticCode.Add_Succ_Count);
        log.info("FAIL ADD COUNT : " + staticCode.Add_Fail_Count);
        log.info("-------------------------------------------");
        log.info("SUCC MOD ENTRY : " + staticCode.Mod_Succ_Count);
        log.info("FAIL MOD ENTRY : " + staticCode.Mod_Fail_Count);
        log.info("-------------------------------------------");
        log.info("SUCC DEL ENTRY : " + staticCode.Del_Succ_Count);
        log.info("FAIL DEL ENTRY : " + staticCode.Del_Fail_Count);
        log.info("===========================================");
    }


}