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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
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
			loadProperties(props, ssfFilePath);
			loadProperties(props, ssfExtFilePath);
			
			String key = props.getProperty("kablink.encryption.key");
			if(key != null && !key.equals("")) {
				key = new String(Base64.decodeBase64(key.getBytes()), "UTF-8");
				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setProvider(new BouncyCastleProvider());
				encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");
				encryptor.setPassword(key);
				StandardPBEStringEncryptor encryptor_preFilr1_0 = new StandardPBEStringEncryptor();
				encryptor_preFilr1_0.setAlgorithm("PBEWithMD5AndDES");
				encryptor_preFilr1_0.setPassword(key);
				props = new PropertyEncrypt(props, encryptor, encryptor_preFilr1_0);
				decryptedPassword = props.getProperty("database.password");
			}
	
			if(decryptedPassword != null)
				properties.setProperty(PROP_PASSWORD, decryptedPassword);
		}
		
		return super.createDataSource(properties, context, XA);
    }
	private void loadProperties(Properties props, String filePath) {
		try {
			InputStream is = new FileInputStream(filePath);
			try {
				props.load(is);
			}
			finally {
				try {
					is.close();
				}
				catch(IOException ignore) {}
			}
		}
		catch(IOException ignore) {}
	}
}
