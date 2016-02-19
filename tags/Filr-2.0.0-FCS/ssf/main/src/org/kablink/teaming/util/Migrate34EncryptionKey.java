/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.dao.JdbcDao;
import org.kablink.teaming.util.encrypt.HibernateEncryptor;
import org.kablink.util.FileUtil;
import org.kablink.util.PropertiesUtil;
import org.kablink.util.Validator;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;
import org.kablink.util.encrypt.PropertyEncrypt;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Jong
 *
 */
public class Migrate34EncryptionKey implements InitializingBean {

	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private JdbcDao jdbcDao;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// First, if necessary, migrate node-specific items stored in ssf-ext.properties file.
		migrateNodeSpecificItems();
		
		// Second, if necessary, migrate cluster-wide items stored in the database.
		migrateSiteWideItems();
	}
	
	private void migrateNodeSpecificItems() throws IOException {
		String webappRootDirPath = DirPath.getWebappRootDirPath();
		String ssfFilePath = webappRootDirPath + "/WEB-INF/classes/config/ssf.properties";
		String ssfExtFilePath = webappRootDirPath + "/WEB-INF/classes/config/ssf-ext.properties";
		
		File ssfExtFile = new File(ssfExtFilePath);
		if(!ssfExtFile.exists()) {
			logger.info("File [" + ssfExtFilePath + "] doesn't exist - No node-specific encrypted properties to migrate.");
			return;
		}
		
		Properties ssfExtProps = new Properties(); // Raw properties from ssf-ext.properties
		PropertiesUtil.loadProperties(ssfExtProps, ssfExtFilePath);

		int migratedFirstGenPropertiesCount = 0;
		int migratedSecondGenPropertiesCount = 0;
		int noChangePropertiesCount = 0;
		int candidatePropertiesCount = 0;
		
		if("true".equalsIgnoreCase(ssfExtProps.getProperty("kablink.encryption.key.sitescape"))) {
			// This indicates that the upgraded system was using the hard-coded constant key value
			// for encryption, and now it should be using a brand new key value specific to the site.
			// All we have to do is to decrypt the items from ssf-ext.properties using the old
			// encryption key and write them back out in plain text into ssf-ext.properties file.
			// Then, when the file is subsequently loaded and processed a bit later during this
			// context initialization process, the items will be encrypted using the new key and
			// written back out to the file again.
			logger.info("Migrating encrypted properties in ssf-ext.properties file");
			Properties props = new Properties(); // Combined raw properties
			PropertiesUtil.loadProperties(props, ssfFilePath);
			PropertiesUtil.loadProperties(props, ssfExtFilePath);			
        	String ePropNames = props.getProperty("kablink.encryption.key.names");
			String origStr = FileUtil.readAsString(ssfExtFile, "8859_1");
        	if (Validator.isNotNull(ePropNames)) {
    			String oldKey = "SiteScape";
    			ExtendedPBEStringEncryptor encryptor_second_gen = ExtendedPBEStringEncryptor.createSecondGen(oldKey);
    			ExtendedPBEStringEncryptor encryptor_first_gen = ExtendedPBEStringEncryptor.createFirstGen(oldKey);
        		String[] eProps = ePropNames.split(",");
        		candidatePropertiesCount = eProps.length;
        		//see if any properties need to be encrypted
        		for (int i=0; i<eProps.length; ++i) {
        			String val = ssfExtProps.getProperty(eProps[i].trim());
        			if (Validator.isNull(val)) 
        				continue; // This property is not supplied
        			if(PropertyEncrypt.isEncrypted_first_gen(val)) {
        				// The property is encrypted using first generation encryptor.
        				if(logger.isDebugEnabled())
        					logger.debug("Migrating property '" + eProps[i] + "' (first gen)");
        				String baseEncryptedValue = PropertyEncrypt.getBaseEncryptedValue_first_gen(val);
        				// Decrypt using first generation algorithm and old key.
        				String decryptedValue = encryptor_first_gen.decrypt(baseEncryptedValue);
        				// Bugzilla 908400 - If the plain value contains backslash character, 
        				// it needs to be escaped to make it safe for storage in properties file.
        				String decryptedValueAsWrittenInFile = decryptedValue.replace("\\", "\\\\");
        				// Replace the encrypted value in the file with the plain value.
        				int beginIndex = origStr.indexOf(eProps[i]+"=");
        				int index = origStr.indexOf("=", beginIndex+eProps[i].length());
        				index = origStr.indexOf(val, index+1);
        				String strToReplace = origStr.substring(beginIndex, index+val.length());
        				String strToReplaceWith = eProps[i] + "=" + decryptedValueAsWrittenInFile;
        				origStr = origStr.replace(strToReplace, strToReplaceWith);
        				migratedFirstGenPropertiesCount++;
        			}
        			else if (PropertyEncrypt.isEncrypted_second_gen(val)) {
        				// The property is encrypted using second generation encryptor
        				if(logger.isDebugEnabled())
        					logger.debug("Migrating property '" + eProps[i] + "' (second gen)");
        				String baseEncryptedValue = PropertyEncrypt.getBaseEncryptedValue_second_gen(val);
        				// Decrypt using second generation algorithm and old key.
        				String decryptedValue = encryptor_second_gen.decrypt(baseEncryptedValue);
        				// If the plain value contains backslash character, it needs to be escaped to make
        				// it safe for storage in properties file.
        				String decryptedValueAsWrittenInFile = decryptedValue.replace("\\", "\\\\");
        				// Replace the encrypted value in the file with the plain value.
        				int beginIndex = origStr.indexOf(eProps[i]+"=");
        				int index = origStr.indexOf("=", beginIndex+eProps[i].length());
        				index = origStr.indexOf(val, index+1);
        				String strToReplace = origStr.substring(beginIndex, index+val.length());
        				String strToReplaceWith = eProps[i] + "=" + decryptedValueAsWrittenInFile;
        				origStr = origStr.replace(strToReplace, strToReplaceWith);
        				migratedSecondGenPropertiesCount++;
        			}
        			else {
        				// This property is not encrypted at all. No update needed for this property.
        				noChangePropertiesCount++;
        			}
        		}
        	}
        	
    		// Now that we successfully turned all encrypted items into plain ones, remove
    		// the marker to indicate that we're done with this special processing.
    		origStr = origStr.replace("kablink.encryption.key.sitescape=true", "");
    		// When upgrade path is Vibe 3.4 -> 4.0.0 -> 4.0.1, the upgraded system must have
    		// kablink.encryption.key.initial property where its value is the same as the
    		// hard-coded constant key value that we're trying to move away from. If we leave
    		// this property in the file, this startup process will fail shortly in the
    		// afterPropertiesSet() method in EncryptedClassPathConfigFiles class because
    		// it will find the value of this property being different from the value of
    		// the kablink.encryption.key property. Therefore, we need to remove this 
    		// property as well right here, if exists, to prevent a trouble later on.
    		String initialValue = ssfExtProps.getProperty("kablink.encryption.key.initial");
    		if(Validator.isNotNull(initialValue)) {
    			origStr = origStr.replace("kablink.encryption.key.initial=" + initialValue, "");
    		}
			// Bugzilla 477366 - We call writePropertiesToFile() instead of calling 
			// Properties.store() because Properties.store() will escape certain characters.
    		PropertiesUtil.writePropertiesToFile(origStr, ssfExtFile);        	
        	
        	logger.info("Candidate properties=" + candidatePropertiesCount + 
        			", Migrated properties(first gen)=" + migratedFirstGenPropertiesCount + 
        			", Migrated properties(second gen)=" + migratedSecondGenPropertiesCount +
        			", No change properties=" + noChangePropertiesCount);
		}	
		else {
			if(logger.isDebugEnabled())
				logger.debug("No need for node-specific migration");
		}
	}
	
	private void migrateSiteWideItems() throws IOException {
		String webappRootDirPath = DirPath.getWebappRootDirPath();
		String ssfExtFilePath = webappRootDirPath + "/WEB-INF/classes/config/ssf-ext.properties";
		if(!(new File(ssfExtFilePath)).exists()) {
			logger.info("File [" + ssfExtFilePath + "] doesn't exist - No site-wide items to migrate.");
			return;
		}		
	
		String ssfFilePath = webappRootDirPath + "/WEB-INF/classes/config/ssf.properties";
		Properties props = new Properties(); // Combined raw properties
		PropertiesUtil.loadProperties(props, ssfFilePath);
		PropertiesUtil.loadProperties(props, ssfExtFilePath);			
	
		String filePath = props.getProperty("data.root.dir") + File.separator + "conf" + File.separator + "mswei"; // mswei = Migrate Site-Wide Encrypted Items marker file
		Path file = Paths.get(filePath);
		if(Files.exists(file)) {
			logger.info("Migrating encrypted items in database");
			
			Properties ssfExtProps = new Properties(); // Raw properties from ssf-ext.properties
			PropertiesUtil.loadProperties(ssfExtProps, ssfExtFilePath);
			
			String newKey = ssfExtProps.getProperty("kablink.encryption.key");
			if(Validator.isNull(newKey)) {
				logger.info("No encryption key found in ssf-ext.properties");
				return;
			}			
			newKey = new String(Base64.decodeBase64(newKey.getBytes()), "UTF-8");
			
			ExtendedPBEStringEncryptor newkey_encryptor_second_gen = ExtendedPBEStringEncryptor.createSecondGen(newKey);
			ExtendedPBEStringEncryptor newkey_encryptor_first_gen = ExtendedPBEStringEncryptor.createFirstGen(newKey);
			HibernateEncryptor hibernateEncryptorWithNewKey = new HibernateEncryptor(newkey_encryptor_second_gen, newkey_encryptor_first_gen);

			String oldKey = "SiteScape";
			ExtendedPBEStringEncryptor oldkey_encryptor_second_gen = ExtendedPBEStringEncryptor.createSecondGen(oldKey);
			ExtendedPBEStringEncryptor oldkey_encryptor_first_gen = ExtendedPBEStringEncryptor.createFirstGen(oldKey);
			HibernateEncryptor hibernateEncryptorWithOldKey = new HibernateEncryptor(oldkey_encryptor_second_gen, oldkey_encryptor_first_gen);
		
			Map<String,Integer> result = getJdbcDao().migrateHibernateEncryptedItems(hibernateEncryptorWithOldKey, hibernateEncryptorWithNewKey);
			StringBuilder sb = new StringBuilder("Number of migrated Hibernate-encrypted items in db: ");
			int count = 0;
			for(Map.Entry<String, Integer> entry : result.entrySet()) {
				if(count > 0)
					sb.append(", ");
				sb.append(entry.getKey())
				.append("=")
				.append(entry.getValue());
				count++;
			}
			logger.info(sb.toString());
			
			result = getJdbcDao().migrateUserPasswords(oldkey_encryptor_first_gen,
					oldkey_encryptor_second_gen,
					newkey_encryptor_first_gen,
					newkey_encryptor_second_gen);
			sb = new StringBuilder("Number of migrated user passwords in db: ");
			count = 0;
			for(Map.Entry<String, Integer> entry : result.entrySet()) {
				if(count > 0)
					sb.append(", ");
				sb.append(entry.getKey())
				.append("=")
				.append(entry.getValue());
				count++;
			}
			logger.info(sb.toString());
			
			logger.info("Deleting marker file [" + filePath + "]");
			try {
				Files.delete(file);
			} catch (IOException e) {
				logger.error("Error deleting marker file [" + filePath + "]. ***** THIS IS SERIOUS!! ADMINISTRATOR NEED TO REMOVE THIS FILE MANUALLY AND IMMEDIATELY!! *****");
				throw e; // Rethrow
			}
		}
		else {
			if(logger.isDebugEnabled())
				logger.debug("No need for site-wide migration");
		}
	}

	protected JdbcDao getJdbcDao() {
		return jdbcDao;
	}

	public void setJdbcDao(JdbcDao jdbcDao) {
		this.jdbcDao = jdbcDao;
	}
}
