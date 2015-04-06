/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.lang.invoke.MethodHandles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.kablink.teaming.ConfigurationException;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;

/**
 * This class wraps old and new encryptors and delegate as appropriate to perform
 * encryption and decryption of data stored in the database in a way that graciously
 * handles upgrade situation from pre Filr 1.0 and pre Vibe 4.0 installation.
 * Note that this wrapper is used only to encrypt/decrypt data stored in the database
 * declaratively through Hibernate mapping files. This is NOT used to encrypt user
 * passwords.
 * 
 * @author jong
 *
 */
public class HibernateEncryptor implements PBEStringEncryptor {
	
	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	// We use "ENC2(" to signal new algorithm (second generation).
	// The values encrypted with the old first generation algorithm do NOT contain any prefix/suffix, 
	// and therefore we will simply assume that any encoded value lacking this new prefix is encrypted with
	// the first generation algorithm. We can make this assumption safely because this wrapper encryptor will
	// never be invoked to decrypt a value that wasn't previously encrypted.
    private static final String PREFIX_SECOND_GEN = "ENC2(";
    private static final String SUFFIX_SECOND_GEN = ")";

    // Current encryptor (may be first or second generation)
	private ExtendedPBEStringEncryptor encryptor;
	// First generation encryptor
	private ExtendedPBEStringEncryptor encryptor_first_gen;
	
	public HibernateEncryptor(ExtendedPBEStringEncryptor encryptor, ExtendedPBEStringEncryptor encryptor_first_gen) {
		this.encryptor = encryptor;
		this.encryptor_first_gen = encryptor_first_gen;
	}
	
	@Override
	public String encrypt(String message) {
		// For encryption, we always use the "current" encryptor.
		// Make sure to include the prefix/suffix in the output for storage.
		if(encryptor.getGeneration() == 2) {
			return getDecoratedEncryptedValue_second_gen(encryptor.encrypt(message));
		}
		else {
			return getDecoratedEncryptedValue_first_gen(encryptor.encrypt(message));
		}
	}

	@Override
	public String decrypt(String encryptedMessage) {
		if(isEncryptedWithSecondGenEncryptor(encryptedMessage)) {
			// This data was encrypted using the second generation encryptor.
			if(encryptor.getGeneration() == 2) {
				// Decrypt it using new encryptor. Also we need to strip off prefix
				// and suffix before decrypting it.
				return encryptor.decrypt(getBaseEncryptedValue_second_gen(encryptedMessage));
			}
			else {
				String errMsg = "Cannot decode second generation encoded value using first generation encryptor. System supports encryptor upgrade but not downgrade.";
				logger.error(errMsg);
				throw new ConfigurationException(errMsg);
			}
		}
		else {
			// This data was encrypted using first generation encryptor.
			// Decrypt it using first generation encryptor.
			return encryptor_first_gen.decrypt(getBaseEncryptedValue_first_gen(encryptedMessage));
		}
	}

	@Override
	public void setPassword(String password) {
		encryptor.setPassword(password);
		encryptor_first_gen.setPassword(password);
	}
	
	private String getDecoratedEncryptedValue_second_gen(String encVal) {
		return PREFIX_SECOND_GEN + encVal + SUFFIX_SECOND_GEN;
	}

	private String getDecoratedEncryptedValue_first_gen(String encVal) {
		// No prefix or suffix when encryping with first generation encryptor
		return encVal;
	}

	private boolean isEncryptedWithSecondGenEncryptor(String encryptedVal) {
		if(encryptedVal == null)
			return false;
		encryptedVal = encryptedVal.trim();
		return (encryptedVal.startsWith(PREFIX_SECOND_GEN) && encryptedVal.endsWith(SUFFIX_SECOND_GEN));
	}

	private static String getBaseEncryptedValue_second_gen(String encodedVal) {
        return encodedVal.substring(PREFIX_SECOND_GEN.length(), (encodedVal.length() - SUFFIX_SECOND_GEN.length()));	
	}

	private static String getBaseEncryptedValue_first_gen(String encodedVal) {
		// Encoded value encrypted with first generation encryptor doesn't contain prefix or suffix.
        return encodedVal;
	}

}
