package com.dsmentoring.target.db;

import com.dsmentoring.util.DBSynchronizer;
import com.dsmentoring.util.EnvProperties;
import com.dsmentoring.util.staticCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


public class SynctoDB  extends DBSynchronizer {

    private static Logger log = LogManager.getLogger(SynctoDB.class);

    private static EnvProperties prop = null;
    private static String BASE_DN = null;
    private static String BASE_ATTR = null;
    private static String RDN_ATTR = null;
    private static String RDN_COLU = null;
    private static String PWD_USER_ATTR = null;
    private static String PASSWD_COLU = null;


    private static String Master_DB_Table = null;
    private static String DB_Columns = null;
    private static String DB_WHERE = null;
    private static String Master_DB_Column[] = null;
    private static String LDAP_Attributes = null;
    private static String Master_LDAP_Attribute[] = null;

    private static boolean DelYN = false;
    private static int total_count = 0;
    private static int target_count = 0;
    private static int not_count = 0;
    private static int skip_count = 0;
    private static int a_succ_count = 0;
    private static int a_err_count = 0;
    private static int m_succ_count = 0;
    private static int m_err_count = 0;
    private static int d_succ_count = 0;
    private static int d_err_count = 0;


    public SynctoDB() {
        log.info("Check DB and LDAP");
        ConnectDB();
        prop = GetEnv();
    }


    public boolean DBCount(String DB_Where) throws Exception {
        String query;
        PreparedStatement stmt	= null;
        ResultSet rs            = null;
        int count =0;

        Master_DB_Table = prop.getValues("Master_DB_Table_User");

        try {
            query ="SELECT count(*) as CNT FROM "+Master_DB_Table + " where " +DB_Where ;
            stmt = GetDBConn().prepareStatement(query);
            rs = stmt.executeQuery();

            while ( rs.next() )
            {
                count = (rs.getInt("CNT"));
            }
        } catch (SQLException se) {
            log.error(">>>>> SQLException {" + se.toString() + "}");
            throw new Exception();
        } catch (Exception e) {
            log.error(">>>>> Exception {" + e.toString() + "}");
            throw new Exception();
        } finally {

            try{
                if( stmt != null){
                    stmt.close();
                }
            }catch (SQLException se) {
                log.error(">>>>> SQL close Error {" + se.getMessage() + "}");
            }

        }

        if (count >= 1 ){
            return true;
        }else{
            return false;
        }

    }


    public boolean DBInsert(String DB_Row, String DB_Value) throws SQLException {

        String query;
        Connection con  = null;
        PreparedStatement pstmt = null;
        int ret=0;
        Master_DB_Table = prop.getValues("Master_DB_Table_User");

        try{

            query ="INSERT INTO "+Master_DB_Table +"("+ DB_Row +") VALUES (" +DB_Value +");" ;
            pstmt = GetDBConn().prepareStatement(query);
            ret = pstmt.executeUpdate();
            GetDBConn().commit();

            if( ret == 0 ){
                log.info("query ="+query);
                log.error("데이터 입력 실패");
            }
            else{
                log.info("데이터 입력 성공");
            }

        }catch (SQLException se) {
            log.error(">>>>> SQLException {" + se.getMessage() + "}");
            GetDBConn().rollback();
            return false;
        } catch (Exception e) {
            log.error(">>>>> Exception {" + e.getMessage() + "}");
            GetDBConn().rollback();
            return false;
        } finally {
            try{
                if( pstmt != null){
                    pstmt.close();
                }
            }catch (SQLException se) {
                log.error(">>>>> SQL close Error {" + se.getMessage() + "}");
            }
        }

        if (ret > 0){
            return true;
        }else
        {
            return false;
        }

    }

