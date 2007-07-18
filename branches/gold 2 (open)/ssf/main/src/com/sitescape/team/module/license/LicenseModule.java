package com.sitescape.team.module.license;

import java.util.Map;

import org.dom4j.Document;

import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.license.LicenseException;

public interface LicenseModule {
	public enum LicenseOperation {
		manageLicense,
	}
	public void recordCurrentUsage();
	
	public void updateLicense() throws LicenseException;
	public void validateLicense() throws LicenseException;
	
	public Document getLicense();
		
	public boolean testAccess(LicenseOperation operation);
}
