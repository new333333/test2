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

package org.kablink.teaming.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import junit.framework.TestCase;

/**
 * @author jong
 *
 */
public class EncryptUtilTests extends TestCase {

	public void testBase64DecodePassword() throws UnsupportedEncodingException {
		String base64EncodedPassword = "U2l0ZVNjYXBl";
		String base64DecodedPassword = new String(Base64.decodeBase64(base64EncodedPassword.getBytes()), "UTF-8");
		System.out.println("Base64 decoded password = '" + base64DecodedPassword + "'");
		System.out.println();
		assertEquals(base64EncodedPassword, Base64.encodeBase64String(base64DecodedPassword.getBytes("UTF-8")));
	}
	
	public void testLengthOfMultiBytePassword_PBEWithMD5AndDES() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("gangnamstyle");
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		
		System.out.println("testLengthOfMultiBytePassword_PBEWithMD5AndDES");
		String pwd = "최대한최대한";
		System.out.println("Password length=" + pwd.length() + ", Encrypted str length=" + encryptor.encrypt(pwd).length());
		System.out.println();
	}
	
	public void testLengthOfNumericPassword_PBEWithMD5AndDES() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("gangnamstyle");
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		
		System.out.println("testLengthOfNumericPassword_PBEWithMD5AndDES");
		String pwd = "";
		for(int i = 0; i < 64; i++) {
			pwd += i;
			System.out.println("Password length=" + pwd.length() + ", Encrypted str length=" + encryptor.encrypt(pwd).length());
		}
		System.out.println();
	}
	
	public void testLengthOfAsciiPassword_PBEWithMD5AndDES() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("gangnamstyle");
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		
		System.out.println("testLengthOfAsciiPassword_PBEWithMD5AndDES");
		int base=33;
		String pwd = "";
		for(int i = 0; i < 90; i++) {
			pwd += String.valueOf((char) (base+i));
			System.out.println("Password length=" + pwd.length() + ", Encrypted str length=" + encryptor.encrypt(pwd).length());
		}
		System.out.println();
	}
	
	public void testLengthOfNumericPassword_PBEWITHSHA256AND128BITAES() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setProvider(new BouncyCastleProvider());
		encryptor.setPassword("gangnamstyle");
		encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");

		System.out.println("testLengthOfNumericPassword_PBEWITHSHA256AND128BITAES");
		int base=33;
		String pwd = "";
		for(int i = 0; i < 64; i++) {
			pwd += i;
			System.out.println("Password length=" + pwd.length() + ", Encrypted str length=" + encryptor.encrypt(pwd).length());
		}
		System.out.println();
	}
	
	public void testLengthOfAsciiPassword_PBEWITHSHA256AND128BITAES() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setProvider(new BouncyCastleProvider());
		encryptor.setPassword("gangnamstyle");
		encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");

		System.out.println("testLengthOfAsciiPassword_PBEWITHSHA256AND128BITAES");
		int base=33;
		String pwd = "";
		for(int i = 0; i < 90; i++) {
			pwd += String.valueOf((char) (base+i));
			System.out.println("Password length=" + pwd.length() + ", Encrypted str length=" + encryptor.encrypt(pwd).length());
		}
		System.out.println();
	}
}
