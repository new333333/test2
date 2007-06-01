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
import java.util.Map;

import com.sitescape.team.InternalException;
import com.sitescape.team.domain.Event;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.EventHelper;

public class MapInputData implements InputDataAccessor {

	private Map source;
	
	public MapInputData(Map source) {
		this.source = source;
	}
	
	public String getSingleValue(String key) {
		Object result = source.get(key);
		if(result == null)
			return null;
		else if (result instanceof String) 
			return (String)result;
		else if (result instanceof String[]) 
			return ((String[]) result)[0];
		else
			throw new InternalException("Illegal value type [" + result.getClass() + "]");
	}
	
	public Date getDateValue(String key) {
		if(source.containsKey(key) && source.get(key) instanceof Date) {
			return (Date) source.get(key);
		}
		return DateHelper.getDateFromInput(this, key);
	}

	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
	{
		if(source.containsKey(key) && source.get(key) instanceof Event) {
			return (Event) source.get(key);
		}
		return EventHelper.getEventFromMap(this, key, hasDuration, hasRecurrence);
	}

	public String[] getValues(String key) {
		Object result = source.get(key);
		if(result == null)
			return null;
		else if (result instanceof String[]) 
			return (String[])result;
		else if(result instanceof String)
			return new String[] { (String) result };
		else {
			throw new InternalException("Illegal value type [" + result.getClass() + "]");			
		}
	}

	public boolean exists(String key) {
		return source.containsKey(key);
	}

	public Object getSingleObject(String key) {
		return source.get(key);
	}
	public int getCount() {
		return source.size();
	}

}
