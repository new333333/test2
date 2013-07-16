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

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.kablink.util.FileUtil;
import org.kablink.util.Validator;
import org.kablink.util.encrypt.PropertyEncrypt;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.util.PropertiesClassPathConfigFiles;
import org.apache.commons.codec.binary.Base64;
public class EncryptedClassPathConfigFiles extends PropertiesClassPathConfigFiles
	implements InitializingBean {
	
	protected PBEStringEncryptor encryptor=null;
	protected PBEStringEncryptor encryptor_preFilr1_0=null;
	private Properties props;
	protected String eConfigFile;
	protected Resource eResource=null;
	    	
	public void setEncryptor(PBEStringEncryptor encryptor) {
		this.encryptor = encryptor;
	}
	
	public void setEncryptor_preFilr1_0(PBEStringEncryptor encryptor_preFilr1_0) {
		this.encryptor_preFilr1_0 = encryptor_preFilr1_0;
	}
	
    public void afterPropertiesSet() throws Exception {
    	super.afterPropertiesSet();
    	props = super.getProperties();
    	String key = props.getProperty("kablink.encryption.key"); //this will get the last definition of the key 
    	if (Validator.isNull(key)) throw new ConfigurationException("Missing encryption key");
    	key = new String(Base64.decodeBase64(key.getBytes()), "UTF-8");
    	//set key for encryption
    	encryptor.setPassword(key);
    	encryptor_preFilr1_0.setPassword(key);
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
	        			if(PropertyEncrypt.isEncrypted_preFilr1_0(val)) {
	        				// The property is encrypted using old encryption algorithm (pre Filr 1.0 and Vibe 4.0)
	        				// We need to re-encrypt this value using new algorithm.
	        				String baseEncryptedValue = PropertyEncrypt.getBaseEncryptedValue_preFilr1_0(val);
	        				// Decrypt using old algorithm
	        				String decryptedValue = encryptor_preFilr1_0.decrypt(baseEncryptedValue);
	        				// Encrypt using new algorithm
	        				String encVal = encryptor.encrypt(decryptedValue);
	        				int beginIndex = origStr.indexOf(eProps[i]+"=");
	        				int index = origStr.indexOf("=", beginIndex+eProps[i].length());
	        				index = origStr.indexOf(val, index+1);
	        				String strToReplace = origStr.substring(beginIndex, index+val.length());
	        				String strToReplaceWith = eProps[i] + "=" + PropertyEncrypt.getDecoratedEncryptedValue(encVal);
	        				origStr = origStr.replace(strToReplace, strToReplaceWith);
	        				needUpdate = true;
	        			}
	        			else if (!PropertyEncrypt.isEncrypted(val)) {
	        				// This property is not encrypted at all.
	        				String encVal = encryptor.encrypt(val);
	        				int beginIndex = origStr.indexOf(eProps[i]+"=");
	        				int index = origStr.indexOf("=", beginIndex+eProps[i].length());
	        				index = origStr.indexOf(val, index+1);
	        				String strToReplace = origStr.substring(beginIndex, index+val.length());
	        				String strToReplaceWith = eProps[i] + "=" + PropertyEncrypt.getDecoratedEncryptedValue(encVal);
	        				origStr = origStr.replace(strToReplace, strToReplaceWith);
	        				needUpdate = true;
	        			}
	        		}
	        		if (needUpdate) {
	        			// We call writePropertiesToFile() instead of calling Properties.store()
	        			// because Properties.store() will escape certain characters.  See bug 477366
	        			writePropertiesToFile( origStr, eResource.getFile() );
	        		}
	        	}
    	   	}
    	   	finally {
    	   		is.close();
    	   	}
        	props.putAll(tempProps);
    	}
    	
    	props = new PropertyEncrypt(props, encryptor, encryptor_preFilr1_0);
    }
    
    //overload
    public Properties getProperties() {
    	return props;
    }
    //this file should be included in original list of files also
     public void setEncryptConfigFile(String eFile) {
   		Resource resource = null;
   		if(eFile.startsWith("optional:")) {
    		eFile = eFile.substring(9);
    		resource = toResource(eFile);
       		// The optional resource does not exist. Proceed.
       		if(!resource.exists()) return;
   		} else {
   			resource = toResource(eFile);
   		}
   		eConfigFile = eFile;
   		eResource = resource;
   		
    }
     
    /**
     * Write all of the key/values pairs found in the given Properties object to the given file.
     * We don't call Properties.store() because that call escapes certain characters found in the value.
     * See bug 477366
     */
    private void writePropertiesToFile(
    	String		content,
    	File		file )
    		throws IOException
    {
	    BufferedWriter aWriter;
	    
	    aWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), "8859_1" ) );

	    try {
		    // Write the date and time to the file.
	        aWriter.write( "#" + new Date().toString() );
	        aWriter.newLine();
	
	        aWriter.write(content);
	        
		    aWriter.flush();
	    }
	    finally {
	    	aWriter.close();
	    }
    }// end writePropertiesToFile()
 
}
