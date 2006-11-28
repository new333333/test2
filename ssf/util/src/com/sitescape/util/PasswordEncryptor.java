package com.sitescape.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sitescape.ef.InternalException;

/**
 * Miscellaneous utilities.
 */
public class PasswordEncryptor {
	
    private static Long PASSWORD_DIGEST=new Long(32958);

    public static String encrypt(String password) {
    	return encrypt(password, PASSWORD_DIGEST);
    }
    
	public static String encrypt(String password, Long digestSeed) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(password.getBytes("UTF-8"));
			algorithm.update(digestSeed.toString().getBytes("UTF-8"));
			byte[] messageDigest = algorithm.digest();
			
			StringBuffer hexString = new StringBuffer();
			for(int i = 0; i < messageDigest.length; i++) {
				// Convert each digest byte value to hex string (which is either
				// one or two characters long). 
				hexString.append(Integer.toHexString(0xff & messageDigest[i]));
			}
			return hexString.toString();
		}
		catch(NoSuchAlgorithmException e) {
			throw new InternalException(e);
		}
		catch(UnsupportedEncodingException e) {
			throw new InternalException(e);			
		}
	}
}