    public boolean DBUpdate(String DB_Row, String DB_Value, String DB_Where) throws SQLException {
        String query;

        Connection con  = null;
        PreparedStatement pstmt = null;
        int ret=0;
        Master_DB_Table = prop.getValues("Master_DB_Table_User");
        try{
            query ="UPDATE "+Master_DB_Table +" SET " + DB_Value + " WHERE " + DB_Where;
            pstmt = GetDBConn().prepareStatement(query);
            ret = pstmt.executeUpdate();
            GetDBConn().commit();

            //log.info("query ="+query);

            if( ret == 0 ){
                //log.error("데이터 입력 실패");
            }
            else{
                //log.info("데이터 입력 성공");
            }

        }catch (SQLException se) {
            log.error(">>>>> SQLException {" + se.getMessage() + "}");
            GetDBConn().rollback();
            return false;
        } catch (Exception e) {
            log.error(">>>>> Exception {" + e.getMessage() + "}");
            GetDBConn().rollback();
            return false;
        } finally {
            try{
                if( pstmt != null){
                    pstmt.close();
                }
            }catch (SQLException se) {
                log.error(">>>>> SQL close Error {" + se.getMessage() + "}");
            }
        }

        if (ret > 0){
            return true;
        }else
        {
            return false;
        }
    }

    public void DB_Add(String DB_Row, String DB_Value,String DB_Update,String DB_Where)
    {
        boolean result      =false;
        boolean rtInsert    =false;
        boolean rtUpdate    =false;

        staticCode.Total_Count++;
        staticCode.Mod_Succ_Count++;

        try {
            result = DBCount(DB_Where);
            if (result == true){
                rtUpdate = DBUpdate(DB_Row,DB_Update,DB_Where);
                if(rtUpdate == true){
                    staticCode.Mod_Succ_Count++;
                    log.info(DB_Where+" Modify Success");
                }else{
                    staticCode.Mod_Fail_Count++;
                    log.info(DB_Where+" Modify Fail");
                }
            }
            else{
                rtInsert = DBInsert(DB_Row,DB_Value);

                if(rtInsert == true){
                    staticCode.Add_Succ_Count++;
                    log.info(DB_Where+" Insert Success");
                }else{
                    staticCode.Add_Fail_Count++;
                    log.info(DB_Where+" Insert Fail");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Sync2LDAP() {

        try {

            BASE_ATTR = prop.getValues("DEFAULT_USER_VALS");
            String [] def_attr = BASE_ATTR.split(",");
            BASE_DN = prop.getValues("BASE_USER_DN");
            RDN_ATTR = prop.getValues("RDN_USER_ATTR");
            RDN_COLU = prop.getValues("RDN_USER_COLU");
            PWD_USER_ATTR =  prop.getValues("PWD_USER_ATTR");
            PASSWD_COLU = prop.getValues("PWD_USER_COLU");

            Master_DB_Table 	= prop.getValues("Master_DB_Table_User");
            DB_Columns 			= prop.getValues("Master_DB_USER_Column");


            PreparedStatement stmt	= null;
            ResultSet rs            = null;

            try
            {
                log.debug("DB Search - ");
                String SQL = "";
                if(null != DB_WHERE)
                    SQL = "SELECT "+ DB_Columns+" FROM "+Master_DB_Table+" "+DB_WHERE;
                else
                    SQL = "SELECT "+ DB_Columns+" FROM "+Master_DB_Table  ;

                stmt = GetDBConn().prepareStatement(SQL);
                rs = stmt.executeQuery();

                while ( rs.next() )
                {



                }

                if (rs   != null)   rs.close();
                if (stmt != null)   stmt.close();
            }
            catch (Exception ex)
            {
                log.error(" error : "+ex);
            }
            finally{
                if ( rs    != null ) try { rs   .close(); } catch(Exception e) {}
                if ( stmt  != null ) try { stmt .close(); } catch(Exception e) {}
            }

        }
        catch (Exception ex) {
            log.error("������ �߻� �Ͽ����ϴ�. �������� Ȯ���� �ʿ��մϴ�.");
            log.error(ex);
            ex.printStackTrace();
        }
        finally {

            if (GetDBConn() != null) {
                int k = DisConnectDB();
            }
        }

    }

    public void DBClose(){

        if (GetDBConn() != null) {
            int k = DisConnectDB();
        }
    }



}
