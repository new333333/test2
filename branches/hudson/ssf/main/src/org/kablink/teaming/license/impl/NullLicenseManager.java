/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.license.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.license.LicenseException;
import org.kablink.teaming.license.LicenseManager;


public class NullLicenseManager implements LicenseManager {

	public void loadLicense() throws LicenseException
	{
	}
	public void validate() throws LicenseException
	{
	}
	
	public void recordUserCount(long internal, long external, long active)
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
			return ((null != featureName) && featureName.equalsIgnoreCase(ObjectKeys.LICENSE_TYPE_KABLINK));
	}
	
	public boolean isAuthorizedByLicense(String featureName, boolean ignoreExpiration)
	{
			return ((null != featureName) && featureName.equalsIgnoreCase(ObjectKeys.LICENSE_TYPE_KABLINK));
	}
	
	public String getLicenseType() 
	{
		return ObjectKeys.LICENSE_TYPE_KABLINK;
	}
	
	public Calendar getExpirationDate()
	{
		GregorianCalendar cal = new GregorianCalendar(TimeZoneHelper.getTimeZone("GMT"), Locale.US);
		//  Make it tomorrow, so it's always valid
		cal.add(Calendar.DATE, 1);
		return cal;
	}
	public Calendar getEffectiveDate()
	{
		GregorianCalendar cal = new GregorianCalendar(TimeZoneHelper.getTimeZone("GMT"), Locale.US);
		// Make it yesterday, so it's always valid
		cal.add(Calendar.DATE, -1);
		return cal;
	}

	public Collection<Document> getLicenses()
	{
		return new LinkedList<Document>();
	}
	
	public long getRegisteredUsers()
	{
		return 0;
	}
	
	public long getExternalUsers()
	{
		return 0;
	}
	
	public long getInternalDevices()
	{
		return 0;
	}

	public long getExternalDevices()
	{
		return 0;
	}

	public boolean validLicenseExists()
	{
		return false;
	}
}
