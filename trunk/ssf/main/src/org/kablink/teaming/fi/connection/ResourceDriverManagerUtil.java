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
package org.kablink.teaming.fi.connection;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.util.SpringContextUtil;


public class ResourceDriverManagerUtil {

	public static List<ResourceDriver> getAllowedResourceDrivers() {
		return getResourceDriverManager().getAllowedResourceDrivers();
	}
	
	public static ResourceDriver findResourceDriver(String driverName) throws FIException {
		return getResourceDriverManager().getDriver(driverName);
	}
	
	public static ResourceDriver findResourceDriver(Long driverId) throws FIException {
		return getResourceDriverManager().getDriver(driverId);
	}
	
	public static long toStorageHashAsLong (String driverName) {
		try {
			// MD5 hash the driver name to 16 byte digest.
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(driverName.getBytes("UTF-8"));
			byte[] digest = messageDigest.digest();
			// Convert the 16 byte digest into a long value.
			BigInteger bi = new BigInteger(digest);		
			long value = bi.longValue();
			// Make sure to return negative value.
			if(value > 0L)
				value *= -1;
			return value;
		}
		catch(Exception e) {
			// This shouldn't happen.
			throw new RuntimeException(e);
		}
	}
	
	/*
	public static void main(String[] args) {
		long value;
		value = toStorageHashAsLong("rd1");
		System.out.println("rd1: " + value);
		value = toStorageHashAsLong("mf13");
		System.out.println("mf13: " + value);
		value = toStorageHashAsLong("mf16");
		System.out.println("mf16: " + value);
	}
	*/
	
	/**
	 * Returns whether this driver was instantiated from the static configuration 
	 * information read from the config files at server startup.
	 * Only Vibe legacy mirrored folder drivers are created this way.
	 * 
	 * @param driver
	 * @return
	 */
	public static boolean isStaticallyCreated(ResourceDriver driver) {
		return (driver.getConfig() == null);
	}
	
	/**
	 * Find the statically instantiated resource driver by its name hash value.
	 * This mechanism does not apply to dynamically loaded resource drivers such
	 * as Filr resource drivers.
	 * 
	 * @param nameHash
	 * @return
	 * @throws FIException
	 */
	public static ResourceDriver findStaticResourceDriverByNameHash(Long nameHash) throws FIException {
		return getResourceDriverManager().getStaticDriverByNameHash(nameHash);
	}
	
	public static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
}
