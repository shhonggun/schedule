package com.dsmentoring.util;

import com.dsmentoring.sync.data.EditChangeRecords;


import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StringUtil {

    private static Logger log = LogManager.getLogger(StringUtil.class);
    private static EnvProperties prop = null;

    /* 2022.0108 */
    private static HashMap<String,String> map  = null;
    private static List Exclude_list = null;
    private static List PkColumn_list = null;
    private static List Include_list = null;
    private static StringUtil stringUtil = null;

    public StringUtil(){
        prop = new EnvProperties();
    }

    public StringUtil  GetStringUtil()
    {
        return stringUtil;
    }

    public void  SetStringUtil(StringUtil SetstringUtil)
    {
        stringUtil = SetstringUtil;
    }


    /* 2022.01.08 ReName 설정 */
    public  void InitRenameColumn(){
        String Tmp_Rename_Column = prop.getValues("Rename_Column");
        map = new HashMap<>();
        System.out.println("Tmp_Rename_Column ="+Tmp_Rename_Column);
        log.debug("Tmp_Rename_Column ="+Tmp_Rename_Column);
        for(String Rename_Columns : Tmp_Rename_Column.split(",")){
            String OriginValue = Rename_Columns.split(":")[0];
            String ReNameValue = Rename_Columns.split(":")[1];
            log.debug("--- Origin Column Name : " + OriginValue + " / Rename Value : " + ReNameValue);
            map.put(OriginValue,ReNameValue);
        }
    }


    public static String ComparedRenameColumn(String strColumn){
        String getName = (String) map.get(strColumn);

        if (getName == null){
            return strColumn;
        }else{
            return getName;
        }

    }

    /* 2022.01.08 Exclude 설정 */
    public static void InitExcludeColumn(){
        String Exclude_Attrs = prop.getValues("Exclude_Column");
        Exclude_list = new ArrayList();

        log.debug("Exclude_Attrs="+Exclude_Attrs);

        String[] Exclude_Attr = Exclude_Attrs.split(",");

        for(String AttrName : Exclude_Attr){
            log.debug("--- Exclude Attribute : " + AttrName);
            Exclude_list.add(AttrName);
        }
    }

    public boolean ComparedExcludeColumn(String strColumn){
        Iterator iterator = Exclude_list.iterator();

        while (iterator.hasNext()) {
            String element = (String) iterator.next();

            if(strColumn.equalsIgnoreCase(element)){
                return true;
            }
        }
        return false;
    }

    /* 2022.01.08 Exclude 설정 */
    public static void InitIncludeColumn(){
        String Include_Attrs = prop.getValues("Include_Column");
        Include_list = new ArrayList();

        log.debug("Include_list="+Include_Attrs);
        String[] Exclude_Attr = Include_Attrs.split(",");

        for(String AttrName : Exclude_Attr){
            log.debug("--- Include Attribute : " + AttrName);
            Include_list.add(AttrName);
        }
    }

    public boolean ComparedIncludeColumn(String strColumn){
        Iterator iterator = Include_list.iterator();

        while (iterator.hasNext()) {
            String element = (String) iterator.next();

            if(strColumn.equalsIgnoreCase(element)){
                return true;
            }
        }
        return false;
    }

    /* 2022.01.08 PK 설정 */
    public static void InitPKColumn(){
        String Exclude_Attrs = prop.getValues("PK_Column");
        PkColumn_list = new ArrayList();

        log.debug("PK_Column="+Exclude_Attrs);
        String[] Exclude_Attr = Exclude_Attrs.split(",");

        for(String AttrName : Exclude_Attr){
            log.debug("--- Exclude Attribute : " + AttrName);
            PkColumn_list.add(AttrName);
        }
    }

    public boolean ComparedPkColumn(String strColumn){
        Iterator iterator = PkColumn_list.iterator();

        while (iterator.hasNext()) {
            String element = (String) iterator.next();

            if(strColumn.equalsIgnoreCase(element)){
                return true;
            }
        }
        return false;
    }

    public String GET_DB_ROW(HashMap<String,String> map)
    {
        String DB_Row = new String();

        for(Map.Entry<String,String> entry:map.entrySet()){
            DB_Row = DB_Row+entry.getKey() +",";
        }

        if (DB_Row != null){
            DB_Row		= DB_Row.substring(0,DB_Row.length()-1);
        }

        return DB_Row;
    }


    public String GET_DB_VALUE(HashMap<String,String> map)
    {
        String DB_Value = "";
        String key ="";
        String value ="";

        for(Map.Entry<String,String> entry:map.entrySet()){
            if( entry.getKey().equalsIgnoreCase("userpassword")){
                value = GET_PASSWD(entry.getValue());
            }else{
                value = entry.getValue();;
            }
            DB_Value = DB_Value + "'" + value + "',";
        }

        if (DB_Value != null) {
            DB_Value = DB_Value.substring(0, DB_Value.length() - 1);
        }

        return DB_Value;
    }


    public String GET_DB_UPDATE(HashMap<String,String> map)
    {
        String DB_update = "";
        String key ="";
        String value ="";

        for(Map.Entry<String,String> entry:map.entrySet()){
            key   = entry.getKey();
            value = entry.getValue();

            if( key.equalsIgnoreCase("userpassword")){
                value = GET_PASSWD(entry.getValue());
            }else{
                value = entry.getValue();;
            }

            DB_update = DB_update + key +"='"+ value +"',"  ;
        }

        if (DB_update != null){
            DB_update		= DB_update.substring(0,DB_update.length()-1);
        }

        return DB_update;
    }

    public String GET_DB_PK(HashMap<String,String> map)
    {
        String DB_PK = new String();

        for(Map.Entry<String,String> entry:map.entrySet()){
            DB_PK = DB_PK + entry.getKey() +"='"+ entry.getValue() +"',"  ;
        }

        if (DB_PK != null){
            DB_PK = DB_PK.substring(0,DB_PK.length()-1);
        }

        return DB_PK;
    }


    public String GET_PK_COLUMN(String strDn)
    {
        String DB_Pk= new String();

        String[] tmpMsg = strDn.split("=");
        String[] PkMsg 	= tmpMsg[1].split(",");

        DB_Pk  = stringUtil.ComparedRenameColumn(tmpMsg[0]);
        DB_Pk = DB_Pk + "='"+ PkMsg[0] +"'";

        return DB_Pk;
    }

    public String GET_PK_VALUE(String strDn)
    {
        String DB_Pk = new String();
        String DB_Pk_value = new String();

        String[] tmpMsg = strDn.split("=");
        String[] PkMsg 	= tmpMsg[1].split(",");

        // DB_Pk  = stringUtil.ComparedRenameColumn(tmpMsg[0]);
        // DB_Pk = DB_Pk + "='"+ PkMsg[0] +"'";
        DB_Pk_value = PkMsg[0];

        return DB_Pk_value;
    }

    static public   String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    static public String GET_PASSWD(String passwd)
    {
        String passmsg = passwd.split("}")[1];
        byte[] decode = Base64.getDecoder().decode(passmsg);
        String Result=byteArrayToHex(decode);

        return Result.toLowerCase();
    }


}
