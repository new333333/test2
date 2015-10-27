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
package org.kablink.teaming.license;

import java.util.Calendar;
import java.util.Collection;

import org.dom4j.Document;

/**
 * ?
 * 
 * @author ?
 */
public interface LicenseManager {
	/**
	 * Loads (or reloads) license information
	 *
	 */
	public void loadLicense() throws LicenseException;
	
	/**
	 * Checks a the currently loaded license for violations
	 * 
	 * @throws LicenseException
	 */
	public void validate() throws LicenseException;
	
	/**
	 * Record snapshot of number of current users, for compliance monitoring
	 * @param internal
	 * @param external
	 * @param active
	 */
	public void recordUserCount(long internal, long external, long active);
	
	/**
	 *  
	 * @return true if appropriate license is installed, and if
	 *  site is in compliance with it.
	 */
	public boolean inCompliance();

	/**
	 *  
	 * @return true if appropriate license is installed and not expired
	 */
	public boolean validLicense();

	/**
	 *  
	 * @return true if appropriate license is installed and not expired on given day
	 */
	public boolean validLicense(Calendar when);
	
	
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
	public boolean isAuthorizedByLicense(String featureName);
	public boolean isAuthorizedByLicense(String featureName, boolean ignoreExpiration);
	
	/**
	 * See what type of license this is (kablink, vibe, filr, or iprint)
	 * @return
	 */
	public String getLicenseType();
	
	/**
	 * 
	 * @return Expiration date of license, or yesterday if license is not valid
	 */
	public Calendar getExpirationDate();
	
	/**
	 * 
	 * @return Effective date of license, or tomorrow if license is not valid
	 */
	public Calendar getEffectiveDate();
	
	/**
	 * 
	 * @return License documents loaded by last loadLicense(), or null if no license found
	 */
	public Collection<Document> getLicenses();

	/**
	 * 
	 * @return Total number of registered users from all licenses
	 */
	public long getRegisteredUsers();

	/**
	 * 
	 * @return Total number of external users from all licenses
	 */
	public long getExternalUsers();

	/**
	 * 
	 * @return Total number of internal devices from all licenses
	 */
	public long getInternalDevices();

	/**
	 * 
	 * @return Total number of external devices from all licenses
	 */
	public long getExternalDevices();

	/**
	 * 
	 * @return whether there is a valid license loaded
	 */
	public boolean validLicenseExists();

	/**
	 * Returns true if this is a software version that requires a
	 * license (e.g., Novell Filr or Novell Vibe) and false otherwise
	 * (e.g., Kablink Vibe.)
	 * 
	 * @return
	 */
	public boolean licenseRequiredEdition();

	/**
	 * Licensing information required for telemetry.
	 * 
	 * @return
	 */
	public boolean isEntitled();
	public boolean isNotForResale();
	public boolean isTrial();
	public int     getTrialDays();
}
