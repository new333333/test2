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
package org.kablink.teaming.smtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

public class SMTPX509TrustManager implements X509TrustManager {
	
    // Log object for this class.
    private static final Log logger = LogFactory.getLog(SMTPX509TrustManager.class);

    // Note:  The code to load a trust store needs to be revisited in
    // that we need to fully determine where the default trust store
    // should get loaded from.
    //
    // DO NOT SET THIS TRUE until that's resolved.
    private static final boolean LOAD_TRUSTSTORE = false;
    private static final String PROP_TRUSTSTORE = "javax.net.ssl.trustStore";
    private static final String PROP_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    private static final String PROP_TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";

    private X509TrustManager standardTrustManager = null;
    
	/*
	 * Inner class used for KeyStore access information.
	 */
	private class TrustStoreInfo {
		private static final String DEFAULT_TS_PASSWORD = "changeit";
		
		private String trustStorePath = null;
		private String trustStorePassword = null;
		private String trustStoreType = null;
		
	    /**
	     * Constructor for TrustStoreInfo.
	     */
		public TrustStoreInfo() {
			super();
		}

		
		/*
		 * Returns the password to use to access the trust store.
		 */
		String getTrustStorePassword() {
	    	if ((null == trustStorePassword) || (0 == trustStorePassword.length())) {
		    	trustStorePassword = System.getProperty(PROP_TRUSTSTORE_PASSWORD);
		    	if ((null == trustStorePassword) || (0 == trustStorePassword.length())) {
		    		trustStorePassword = DEFAULT_TS_PASSWORD;
		    	}
			}
			return trustStorePassword;
		}
		
		char[] getTrustStorePasswordChars() {
			return getTrustStorePassword().toCharArray();
		}

		
		/*
		 * Returns the path to the trust store file to load.
		 */
		String getTrustStorePath() {
        	if ((null == trustStorePath) || (0 == trustStorePath.length())) {
	        	trustStorePath = System.getProperty(PROP_TRUSTSTORE);
	        	if ((null == trustStorePath) || (0 == trustStorePath.length())) {
		        	String jreHome = "/usr/local/jdk/jre"; //! System.getProperty("jre.home").trim();
		        	if (jreHome.charAt(jreHome.length() - 1) != File.separatorChar) {
		        		jreHome += File.separator;
		        	}
		        	trustStorePath = (jreHome + "lib" + File.separator + "security" + File.separator + "cacerts");
	        	}
			}
			return trustStorePath;
		}

		
		/*
		 * Returns the type of trust store to load the file as.
		 */
		String getTrustStoreType() {
	    	if ((null == trustStoreType) || (0 == trustStoreType.length())) {
		    	trustStoreType = System.getProperty(PROP_TRUSTSTORE_TYPE);
		    	if ((null == trustStoreType) || (0 == trustStoreType.length())) {
		    		trustStoreType = KeyStore.getDefaultType();
		    	}
			}
			return trustStoreType;
		}
	}
	
    /**
     * Constructor for SMTPX509TrustManager.
     */
    @SuppressWarnings("unchecked")
	public SMTPX509TrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        super();

        KeyStore trustStore;
        if (LOAD_TRUSTSTORE) {
	        TrustStoreInfo tsi = new TrustStoreInfo();
	        trustStore = KeyStore.getInstance(tsi.getTrustStoreType());
	        
	        Exception ex = null;
	        try {
		        // Load the trust store file... 
		        InputStream ksf = ((InputStream) new FileInputStream(tsi.getTrustStorePath())); 
		        trustStore.load(ksf, tsi.getTrustStorePasswordChars());
		        ksf.close();
		        
		        // ...and display information about its contents. 
		        if (logger.isDebugEnabled()) {
			        logger.debug("SMTP SSL trust store contains " + trustStore.size()  + " certificates." ) ;
			        int count = 1;
			        for  (Enumeration e = trustStore.aliases(); e.hasMoreElements();) {  
			        	logger.debug("...item " + count + ":  " + e.nextElement()); 
			        	count += 1; 
			        }
		        }
	        }
	        catch (CertificateException e) {ex = e;}
	        catch (FileNotFoundException e) {ex = e;}
	        catch (IOException e) {ex = e;}
	        if (null != ex) {
	            logger.error("Error reading SMTP SSL trust store:  " + ex.toString(), ex);
	        	trustStore = null;
	        }
        }
        else {
        	trustStore = null;
        }
        
        String trustManagementAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
       	TrustManagerFactory factory = TrustManagerFactory.getInstance(trustManagementAlgorithm);
        factory.init(trustStore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException(trustManagementAlgorithm + " trust manager not supported");
        }
        this.standardTrustManager = ((X509TrustManager) trustmanagers[0]);
    }

    public X509Certificate[] getAcceptedIssuers() {
        logger.debug("getAcceptedIssuers()");
        return this.standardTrustManager.getAcceptedIssuers();
    }

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
        logger.debug("checkClientTrusted(X509Certificate()");
        this.standardTrustManager.checkClientTrusted(arg0, arg1);
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
        logger.debug("checkServerTrusted()");
        this.standardTrustManager.checkServerTrusted(arg0, arg1);
	}
}
