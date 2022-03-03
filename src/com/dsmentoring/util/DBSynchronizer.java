package com.dsmentoring.util;

import com.dsmentoring.sync.database.DBConnector;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class DBSynchronizer {

    protected EnvProperties _env				= null;
    protected DBConnector _dbConnector 			= null;
    protected Logger log           		        = null;

    public DBSynchronizer() {
        InitSynchronizer();
    }

    protected int InitSynchronizer() {
        _env = new EnvProperties();
        return 0;
    }

    protected int ConnectDB() {
        _dbConnector = new DBConnector(_env);
        return 0;
    }

    protected int DisConnectDB() {
        _dbConnector.disconnect();
        return 0;
    }

    protected EnvProperties GetEnv() {
        return _env;
    }

    protected Connection GetDBConn() {
        return _dbConnector.GetDBConnection();
    }


}
