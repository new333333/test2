package com.sitescape.team.license;

import java.util.Calendar;

import org.dom4j.Document;

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
	 */
	public void recordUserCount(long internal, long external);
	
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
	 * com.sitescape.team.module.workflow.AdvancedWorkflow
	 * @return
	 */
	public boolean isAuthorizedByLicense(String featureName);
	
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
	 * @return License document loaded by last loadLicense(), or null if no license found
	 */
	public Document getLicense();
}
