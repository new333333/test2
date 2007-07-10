package com.sitescape.team.taglib;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.module.license.LicenseChecker;


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
