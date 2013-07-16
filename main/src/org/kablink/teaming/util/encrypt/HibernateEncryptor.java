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

import org.jasypt.encryption.pbe.PBEStringEncryptor;

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

	// We use "ENC2(" to signal new algorithm.
	// The values encrypted with the old algorithm do NOT contain any prefix/suffix, and therefore
	// we will simply assume that any encoded value lacking this new prefix is encrypted with the
	// old algorithm. We can make this assumption safely because this wrapper encryptor will never
	// be invoked to decrypt a value that wasn't previously encrypted.
    private static final String PREFIX = "ENC2(";
    private static final String SUFFIX = ")";

	private PBEStringEncryptor encryptor;
	private PBEStringEncryptor encryptor_preFilr1_0;
	
	public HibernateEncryptor(PBEStringEncryptor encryptor, PBEStringEncryptor encryptor_preFilr1_0) {
		this.encryptor = encryptor;
		this.encryptor_preFilr1_0 = encryptor_preFilr1_0;
	}
	
	@Override
	public String encrypt(String message) {
		// For encryption, we always use the new algorithm.
		// Make sure to include the prefix/suffix in the output for storage.
		return getDecoratedEncryptedValue(encryptor.encrypt(message));
	}

	@Override
	public String decrypt(String encryptedMessage) {
		if(isEncryptedWithNewEncryptor(encryptedMessage)) {
			// This data was encrypted using the new encryptor.
			// Decrypt it using new encryptor. Also we need to strip off prefix
			// and suffix before decrypting it.
			return encryptor.decrypt(getBaseEncryptedValue(encryptedMessage));
		}
		else {
			// This data was encrypted using the old encryptor.
			// Decrypt it using old encryptor. The decrypted value doesn't contain any prefix/suffix.
			return encryptor_preFilr1_0.decrypt(encryptedMessage);
		}
	}

	@Override
	public void setPassword(String password) {
		encryptor.setPassword(password);
		encryptor_preFilr1_0.setPassword(password);
	}
	
	private String getDecoratedEncryptedValue(String encVal) {
		return PREFIX+ encVal + SUFFIX;
	}

	private boolean isEncryptedWithNewEncryptor(String encryptedVal) {
		if(encryptedVal == null)
			return false;
		encryptedVal = encryptedVal.trim();
		return (encryptedVal.startsWith(PREFIX) && encryptedVal.endsWith(SUFFIX));
	}

	private static String getBaseEncryptedValue(String encodedVal) {
        return encodedVal.substring(PREFIX.length(), (encodedVal.length() - SUFFIX.length()));	
	}

}
