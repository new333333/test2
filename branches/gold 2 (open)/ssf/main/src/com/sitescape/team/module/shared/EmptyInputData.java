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
package com.sitescape.team.module.shared;

import java.util.Date;

import com.sitescape.team.domain.Event;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.web.util.EventHelper;

/**
 * An implementation of <code>InputDataAccessor</code> interface
 * where input data is empty. 
 * 
 * @author jong
 *
 */
public class EmptyInputData implements InputDataAccessor{

	public String getSingleValue(String key) {
		return null;
	}

	public String[] getValues(String key) {
		return null;
	}

	public Date getDateValue(String key) {
		return null;
	}

	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
	{
		return null;
	}

	public Survey getSurveyValue(String key)
	{
		return null;
	}
	
	public boolean exists(String key) {
		return false;
	}

	public Object getSingleObject(String key) {
		return null;
	}
	public int getCount() {
		return 0;
	}
}
