package com.sitescape.team.license.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.dom4j.Document;

import com.sitescape.team.license.LicenseException;
import com.sitescape.team.license.LicenseManager;

public class NullLicenseManager implements LicenseManager {

	public void loadLicense() throws LicenseException
	{
	}
	public void validate() throws LicenseException
	{
	}
	
	public void recordUserCount(long internal, long external)
	{
	}
	
	public boolean inCompliance()
	{
		return true;
	}

	public boolean validLicense()
	{
		return true;
	}
	
	public boolean validLicense(Calendar when)
	{
		return true;
	}
	
	public boolean isAuthorizedByLicense(String featureName)
	{
			return false;
	}
	
	public Calendar getExpirationDate()
	{
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.US);
		//  Make it tomorrow, so it's always valid
		cal.add(Calendar.DATE, 1);
		return cal;
	}
	public Calendar getEffectiveDate()
	{
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.US);
		// Make it yesterday, so it's always valid
		cal.add(Calendar.DATE, -1);
		return cal;
	}

	public List<Document> getLicenses()
	{
		return null;
	}
	
	public long getRegisteredUsers()
	{
		return 0;
	}
	
	public long getExternalUsers()
	{
		return 0;
	}
}
