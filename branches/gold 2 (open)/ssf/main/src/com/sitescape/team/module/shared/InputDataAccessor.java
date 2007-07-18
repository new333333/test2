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

public interface InputDataAccessor {

	/**
	 * Returns as a single string the value associated with the key.
	 * Returns <code>null</code> if no value exists for this key. 
	 * 
	 * @param key
	 * @return
	 */
	public String getSingleValue(String key);
	
	/**
	 * Returns as an array of string the value associated with the key.
	 * Returns <code>null</code> if no value exists for this key.
	 * 
	 * @param key
	 * @return
	 */
	public String[] getValues(String key);
	
	/**
	 * Returns as a java.util.Date the value associated with the key.
	 * Returns <code>null</code> if no value exists for this key. 
	 * 
	 * @param key
	 * @return
	 */
	public Date getDateValue(String key);
	
	/**
	 * Returns as a com.sitescape.domain.Event the value associated with the key.
	 * Returns <code>null</code> if no value exists for this key. 
	 * 
	 * @param key
	 * @return
	 */
	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence);

	/**
	 * Returns <code>true</code> if the source contains a value for the
	 * specified key.  
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key);
	
	/**
	 * Returns as a single object the value associated with the key.
	 * If there are multiple values associated with the key, this will return
	 * the first value. Returns <code>null</code> if no value exists for this key.
	 * Sort of catch-all method, which provides a hook for passing arbitrary
	 * objects that are not necessarily strings. 
	 * 
	 * value. 
	 * @param key
	 * @return
	 */
	public Object getSingleObject(String key);
	/**
	 * Return count of elements
	 * @return
	 */
	public int getCount();

	public Survey getSurveyValue(String nameValue);
		
}
