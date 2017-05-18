/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.encrypt;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * This class is used to conditionally create an encryptor implementing strong encryption only
 * when the system is actually configured to do so. This is to avoid having to require
 * the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files in this 
 * Java Virtual Machine except when it is actually needed by the runtime. This class serves
 * when programmatic configuration is preferred to static configuration through Spring context.
 * 
 * @author jong
 *
 */
public class ExtendedPBEStringEncryptor implements PBEStringEncryptor {
	
    // Symmetric encryption algorithm used with Filr product beginning with 1.0 release (second generation)
    public static final String SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN = "PBEWITHSHA256AND128BITAES-CBC-BC";

    // Symmetric encryption algorithm used before Filr 1.0 release and also with all versions of Vibe product including Hudson (first generation)
    public static final String SYMMETRIC_ENCRYPTION_ALGORITHM_FIRST_GEN = "PBEWithMD5AndDES";
    
    public static final String SYMMETRIC_ENCRYPTION_ALGORITHM_PROPERTY_NAME = "symmetric.string.encryptor.algorithm";

	// This is the real one - We can't extend StandardPBEStringEncryptor since it is a final class.
	private StandardPBEStringEncryptor delegatee; 
	
	private String algorithm;
	private String password;
	private int generation;

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
		if(delegatee != null)
			delegatee.setAlgorithm(algorithm);
	}
	
	public String getAlgorithm() {
		return algorithm;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	protected void initialize() {
		if(algorithm == null)
			throw new IllegalArgumentException("Algorithm must be specifled");
		delegatee = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
		delegatee.setAlgorithm(algorithm);
		if(password != null)
			delegatee.setPassword(password);
		if(SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN.equals(algorithm)) {
			// For this algorithm, use Bouncy Castle provider. The one in JDK doesn't support this.
			delegatee.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			setGeneration(2);
		}
		else if(SYMMETRIC_ENCRYPTION_ALGORITHM_FIRST_GEN.equals(algorithm)) {
			setGeneration(1);
		}
	}	

	/* (non-Javadoc)
	 * @see org.jasypt.encryption.StringEncryptor#encrypt(java.lang.String)
	 */
	@Override
	public String encrypt(String message) {
		return delegatee.encrypt(message);
	}

	/* (non-Javadoc)
	 * @see org.jasypt.encryption.StringEncryptor#decrypt(java.lang.String)
	 */
	@Override
	public String decrypt(String encryptedMessage) {
		return delegatee.decrypt(encryptedMessage);
	}

	/* (non-Javadoc)
	 * @see org.jasypt.encryption.pbe.PasswordBased#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
		if(delegatee != null)
			delegatee.setPassword(password);
	}

	/// Convenience factory methods to be used outside of Spring context
	
	public static ExtendedPBEStringEncryptor create(String algorithm, String encryptorPassword)  {
		ExtendedPBEStringEncryptor encryptor = new ExtendedPBEStringEncryptor();
		encryptor.setAlgorithm(algorithm);
		encryptor.setPassword(encryptorPassword);
		encryptor.initialize();
		return encryptor;
	}
	
	public static ExtendedPBEStringEncryptor createFirstGen(String encryptorPassword) {
		ExtendedPBEStringEncryptor encryptor = new ExtendedPBEStringEncryptor();
		encryptor.setAlgorithm(SYMMETRIC_ENCRYPTION_ALGORITHM_FIRST_GEN);
		encryptor.setPassword(encryptorPassword);
		encryptor.initialize();
		return encryptor;
	}
	
	public static ExtendedPBEStringEncryptor createSecondGen(String encryptorPassword) {
		ExtendedPBEStringEncryptor encryptor = new ExtendedPBEStringEncryptor();
		encryptor.setAlgorithm(SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN);
		encryptor.setPassword(encryptorPassword);
		encryptor.initialize();
		return encryptor;
	}
}
