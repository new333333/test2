/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.util;

import java.util.Properties;
import java.io.FileOutputStream;
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
        			tempProps.store(new FileOutputStream(eResource.getFile()), null);
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
  
 
}
