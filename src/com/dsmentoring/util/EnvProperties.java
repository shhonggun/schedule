package com.dsmentoring.util;

import java.util.*;
import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Title		Module to Synchronize Audit Log Data to LDAP
 * @CreateDate	2020-12-10
 * @UpdateDate	2020-12-10
 * @EMail		tech@dsmentoring.com
 * @Version		1.0
 * @Description	Module to Synchronize Audit Log Data to LDAP
 * @Author		Hyun Woo Kim
 * @Copyright	Copyright (c) 2020 DSMentoring Co., Ltd. All rights reserved.
 * @Company		DSMentoring Co., Ltd.
 */

public class EnvProperties {
	private static Logger log = LogManager.getLogger(EnvProperties.class);
	private Hashtable<String, String> _env = new Hashtable<String, String>();
	private final String _defaultEnvFile = "conf/Module.conf";
	
	public EnvProperties() {
		log.debug("Attempts to load the Configuration File.");
		Properties properties = new Properties();

		FileInputStream Input_File = null;
		try {
			Input_File = new FileInputStream(_defaultEnvFile);
			properties.load(Input_File);
			
			if (Input_File != null){
				Input_File.close();
			}
			
			Enumeration<?> Keys = properties.propertyNames();
			while (Keys.hasMoreElements()) {
				String Key = (String) Keys.nextElement();
				String Value = new String(properties.getProperty(Key).getBytes("8859_1"), "utf-8");
				_env.put(Key, Value);
			}
			
			log.debug("Loading the Configuration File was Successful.");
		} catch (Exception Ex) {
			log.error("Configuration File Read Failed. Exit Program.");
			log.error(Ex);
			System.exit(1);
			
		}
	}

	public String getValues(String Key) {
		String Value = (String) _env.get(Key);
		return Value;
	}
}
