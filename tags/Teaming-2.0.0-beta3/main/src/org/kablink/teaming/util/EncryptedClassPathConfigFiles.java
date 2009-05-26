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
package org.kablink.teaming.util;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.kablink.util.Validator;
import org.kablink.teaming.ConfigurationException;
import org.apache.commons.codec.binary.Base64;
public class EncryptedClassPathConfigFiles extends PropertiesClassPathConfigFiles
	implements InitializingBean {
	
	protected PBEStringEncryptor encryptor=null;
	private Properties props;
	protected String eConfigFile;
	protected Resource eResource=null;
	    

	
	public void setEncryptor(PBEStringEncryptor encryptor) {
		this.encryptor = encryptor;
	}
    public void afterPropertiesSet() throws Exception {
    	super.afterPropertiesSet();
    	props = super.getProperties();
    	String key = props.getProperty("kablink.encryption.key"); //this will get the last definition of the key 
    	if (Validator.isNull(key)) throw new ConfigurationException("Missing encryption key");
    	key = new String(Base64.decodeBase64(key.getBytes()), "UTF-8");
    	//set key for encryption
    	encryptor.setPassword(key);
    	if (eResource != null) {
    	   	Properties tempProps = new Properties();
        	tempProps.load(eResource.getInputStream());
        	//encrypt and get key
        	String  ePropNames = props.getProperty("kablink.encryption.key.names");
        	if (Validator.isNotNull(ePropNames)) {
        		String []eProps = ePropNames.split(",");
        		//see if any properties need to be encrypted
        		boolean needUpdate = false;
        		for (int i=0; i<eProps.length; ++i) {
        			String val = tempProps.getProperty(eProps[i].trim());
        			if (Validator.isNull(val)) continue;//not supplied
        			if (!PropertyValueEncryptionUtils.isEncryptedValue(val)) {
        				val = encryptor.encrypt(val);
        				tempProps.setProperty(eProps[i], "ENC("+ val +")");
        				needUpdate = true;
        			}
        		}
        		if (needUpdate) {
        			// We call writePropertiesToFile() instead of calling Properties.store()
        			// because Properties.store() will escape certain characters.  See bug 477366
        			writePropertiesToFile( tempProps, eResource.getFile() );
        		}
        	}
        	props.putAll(tempProps);
    	}
    	props = new EncryptableProperties(props, encryptor);
 
 
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
    	Properties	properties,
    	File		file )
    		throws IOException
    {
	    BufferedWriter aWriter;
	    FileOutputStream outputStream;
	    Enumeration keys;
	    
	    outputStream = new FileOutputStream( file );
	    aWriter = new BufferedWriter( new OutputStreamWriter( outputStream, "8859_1" ) );

	    // Write the date and time to the file.
        aWriter.write( "#" + new Date().toString() );
        aWriter.newLine();

        // Spin through the list of key/value pairs and write them to the file.
	    for (keys = properties.keys(); keys.hasMoreElements();)
	    {
	        String key;
	        String val;
	        
	        // Write the next key/value pair to the file.
	        key = (String)keys.nextElement();
	        val = (String)properties.get( key );
	        aWriter.write( key + "=" );
	        if ( val != null )
	        	aWriter.write( val );
	        aWriter.newLine();
	    }

	    aWriter.flush();
	    aWriter.close();
    }// end writePropertiesToFile()
 
}
