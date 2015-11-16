/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util.encrypt;

import java.util.Properties;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.kablink.util.FileUtil;
import org.kablink.util.PropertiesUtil;
import org.kablink.util.Validator;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;
import org.kablink.util.encrypt.PropertyEncrypt;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.util.PropertiesClassPathConfigFiles;
import org.kablink.teaming.util.PropertiesSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class delegates to PropertiesClassPathConfigFiles rather than subclasses it
 * in order to avoid circular dependency between the encryptor bean and the ssf
 * configuration properties bean.
 * 
 * @author jong
 *
 */
public class EncryptedClassPathConfigFiles
	implements PropertiesSource, InitializingBean {
	
	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());
	
	// delegatee
	PropertiesClassPathConfigFiles propertiesClassPathConfigFiles; 
	// Current encryptor (which may be first or second generation one).
	protected ExtendedPBEStringEncryptor encryptor=null;
	// First generation encryptor
	protected PBEStringEncryptor encryptor_first_gen=null;
	private Properties props;
	protected String eConfigFile;
	protected Resource eResource=null;
	    	
	public void setEncryptor(ExtendedPBEStringEncryptor encryptor) {
		this.encryptor = encryptor;
	}
	
	public void setEncryptor_first_gen(PBEStringEncryptor encryptor_first_gen) {
		this.encryptor_first_gen = encryptor_first_gen;
	}
	
	public void setPropertiesClassPathConfigFiles(PropertiesClassPathConfigFiles propertiesClassPathConfigFiles) {
		this.propertiesClassPathConfigFiles = propertiesClassPathConfigFiles;
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
    	props = propertiesClassPathConfigFiles.getProperties();
    	String key = props.getProperty("kablink.encryption.key"); //this will get the last definition of the key 
    	if (Validator.isNull(key)) 
    		throw new ConfigurationException("Missing encryption key");
    	String initialKey = props.getProperty("kablink.encryption.key.initial");
    	if(Validator.isNotNull(initialKey)) { // Initial key marked
    		// Make sure initial key is the same as the current key, that is, the key hasn't changed
    		if(!initialKey.equals(key)) {
    			logger.error("*** ENCRYPTION KEY MUST NEVER CHANGE. IT WILL MAKE SYSTEM UNUSABLE!! - initial=[" + initialKey + "], current=[" + key + "]");
    			throw new ConfigurationException("Detecting encryption key change - This will make system unusable!");
    		}
    	}
    	else { // Initial key not marked yet
    		// Mark current key as the initial (and the only) key
    		try(FileWriter fw = new FileWriter(eResource.getFile(), true); BufferedWriter bw = new BufferedWriter(fw)) {
    			bw.newLine();
    			String line = "kablink.encryption.key.initial=" + key;
    			bw.append(line);
    			bw.flush();
    		}
    	}
    	key = new String(Base64.decodeBase64(key.getBytes()), "UTF-8");
    	//set key for encryption
    	encryptor.setPassword(key);
    	encryptor_first_gen.setPassword(key);
    	if (eResource != null) {
    	   	Properties tempProps = new Properties();
    	   	InputStream is = eResource.getInputStream();
    	   	try {
	        	tempProps.load(is);
	        	//encrypt and get key
	        	String  ePropNames = props.getProperty("kablink.encryption.key.names");
	        	if (Validator.isNotNull(ePropNames)) {
	        		String origStr = FileUtil.readAsString(eResource.getFile(), "8859_1");
	        		String []eProps = ePropNames.split(",");
	        		//see if any properties need to be encrypted
	        		boolean needUpdate = false;
	        		for (int i=0; i<eProps.length; ++i) {
	        			String val = tempProps.getProperty(eProps[i].trim());
	        			if (Validator.isNull(val)) continue;//not supplied
	        			if(PropertyEncrypt.isEncrypted_first_gen(val)) {
	        				// The property is encrypted using first generation encryptor.
	        				if(encryptor.getGeneration() == 2) {
		        				// We need to re-encrypt this value using new algorithm (second generation)
		        				String baseEncryptedValue = PropertyEncrypt.getBaseEncryptedValue_first_gen(val);
		        				// Decrypt using old algorithm
		        				String decryptedValue = encryptor_first_gen.decrypt(baseEncryptedValue);
		        				// Encrypt using new algorithm
		        				String encVal = encryptor.encrypt(decryptedValue);
		        				int beginIndex = origStr.indexOf(eProps[i]+"=");
		        				int index = origStr.indexOf("=", beginIndex+eProps[i].length());
		        				index = origStr.indexOf(val, index+1);
		        				String strToReplace = origStr.substring(beginIndex, index+val.length());
		        				String strToReplaceWith = eProps[i] + "=" + PropertyEncrypt.getDecoratedEncryptedValue_second_gen(encVal);
		        				origStr = origStr.replace(strToReplace, strToReplaceWith);
		        				needUpdate = true;
	        				}
	        				else {
	        					// No need to re-encrypt the value. Good as is.
	        				}
	        			}
	        			else if (PropertyEncrypt.isEncrypted_second_gen(val)) {
	        				// The property is encrypted using second generation encryptor
	        				if(encryptor.getGeneration() == 2) {
	        					// No need to do anything. Good as is.
	        				}
	        				else {
	        					throw new ConfigurationException("Cannot decode second generation encoded value using first generation encryptor. System supports encryptor upgrade but not downgrade.");
	        				}
	        			}
	        			else {
	        				// This property is not encrypted at all.
	        				String encVal = encryptor.encrypt(val);
	        				int beginIndex = origStr.indexOf(eProps[i]+"=");
	        				int index = origStr.indexOf("=", beginIndex+eProps[i].length());
	        				// (Bug #908400) To handle escaped backslash (if any) included in the property value.
	        				String valAsWrittenInFile = val.replace("\\", "\\\\");
	        				index = origStr.indexOf(valAsWrittenInFile, index+1);
	        				String strToReplace = origStr.substring(beginIndex, index+valAsWrittenInFile.length());
	        				String strToReplaceWith;
	        				if(encryptor.getGeneration() == 2)
	        					strToReplaceWith = eProps[i] + "=" + PropertyEncrypt.getDecoratedEncryptedValue_second_gen(encVal);
	        				else
	        					strToReplaceWith = eProps[i] + "=" + PropertyEncrypt.getDecoratedEncryptedValue_first_gen(encVal);
	        				origStr = origStr.replace(strToReplace, strToReplaceWith);
	        				needUpdate = true;
	        			}
	        		}
	        		if (needUpdate) {
	        			// We call writePropertiesToFile() instead of calling Properties.store()
	        			// because Properties.store() will escape certain characters.  See bug 477366
	        			PropertiesUtil.writePropertiesToFile( origStr, eResource.getFile() );
	        		}
	        	}
    	   	}
    	   	finally {
    	   		is.close();
    	   	}
        	props.putAll(tempProps);
    	}
    	
    	props = new PropertyEncrypt(props, encryptor, encryptor_first_gen);
    }
    
    @Override
    public Properties getProperties() {
    	return props;
    }
    
    //this file should be included in original list of files also
     public void setEncryptConfigFile(String eFile) {
   		Resource resource = null;
   		if(eFile.startsWith("optional:")) {
    		eFile = eFile.substring(9);
    		resource = propertiesClassPathConfigFiles.toResource(eFile);
       		// The optional resource does not exist. Proceed.
       		if(!resource.exists()) return;
   		} else {
   			resource = propertiesClassPathConfigFiles.toResource(eFile);
   		}
   		eConfigFile = eFile;
   		eResource = resource;
   		
    }
     
}
