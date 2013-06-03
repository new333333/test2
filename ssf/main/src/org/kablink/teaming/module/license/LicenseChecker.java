/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.module.license;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.license.LicenseManager;

/**
 * ?
 * 
 * @author ?
 */
public class LicenseChecker {
	private static LicenseChecker instance = null;
	public LicenseChecker() {
		if(instance != null)
			throw new SingletonViolationException(LicenseChecker.class);
		
		instance = this;
	}
    private static LicenseChecker getInstance() {
    	return instance;
    }

    private LicenseManager licenseManager;
	public void setLicenseManager(LicenseManager licenseManager) {
		this.licenseManager = licenseManager;
	}
	protected LicenseManager getLicenseManager() {
		return licenseManager;
	}
	
	/**
	 * Check if the license permits the specified feature to be executed within
	 * the current runtime environment.
	 *  
	 * @param featureName name of the feature to check the licensing againt;
	 * typically feature name begins with the package name of the interface
	 * through which the particular feature is exposed followed by a short
	 * name representing the feature. For example,
	 * com.novell.teaming.module.folder.MirroredFolder
	 * @return
	 */
	public static boolean isAuthorizedByLicense(String featureName) {
		return getInstance().getLicenseManager().isAuthorizedByLicense(featureName);
	}
	
	public static boolean isAuthorizedByLicense(String featureName, boolean ignoreExpiration) {
		return getInstance().getLicenseManager().isAuthorizedByLicense(featureName, ignoreExpiration);
	}
	
	public static String getLicenseType() {
		return getInstance().getLicenseManager().getLicenseType();
	}
	
	public static boolean inCompliance()
	{
		return getInstance().getLicenseManager().inCompliance();
	}
	
	public static boolean validLicense(Calendar when)
	{
		return getInstance().getLicenseManager().validLicense(when);
	}
	
	public static boolean validLicenseExists()
	{
		return getInstance().getLicenseManager().validLicenseExists();
	}

	/**
	 * Returns true if we're running with an expired license and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseExpired() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		boolean reply = (!(validLicense(cal)));
		return reply;
	}
}
