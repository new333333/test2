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
package org.kablink.teaming.tomcat.dbcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;

import org.apache.commons.codec.binary.Base64;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;
import org.kablink.util.encrypt.PropertyEncrypt;

public class TeamingBasicDataSourceFactory extends BasicDataSourceFactory {
	
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
		Object o = super.getObjectInstance(obj, name, nameCtx, environment);
		if ((o != null) && (o instanceof BasicDataSource)) {
			String safePassword = getSafePassword(obj);
			if(safePassword != null) {
				BasicDataSource ds = (BasicDataSource) o;
				ds.setPassword(safePassword);
			}
		}
		return o;
	}
	
	private String getSafePassword(Object obj) {
		try {
			String safePassword = null;
			if(obj instanceof ResourceRef) {
				ResourceRef ref = (ResourceRef) obj;
				RefAddr ra = ref.get("catalinaBase");
				if(ra != null) {
					String catalinaBase = ra.getContent().toString();
					String ssfFilePath = catalinaBase + "/webapps/ssf/WEB-INF/classes/config/ssf.properties";
					String ssfExtFilePath = catalinaBase + "/webapps/ssf/WEB-INF/classes/config/ssf-ext.properties";
					Properties props = new Properties();
					loadProperties(props, ssfFilePath);
					loadProperties(props, ssfExtFilePath);
					
					String key = null;
					if("true".equalsIgnoreCase(props.getProperty("kablink.encryption.key.sitescape"))) {
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
						safePassword = props.getProperty("database.password");
					}
				}		
			}
			return safePassword;
		}
		catch(Exception e) {
			return null;
		}
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
