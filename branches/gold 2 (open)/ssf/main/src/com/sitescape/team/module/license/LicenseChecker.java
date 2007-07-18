/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.license;

import java.util.Calendar;

import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.license.LicenseManager;

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
	 * com.sitescape.team.module.workflow.AdvancedWorkflow
	 * @return
	 */
	public static boolean isAuthorizedByLicense(String featureName) {
		return getInstance().getLicenseManager().isAuthorizedByLicense(featureName);
	}
	
	public static boolean inCompliance()
	{
		return getInstance().getLicenseManager().inCompliance();
	}
	
	public static boolean validLicense(Calendar when)
	{
		return getInstance().getLicenseManager().validLicense(when);
	}
}
