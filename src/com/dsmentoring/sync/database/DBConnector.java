package com.dsmentoring.sync.database;

import com.dsmentoring.util.EnvProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnector {
    private static Logger log = LogManager.getLogger(DBConnector.class);
    protected Connection _dbconn = null;   //DB����

    Statement stmt = null;   //SQL Statement
    ResultSet rs = null;
    String strSQL = null;

    public DBConnector(EnvProperties prop) {

        log.debug(prop.getValues("DB_ID"));
        log.debug(prop.getValues("DB_PWD"));

        String DB_DRV = prop.getValues("DB_DRV");
        String DB_ID = prop.getValues("DB_ID");
        String DB_PWD = prop.getValues("DB_PWD");
        String DB_URL = prop.getValues("DB_URL");

        try {
            log.debug("=====================================");
            log.debug("DB_DRV : " + DB_DRV);
            log.debug("DB_URL : " + DB_URL);
            log.debug("DB_ID  : " + DB_ID);
            log.debug("DB_PWD : " + DB_PWD);
            log.debug("-------------------------------------");

            Class.forName(DB_DRV);
            _dbconn = DriverManager.getConnection(DB_URL, DB_ID, DB_PWD);
            _dbconn.setAutoCommit(false);

            log.debug("Database Connect Success");
        } catch (Exception ex) {
            log.error("Database Connect Fail");
            log.error(ex);
            System.exit(1);
        }
    }

    public Connection GetDBConnection() {
        return _dbconn;
    }

    public void excute() {
        try {
            strSQL = "SELECT * from tab";
            stmt = _dbconn.createStatement();
            rs = stmt.executeQuery(strSQL);
            log.debug("=====================================");
            log.debug("SQL : " + strSQL);
            log.debug("-------------------------------------");
            while (rs.next()) {
                log.debug("SID        : " + rs.getString(1));
                log.debug("USERNAME   : " + rs.getString(2));
                log.debug("SQL_ID     : " + rs.getString(3));
                log.debug("LOGON_TIME : " + rs.getString(4));
                log.debug("=====================================");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            disconnect();
        }
    }

    public void disconnect() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (_dbconn != null) _dbconn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (_dbconn != null) try {
                _dbconn.close();
            } catch (Exception e) {
            }
        }
    }

}

