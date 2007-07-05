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

import com.sitescape.team.util.SPropsUtil;

public class LicenseChecker {

	private static String releaseType;
	
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
		// TODO This is a 100% fake implementation and must be completely 
		// re-written to use the real License Manager.
		
		init();
		
		if(featureName.equals("com.sitescape.team.module.folder.MirroredFolder")) {
			return authorizedUnlessOpen();
		} else if (featureName.equals("com.sitescape.team.module.workflow.Workflow")) {
			return authorizedUnlessOpen();		
		} else {
			return false; // brain dead and stubborn
		}
	}
	
	private static void init() {
		if(releaseType == null) {
			releaseType = SPropsUtil.getString("release.type", "open");
		}
	}
	
	private static boolean authorizedUnlessOpen() {
		return !releaseType.equals("open");
	}
}
