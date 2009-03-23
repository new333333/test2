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
package org.kablink.teaming.taglib;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.module.license.LicenseChecker;



public class LicenseExpiredTag extends BodyTagSupport {
	
	private Integer inThisManyDays = new Integer(0);
	
	public void setInThisManyDays(Integer inThisManyDays)
	{
		this.inThisManyDays = inThisManyDays;
	}
	
	private Boolean invert = Boolean.FALSE;
	
	public void setInvert(Boolean invert)
	{
		this.invert = invert;
	}
	public int doStartTag() throws JspTagException {
		try {
			boolean result = false;
			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.US);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, inThisManyDays.intValue());
			result = !LicenseChecker.validLicense(cal);
			if(invert.booleanValue()) {
				result = ! result;
			}
			if(result) {
				return EVAL_BODY_INCLUDE;
			}
		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		
		return EVAL_PAGE;
	}
	
	public int doAfterBody() {
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		return EVAL_BODY_INCLUDE;
	}

}
