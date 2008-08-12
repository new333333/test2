/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sitescape.team.util.SPropsUtil;

/**
 * Miscellaneous utilities.
 */
public class PasswordEncryptor {
	
    private static Long PASSWORD_DIGEST=new Long(32958);

    private static final String ALGORITHM = SPropsUtil.getString("user.password.encryption.algorithm", "MD5");
    
    public static String encrypt(String password) {
    	// If algorithm is MD5, do NOT ever call encrypt(). Instead, call encryptMD5
    	// which uses slightly different (and non-standard) encoding when converting
    	// computed bytes into a hex string. This is to maintain backward compatibility
    	// with existing installations that use MD5.
    	if(ALGORITHM.equals("MD5"))
    		return encryptMD5(password, PASSWORD_DIGEST);
    	else
    		return encrypt(ALGORITHM, password, PASSWORD_DIGEST);
    }
    
	private static String encryptMD5(String password, Long digestSeed) {
		// Use this method only for MD5 encryption.
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(password.getBytes("UTF-8"));
			digest.update(digestSeed.toString().getBytes("UTF-8"));
			byte[] messageDigest = digest.digest();
			
			StringBuffer hexString = new StringBuffer();
			for(int i = 0; i < messageDigest.length; i++) {
				// Convert each digest byte value to hex string (which is either
				// one or two characters long). 
				// Note: It appears this is a non-standard encoding. Each byte
				// should have been converted into two hex digits. Due to backward
				// compatibility with existing installations, we won't (can't) 
				// correct this.
				hexString.append(Integer.toHexString(0xff & messageDigest[i]));
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
			// this will occur when the password.getBytes("UTF-8") returns a null.
			return new StringBuffer().toString();
		}
	}
    
	private static String encrypt(String algorithm, String password, Long digestSeed) {
		// Use this method for all encryptions but MD5.
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.reset();
			digest.update(password.getBytes("UTF-8"));
			digest.update(digestSeed.toString().getBytes("UTF-8"));
			byte[] messageDigest = digest.digest();
			
			StringBuffer hexString = new StringBuffer();
			for(int i = 0; i < messageDigest.length; i++) {
				// Convert each byte into two digit hex characters.
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
			// this will occur when the password.getBytes("UTF-8") returns a null.
			return new StringBuffer().toString();
		}
	}
}
