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
package org.kablink.util.encrypt;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;

import org.jasypt.encryption.StringEncryptor;

/**
 * @author jong
 *
 */
public class PropertyEncrypt extends Properties {

	// The JDK Properties class is serializable, but this class isn't safe for serialization
	// because encryptors aren't serializable and this class is of no use without them.
	// This violates the API contract. However, that should be OK because Filr uses this class
	// only for the use cases where serialization/deserialization isn't needed or used.
	// The bottom line: This is NOT a general-purpose properties implementation.
	private static final long serialVersionUID = 1L;

	// We use "ENC2(" to signal new algorithm
    private static final String PREFIX = "ENC2(";
    private static final String SUFFIX = ")";

    private static final String PREFIX_PRE_FILR_1_0 = "ENC(";
    private static final String SUFFIX_PRE_FILR_1_0 = ")";

	private StringEncryptor stringEncryptor;
	private StringEncryptor stringEncryptor_preFilr1_0;
	
	public PropertyEncrypt(Properties props, StringEncryptor stringEncryptor, StringEncryptor stringEncryptor_preFilr1_0) {
		super(props);
		this.stringEncryptor = stringEncryptor;
		this.stringEncryptor_preFilr1_0 = stringEncryptor_preFilr1_0;
	}
	
	@Override
	public String getProperty(String key) {
		return decode(super.getProperty(key));
	}

	@Override
    public String getProperty(String key, String defaultVal) {
        return decode(super.getProperty(key, defaultVal));
    }

	
	protected synchronized String decode(String encodedVal) {
		if(encodedVal == null)
			return encodedVal;
		
		encodedVal = encodedVal.trim();
		
		if(encodedVal.length() == 0)
			return encodedVal;
		
		if(isEncrypted(encodedVal)) {
			// Encrypted using new algorithm
			return stringEncryptor.decrypt(getBaseEncryptedValue(encodedVal));
		}
		else if(isEncrypted_preFilr1_0(encodedVal)) {
			// Encrypted using old algorithm
			return stringEncryptor_preFilr1_0.decrypt(getBaseEncryptedValue_preFilr1_0(encodedVal));			
		}
		else {
			// Not encrypted
			return encodedVal;
		}
	}
	
    private void writeObject(final ObjectOutputStream outputStream) throws IOException {
    	throw new UnsupportedOperationException("I don't support serialization");
    }
    
	public static boolean isEncrypted(String propVal) {
		if(propVal == null)
			return false;
		propVal = propVal.trim();
		return (propVal.startsWith(PREFIX) && propVal.endsWith(SUFFIX));
	}
	
	public static boolean isEncrypted_preFilr1_0(String propVal) {
		if(propVal == null)
			return false;
		propVal = propVal.trim();
		return (propVal.startsWith(PREFIX_PRE_FILR_1_0) && propVal.endsWith(SUFFIX_PRE_FILR_1_0));		
	}
	
	public static String getBaseEncryptedValue(String encodedPropVal) {
        return encodedPropVal.substring(PREFIX.length(), (encodedPropVal.length() - SUFFIX.length()));	
	}
	
	public static String getBaseEncryptedValue_preFilr1_0(String encodedPropVal) {
        return encodedPropVal.substring(PREFIX_PRE_FILR_1_0.length(), (encodedPropVal.length() - SUFFIX_PRE_FILR_1_0.length()));	
	}
	
	public static String getDecoratedEncryptedValue(String encVal) {
		return PREFIX+ encVal + SUFFIX;
	}
	
}
