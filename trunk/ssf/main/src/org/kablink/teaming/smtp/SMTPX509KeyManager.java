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
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

public class SMTPX509KeyManager extends X509ExtendedKeyManager {
	
    // Log object for this class.
    private static final Log logger = LogFactory.getLog(SMTPX509KeyManager.class);
    
    
    // Note:  The code to load a key store needs to be revisited in
    // that we need to fully determine where the default key store
    // should get loaded from.
    //
    // DO NOT SET THIS TRUE until that's resolved.
    private static final boolean LOAD_KEYSTORE = false;
    private static final String PROP_KEYSTORE = "javax.net.ssl.keyStore";
    private static final String PROP_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    private static final String PROP_KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
    
    private X509ExtendedKeyManager standardKeyManager = null;

	/*
	 * Inner class used for KeyStore access information.
	 */
	private class KeyStoreInfo {
		private static final String DEFAULT_KS_PASSWORD = "changeit";
		
		private String keyStorePath = null;
		private String keyStorePassword = null;
		private String keyStoreType = null;
		
	    /**
	     * Constructor for KeyStoreInfo.
	     */
		public KeyStoreInfo() {
			super();
		}

		
		/*
		 * Returns the password to use to access the key store.
		 */
		String getKeyStorePassword() {
	    	if ((null == keyStorePassword) || (0 == keyStorePassword.length())) {
		    	keyStorePassword = System.getProperty(PROP_KEYSTORE_PASSWORD);
		    	if ((null == keyStorePassword) || (0 == keyStorePassword.length())) {
		    		keyStorePassword = DEFAULT_KS_PASSWORD;
		    	}
			}
			return keyStorePassword;
		}
		
		char[] getKeyStorePasswordChars() {
			return getKeyStorePassword().toCharArray();
		}

		
		/*
		 * Returns the path to the key store file to load.
		 */
		String getKeyStorePath() {
        	if ((null == keyStorePath) || (0 == keyStorePath.length())) {
	        	keyStorePath = System.getProperty(PROP_KEYSTORE);
	        	if ((null == keyStorePath) || (0 == keyStorePath.length())) {
		        	String catalinaHome = System.getProperty("catalina.home").trim();
		        	if (catalinaHome.charAt(catalinaHome.length() - 1) != File.separatorChar) {
		        		catalinaHome += File.separator;
		        	}
		        	keyStorePath = (catalinaHome + "conf" + File.separator + ".keystore");
	        	}
			}
			return keyStorePath;
		}

		
		/*
		 * Returns the type of key store to load the file as.
		 */
		String getKeyStoreType() {
	    	if ((null == keyStoreType) || (0 == keyStoreType.length())) {
		    	keyStoreType = System.getProperty(PROP_KEYSTORE_TYPE);
		    	if ((null == keyStoreType) || (0 == keyStoreType.length())) {
		    		keyStoreType = KeyStore.getDefaultType();
		    	}
			}
			return keyStoreType;
		}
	}
	
    /**
     * Constructor for SMTPX509KeyManager.
     */
    @SuppressWarnings("unchecked")
	public SMTPX509KeyManager() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        super();

        KeyStore keyStore;
        char[] keyStorePassword;
        if (LOAD_KEYSTORE) {
	        KeyStoreInfo ksi = new KeyStoreInfo();
	        keyStore = KeyStore.getInstance(ksi.getKeyStoreType());
	        keyStorePassword = ksi.getKeyStorePasswordChars();
	        
	        Exception ex = null;
	        try {
		        // Load the keystore file... 
		        InputStream ksf = ((InputStream) new FileInputStream(ksi.getKeyStorePath())); 
		        keyStore.load(ksf, ksi.getKeyStorePasswordChars());
		        ksf.close();
		        
		        // ...and display information about its contents. 
		        if (logger.isDebugEnabled()) {
			        logger.debug("SMTP SSL keystore contains " + keyStore.size()  + " certificates." ) ;
			        int count = 1;
			        for  (Enumeration e = keyStore.aliases(); e.hasMoreElements();) {  
			        	logger.debug("...item " + count + ":  " + e.nextElement()); 
			        	count += 1; 
			        }
		        }
	        }
	        catch (CertificateException e) {ex = e;}
	        catch (FileNotFoundException e) {ex = e;}
	        catch (IOException e) {ex = e;}
	        if (null != ex) {
	            logger.error("Error reading SMTP SSL keystore:  " + ex.toString(), ex);
	        	keyStore = null;
	        }
        }
        else {
        	keyStore = null;
        	keyStorePassword = null;
        }

        String keyManagmentAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory factory = KeyManagerFactory.getInstance(keyManagmentAlgorithm);
        factory.init(keyStore, keyStorePassword);
        KeyManager[] keyManagers = factory.getKeyManagers();
        if (keyManagers.length == 0) {
            throw new NoSuchAlgorithmException(keyManagmentAlgorithm + " key manager not supported");
        }
        this.standardKeyManager = ((X509ExtendedKeyManager) keyManagers[0]);
    }

	public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
        logger.debug("chooseClientAlias()");
		return this.standardKeyManager.chooseClientAlias(arg0, arg1, arg2);
	}

	public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
        logger.debug("chooseServerAlias()");
		return this.standardKeyManager.chooseServerAlias(arg0, arg1, arg2);
	}

	public X509Certificate[] getCertificateChain(String arg0) {
        logger.debug("getCertificateChain()");
		return this.standardKeyManager.getCertificateChain(arg0);
	}

	public String[] getClientAliases(String arg0, Principal[] arg1) {
        logger.debug("getClientAliases()");
		return this.standardKeyManager.getClientAliases(arg0, arg1);
	}

	public PrivateKey getPrivateKey(String arg0) {
        logger.debug("getPrivateKey()");
		return this.standardKeyManager.getPrivateKey(arg0);
	}

	public String[] getServerAliases(String arg0, Principal[] arg1) {
        logger.debug("getServerAliases()");
		return this.standardKeyManager.getServerAliases(arg0, arg1);
	}
}
