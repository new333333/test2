/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.tomcat.jdbc.pool;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.kablink.util.PropertiesUtil;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;
import org.kablink.util.encrypt.PropertyEncrypt;

public class DataSourceFactory extends org.apache.tomcat.jdbc.pool.DataSourceFactory {

	private static String catalinaBase;
	
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?,?> environment) throws Exception {
    	Reference ref = (Reference) obj;
    	RefAddr ra = ref.get("catalinaBase");
    	catalinaBase = ra.getContent().toString();
    	
    	return super.getObjectInstance(obj, name, nameCtx, environment);
    }
    
	@Override
    public DataSource createDataSource(Properties properties,Context context, boolean XA) throws Exception {
		String password = properties.getProperty(PROP_PASSWORD);
		
		if(password == null) {
			String decryptedPassword = null;
			String ssfFilePath = catalinaBase + "/webapps/ssf/WEB-INF/classes/config/ssf.properties";
			String ssfExtFilePath = catalinaBase + "/webapps/ssf/WEB-INF/classes/config/ssf-ext.properties";
			Properties props = new Properties();
			PropertiesUtil.loadProperties(props, ssfFilePath);
			PropertiesUtil.loadProperties(props, ssfExtFilePath);
			
			// Bugzilla 945714 - When upgrading from Vibe 3.x to 4.0.1, we change not only the
			// encryption algorithm (from PBEWithMD5AndDES to PBEWITHSHA256AND128BITAES-CBC-BC)
			// but also the encryption key (from hard-coded constant to randomly generated
			// site-specific key). When upgrading from Vibe 4.0.0 to 4.0.1, the encryption
			// algorithm remains the same but the encryption key changes (again, from hard-coded
			// constant to randomly generated cluster-wide and site-specific key).
			// Since encrypted passwords encode a hint about the algorithm used into the values,
			// migrating passwords across different algorithms over different product versions
			// is relatively systematic. In comparison, migrating passwords for different keys
			// is more tricky and less robust. Generally speaking, that's a situation we should
			// try to avoid at all cost. Unfortunately, Vibe 3.x to 4.0.0 upgrade introduced one
			// such incident.
			String key = null;
			if("true".equalsIgnoreCase(props.getProperty("kablink.encryption.key.sitescape"))) {
				// This represents an upgrade situation where the system was using hard-coded
				// key value prior to upgrade and the upgrade process assigned a newly generated
				// random key which is yet to be used for password encryption.
				key = "SiteScape";
			}
			else {
				String encodedKey = props.getProperty("kablink.encryption.key");
				if(encodedKey != null && !encodedKey.equals("")) {
					key = new String(Base64.decodeBase64(encodedKey.getBytes()), "UTF-8");
				}
			}
			if(key != null && !key.equals("")) {
				ExtendedPBEStringEncryptor encryptor = ExtendedPBEStringEncryptor.create(props.getProperty(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_PROPERTY_NAME), key);
				ExtendedPBEStringEncryptor encryptor_first_gen = ExtendedPBEStringEncryptor.createFirstGen(key);
				props = new PropertyEncrypt(props, encryptor, encryptor_first_gen);
				decryptedPassword = props.getProperty("database.password");
			}

			if(decryptedPassword != null)
				properties.setProperty(PROP_PASSWORD, decryptedPassword);
		}
		
		return super.createDataSource(properties, context, XA);
    }
}
