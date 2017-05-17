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
package org.kablink.teaming.module.license;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.dom4j.Document;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.license.LicenseManager;
import org.kablink.teaming.util.Utils;

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
	
	public static int getLicenseCount() {
		Collection<Document> licenses = getInstance().getLicenseManager().getLicenses();
		return ((null == licenses) ? 0 : licenses.size());
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

	/**
	 * Returns true if based on the license, Filr features should be
	 * exposed to the user and false otherwise.
	 *
	 * Note that if the license doesn't include Vibe or Filr features,
	 * the type of license is used to determine what features to show.
	 * 
	 * @return
	 */
	public static boolean showFilrFeatures() {
		// Are Filr features enabled by the license?
		boolean reply = isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_FILR);
		if (!reply) {
			// No!  Are Vibe features enabled by the license?
			if (!(isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_VIBE))) {
				// No!  Is it a Filr license?
				reply = Utils.checkIfFilr();
			}
		}
		
		// If we get here, reply is true if we should show Filr
		// features and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if based on the license, Vibe features should be
	 * exposed to the user and false otherwise.
	 *
	 * Note that if the license doesn't include Vibe or Filr features,
	 * the type of license is used to determine what features to show.
	 * 
	 * @return
	 */
	public static boolean showVibeFeatures() {
		// Are Vibe features enabled by the license?
		boolean reply = isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_VIBE);
		if (!reply) {
			// No!  Are Filr features enabled by the license?
			if (!(isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_FILR))) {
				// No!  Is it a Novell Vibe or Kablink Vibe license?
				reply = (Utils.checkIfVibe() || Utils.checkIfKablink());
			}
		}
		
		// If we get here, reply is true if we should show Vibe
		// features and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if this is a software version that requires a
	 * license (e.g., Novell Filr or Novell Vibe) and false otherwise
	 * (e.g., Kablink Vibe.)
	 * 
	 * @return
	 */
	public static boolean licenseRequiredEdition() {
		return getInstance().getLicenseManager().licenseRequiredEdition();
	}
	
	/**
	 * Returns true if an entitled license is being used and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isEntitled() {
		return getInstance().getLicenseManager().isEntitled();
	}
	
	/**
	 * Returns true if a not-for-resale license is being used and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isNotForResale() {
		return getInstance().getLicenseManager().isNotForResale();
	}
	
	/**
	 * Returns true if a trial license is being used and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isTrial() {
		return getInstance().getLicenseManager().isTrial();
	}

	/**
	 * If a trial license is being used, returns the number of days it
	 * was defined for.  Otherwise, returns -1.
	 * 
	 * @return
	 */
	public static int getTrialDays() {
		return getInstance().getLicenseManager().getTrialDays();
	}
}
