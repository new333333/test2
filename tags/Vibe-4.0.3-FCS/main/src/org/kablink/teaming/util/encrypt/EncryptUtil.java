/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jasypt.encryption.StringEncryptor;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.domain.ProxyIdentity;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.PasswordHashEncryptor;
import org.kablink.util.cache.HashMapCache;
import org.kablink.util.encrypt.ExtendedPBEStringEncryptor;

public class EncryptUtil {
	
	private static Log logger = LogFactory.getLog(EncryptUtil.class);

    private static final String PASSWORD_ENCRYPTION_ALGORITHM_BEFORE_INDIVIDUALIZATION = SPropsUtil.getString("user.password.encryption.algorithm", "MD5");
    
    private static final String PASSWORD_ENCRYPTION_ALGORITHM_AFTER_INDIVIDUALIZATION = SPropsUtil.getString("user.pwdenc.default", "SHA-256");
    
    // We support three different asymmetric encryption algorithms.
    private static final String[] ASYMMETRIC_ENCRYPTION_ALGORITHMS = new String[] {"SHA", "SHA-256", "MD5"};
    
    private static HashMapCache<Long, String> passwordCache = new HashMapCache<Long, String>(SPropsUtil.getLong("cache.decrypted.passwords", 60));

	public static String encryptSHA1(String... input) {
		long startTime = System.nanoTime();

		String result = encrypt("SHA-1", input);
		
		end(startTime, "encryptSHA1");
		
		return result;
	}
	
	public static String encryptSHA256(Object... input) {
		long startTime = System.nanoTime();

		String result = encrypt("SHA-256", input);
		
		end(startTime, "encryptSHA256");
		
		return result;
	}
	
