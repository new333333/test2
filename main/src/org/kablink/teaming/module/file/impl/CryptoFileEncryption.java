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
package org.kablink.teaming.module.file.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.kablink.teaming.module.file.FileEncryption;

public class CryptoFileEncryption implements FileEncryption {
    protected Cipher ecipher;
    protected Cipher dcipher;
    protected KeyGenerator kgen;
    protected SecretKey key;
    private String SALT = "The Secret Salt";
    private boolean initialized = false;


    public CryptoFileEncryption() {
		//Get the key to use when encrypting and decrypting
    	try {
    		kgen = KeyGenerator.getInstance("AES");
	    	kgen.init(128); 
    	} catch(NoSuchAlgorithmException e) {
    		return;
    	} catch(InvalidParameterException e) {
        	return;
    	}
    	key = kgen.generateKey();
    }
    public CryptoFileEncryption(byte[] raw) {
    	SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    	initCipher(skeySpec);
    }
    
    public CryptoFileEncryption(String password) {
    	byte[] key;
		try {
			key = (SALT + password).getBytes("UTF-8");
	    	MessageDigest sha = MessageDigest.getInstance("SHA-1");
	    	key = sha.digest(key);
	    	key = Arrays.copyOf(key, 16); // use only first 128 bit
		} catch (UnsupportedEncodingException e) {
			return;
		} catch (NoSuchAlgorithmException e) {
			return;
		}
    	SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
    	initCipher(skeySpec);
    }
    
    private void initCipher(SecretKeySpec skeySpec) {
        try {
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            dcipher.init(Cipher.DECRYPT_MODE, skeySpec);
            initialized = true;
        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        }
    }
    
    public SecretKey getSecretKey() {
    	return key;
    }
    
    public boolean isInitialized() {
    	return initialized;
    }

    public InputStream getEncryptionInputEncryptedStream(InputStream in) {
    	// Bytes read from in will be encrypted
		return new CipherInputStream(in, ecipher);
	}
	
    public InputStream getEncryptionInputDecryptedStream(InputStream in) {
    	// Bytes read from in will be decrypted
		return new CipherInputStream(in, dcipher);
	}
	
	public OutputStream getEncryptionOutputEncryptedStream(OutputStream out) {
        // Bytes written to out will be encrypted
		return new CipherOutputStream(out, ecipher);
	}
	
	public OutputStream getEncryptionOutputDecryptedStream(OutputStream out) {
        // Bytes written to out will be encrypted
		return new CipherOutputStream(out, dcipher);
	}
}
