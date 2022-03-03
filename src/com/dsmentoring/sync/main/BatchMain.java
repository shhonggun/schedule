package com.dsmentoring.sync.main;

import com.dsmentoring.target.db.SynctoDB;
import com.dsmentoring.target.ldap.SynctoLDAP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Properties;



public class BatchMain {


    String prop_file = "conf/Module.conf";
    public static SchedulerFactory schedulerFactory = null;
    public static Scheduler scheduler = null;
    //private EnvProperties props = null;
    private Properties props = null;
    //private Class BATCH_SYNC = com.dsmentoring.sync.main.SyncMain.class;
    private Class BATCH_SYNC = com.dsmentoring.sync.main.SyncMain.class;


    public static SynctoDB syncdb;
    public static SynctoLDAP syncldap;


    private static Logger log = LogManager.getLogger(SyncMain.class);

    public JobDetail BatchSyncJob = null;
    public CronTrigger BatchCrontrigger = null;

    public BatchMain batchmain;
    public String batch_run_time = "";
    public String batch_sync = "";


    public BatchMain(){
        props           = new Properties();
        batch_run_time  = props.getProperty("BATCH.SYNC.time");
        batch_sync      = props.getProperty("BATCH.SYNC");
    }

    public static SynctoDB getSyncdb() {
        return syncdb;
    }
    public static SynctoLDAP getSyncldap() {
        return syncldap;
    }


    public void scheduerStart(){

        syncdb = new SynctoDB();
        syncldap = new SynctoLDAP();

        try {
            schedulerFactory = new StdSchedulerFactory();
            props.load(new FileInputStream(prop_file));

            scheduler = schedulerFactory.getScheduler();
            scheduler.start();

            batch_run_time = props.getProperty("BATCH.SYNC.time");
            batch_sync = props.getProperty("BATCH.SYNC");

            BatchSyncJob = new JobDetail("batchSync", "batch", BATCH_SYNC);
            BatchCrontrigger = new CronTrigger("batchSync", "batch", batch_run_time);

            System.out.println("batch_sync=" + batch_sync);


            long beforeTime = System.currentTimeMillis();
            long afterTime = System.currentTimeMillis();
            long secDiffTime = (afterTime - beforeTime)/1000;

            log.info("beforeTime = "+ beforeTime);
            log.info("afterTime = "+ afterTime);


            System.out.println("시간차이(m) : "+secDiffTime);



            if ("Y".equals(batch_sync)) {
                if (batch_run_time == null || batch_run_time.trim().length() == 0) {
                    if (scheduler != null) {
                        scheduler.shutdown();
                        scheduler = null;
                        schedulerFactory = null;
                    }
                }
                scheduler.scheduleJob(BatchSyncJob, BatchCrontrigger);
            }


        } catch(ParseException pe) {
            System.out.println("parse Error : " + pe.getMessage());
        } catch(Exception e) {
            System.out.println("Exception : " + e.getMessage());
        } finally {

        }


    }

    public static SchedulerFactory getSchedulerFactory()
    {
        return schedulerFactory;
    }

    public static Scheduler getinstance()
    {
        return scheduler;
    }

    public JobDetail getBatchSyncJob() {
        return BatchSyncJob;
    }

    public CronTrigger getBatchCrontrigger() {
        return BatchCrontrigger;
    }


    public void SchStart() throws SchedulerException {

        scheduler.scheduleJob(BatchSyncJob, BatchCrontrigger);

    }




    public static void main(String[] args){
        //new LDAPSync();
        System.out.println("시작");
        new BatchMain();
    }

}

