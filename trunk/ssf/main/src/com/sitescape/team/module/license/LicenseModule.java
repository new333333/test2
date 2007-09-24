package com.sitescape.team.module.license;

import java.util.Collection;

import org.dom4j.Document;

import com.sitescape.team.license.LicenseException;

public interface LicenseModule {
	public enum LicenseOperation {
		manageLicense,
	}
	public void recordCurrentUsage();
	
	public void updateLicense() throws LicenseException;
	public void validateLicense() throws LicenseException;
	
	public Collection<Document> getLicenses();
		
	public boolean testAccess(LicenseOperation operation);

	public long getRegisteredUsers();
	public long getExternalUsers();
}
