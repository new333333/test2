package com.sitescape.team.license;

import com.sitescape.team.exception.CheckedCodedException;

public class LicenseException extends CheckedCodedException {
	
	public static final String BAD_VALUE = "license.exception.bad.date";
	public static final String NO_FILE = "license.exception.no.file";
	public static final String FILE_FORMAT = "license.exception.file.format";
	public static final String BAD_SIGNATURE = "license.exception.bad.signature";
	public static final String CONFLICT = "license.exception.conflict";
	
	public LicenseException(String cause)
	{
		super(cause);
	}
	public LicenseException(String cause, String field)
	{
		super(cause, new Object[] {field});
	}
	
}