	private static String encrypt(String algorithm, Object[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.reset();
			for(int i = 0; i < input.length; i++) {
				if(input[i] != null) {
					if(input[i] instanceof String)
						md.update(((String)input[i]).getBytes("UTF-8"));
					else if(input[i] instanceof byte[])
						md.update(((byte[])input[i]));
					else
						md.update(input[i].toString().getBytes("UTF-8"));
				}
			}
			byte[] messageDigest = md.digest();
			
			StringBuffer hexString = new StringBuffer();
			for(int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString((0xf0 & messageDigest[i])>>4));
				hexString.append(Integer.toHexString(0x0f & messageDigest[i]));
			}
			return hexString.toString();
		}
		catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);			
		}
		catch (NullPointerException e) {
			// this will occur when the .getBytes("UTF-8") returns a null.
			return new StringBuffer().toString();
		}
	}

    public static void clearCachedPassword(Long userId) {
        passwordCache.remove(userId);
    }
	
	public static boolean checkPassword(String passwordToCheck, User user) {
		long startTime = System.nanoTime();

		if(passwordToCheck == null)
			return false;

		String alg = passwordEncryptionAlgorithmForMatching(user);
		
		boolean result = false;

        String decryptedPassword = passwordCache.get(user.getId());
        if (decryptedPassword==null) {
            if(alg.equals(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN)) {
                // New symmetric encryption algorithm
                if(logger.isTraceEnabled())
                    logger.trace("Checking password for user '" + user.getName() + "' using symmetric algorithm '" + alg + "'");
                // Since encryption of the same password value doesn't necessarily result in the same encrypted value,
                // we must decrypt the user's stored password and compare it with the user entered value.
                decryptedPassword = getStringEncryptor_second_gen().decrypt(user.getPassword());
                passwordCache.put(user.getId(), decryptedPassword);
            }
            else if(alg.equals(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_FIRST_GEN)) {
                // Old symmetric encryption algorithm
                if(logger.isTraceEnabled())
                    logger.trace("Checking password for user '" + user.getName() + "' using symmetric algorithm '" + alg + "'");
                decryptedPassword = getStringEncryptor_first_gen().decrypt(user.getPassword());
                passwordCache.put(user.getId(), decryptedPassword);
            }
            else {
                // Asymmetric encryption algorithm
                for(String asymAlg : ASYMMETRIC_ENCRYPTION_ALGORITHMS) {
                    if(alg.equals(asymAlg)) {
                        if(logger.isTraceEnabled())
                            logger.trace("Checking password for user " + user.getName() + " using asymmetric algorithm " + alg);
                        // Apply the same algorithm to the user entered value and compare it with the user's
                        // stored (encrypted) password.
                        return PasswordHashEncryptor.encrypt(alg, passwordToCheck).equals(user.getPassword());
                    }
                }
            }
        }
        if (decryptedPassword!=null) {
            result = passwordToCheck.equals(decryptedPassword);
        }

		end(startTime, "checkPassword");

		return result;
	}
	
	/*
	public static String encryptPasswordForMatching(String passwordToMatch, User user) {
		String alg = passwordEncryptionAlgorithmForMatching(user);
		return encryptPassword(alg, passwordToMatch, user.getName());
	}
	*/
	
	public static String encryptPasswordForStorage(String passwordToStore, User user) {
		long startTime = System.nanoTime();

		// This is the time to individualize the password encryption algorithm for the user if it hasn't happened yet. 
		String result = encryptPassword(passwordEncryptionAlgorithmForStorage(user), passwordToStore, user.getName());
		
		end(startTime, "encryptPasswordForStorage");
		
		return result;
	}
	
	public static String encryptPasswordForStorage(String passwordToStore, ProxyIdentity pi) {
		long startTime = System.nanoTime();

		// This is the time to individualize the password encryption algorithm for the user if it hasn't happened yet. 
		String result = encryptPassword(passwordEncryptionAlgorithmForStorage(pi), passwordToStore, pi.getProxyName());
		
		end(startTime, "encryptPasswordForStorage");
		
		return result;
	}
	
	public static String passwordEncryptionAlgorithmForStorage(User user) {
		return PASSWORD_ENCRYPTION_ALGORITHM_AFTER_INDIVIDUALIZATION;
	}
	
	public static String passwordEncryptionAlgorithmForStorage(ProxyIdentity pi) {
		return PASSWORD_ENCRYPTION_ALGORITHM_AFTER_INDIVIDUALIZATION;
	}
	
	/**
	 * If the user's password is stored using symmetric encryption (i.e., reversible), 
	 * returns decrypted password. If the user doesn't have a password or the password
	 * is stored using asymmetric encryption, it returns <code>null</code>.
	 *  
	 * @param user
	 * @return
	 */
	public static String decryptPasswordForMatching(User user) {
		long startTime = System.nanoTime();

		if(user.getPassword() == null) {
			if(logger.isTraceEnabled())
				logger.trace("There is no password for user " + user.getName());
			return null;
		}
		
		String result = passwordCache.get(user.getId());
        if (result==null) {
            String alg = passwordEncryptionAlgorithmForMatching(user);
            if(alg.equals(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN)) {
                if(logger.isTraceEnabled())
                    logger.trace("Decrypting password for user '" + user.getName() + "' using symmetric algorithm '" + alg + "'");
                result = getStringEncryptor_second_gen().decrypt(user.getPassword());
            }
            else if(alg.equals(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_FIRST_GEN)) {
                if(logger.isTraceEnabled())
                    logger.trace("Decrypting password for user '" + user.getName() + "' using symmetric algorithm '" + alg + "'");
                result = getStringEncryptor_first_gen().decrypt(user.getPassword());
            }
            else {
                if(logger.isTraceEnabled())
                    logger.trace("Cannot decrypt password for user '" + user.getName() + "' since it uses asymmetric algorithm '" + alg + "'");
                result = null;
            }
            if (result!=null) {
                passwordCache.put(user.getId(), result);
            }
        }
		
		end(startTime, "decryptPasswordForMatching");
		
		return result;
	}
	
	public static String passwordEncryptionAlgorithmForMatching(User user) {
		String alg = user.getPwdenc();
		if(alg == null) {
			// The password encryption algorithm hasn't been individualized for that user yet. Use the global one. 
			alg = PASSWORD_ENCRYPTION_ALGORITHM_BEFORE_INDIVIDUALIZATION;
		}
		return alg;
	}
	
	static String encryptPassword(String alg, String password, String username) {
		if(alg.equals(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_SECOND_GEN)) {
			if(logger.isTraceEnabled())
				logger.trace("Encrypting password for user '" + username + "' using symmetric algorithm '" + alg + "'");
			return getStringEncryptor_second_gen().encrypt(password);
		}
		else if(alg.equals(ExtendedPBEStringEncryptor.SYMMETRIC_ENCRYPTION_ALGORITHM_FIRST_GEN)) {
			if(logger.isTraceEnabled())
				logger.trace("Encrypting password for user '" + username + "' using symmetric algorithm '" + alg + "'");
			return getStringEncryptor_first_gen().encrypt(password);
		}
		else {
			for(String asymAlg : ASYMMETRIC_ENCRYPTION_ALGORITHMS) {
				if(alg.equals(asymAlg)) {
					if(logger.isTraceEnabled())
						logger.trace("Encrypting password for user " + username + " using asymmetric algorithm " + alg);
					return PasswordHashEncryptor.encrypt(alg, password);
				}
			}
			throw new ConfigurationException("Invalid password encryption algorithm '" + alg + "'");
		}
	}
	
	public static StringEncryptor getStringEncryptor_second_gen() {
		ExtendedPBEStringEncryptor encryptor = (ExtendedPBEStringEncryptor) SpringContextUtil.getBean("symmetricStringEncryptor");
		if(encryptor.getGeneration() == 2)
			return encryptor;
		else
			throw new ConfigurationException("Second generation encryptor is not configured with the system");
	}

	static StringEncryptor getStringEncryptor_first_gen() {
		return (StringEncryptor) SpringContextUtil.getBean("symmetricStringEncryptor_firstGen");
	}

	private static void end(long beginInNanoseconds, String message) {
		if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - beginInNanoseconds)/1000000.0;
			logger.debug("{" + diff + " ms} " + message);
		}
	}

}
