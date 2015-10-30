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
package org.kablink.teaming.module.shared;

import java.util.Date;
import java.util.Map;

import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.web.util.DateHelper;
import org.kablink.teaming.web.util.EventHelper;

public class MapInputData implements InputDataAccessor {

	private Map<String,Object> source;
	private Boolean fieldsOnly;
	
	public MapInputData(Map source) {
		this.source = source;
		this.fieldsOnly = false;
	}
	
	public String getSingleValue(String key) {
		Object result = source.get(key);
		if(result == null) {
			return null;
		}
		else if (result instanceof String) {
			return (String)result;
		}
		else if (result instanceof String[]) {
			if(((String[])result).length > 0)
				return ((String[]) result)[0];
			else
				return null;
		}
		else if(result instanceof byte[]) {
			try {
				return new String((byte[])result, "UTF-8");
			} catch (Exception e) {
				return null;
			}
		}
		else {
			return null;
		}
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
	
	public Survey getSurveyValue(String key)
	{
		if(source.containsKey(key) && source.get(key) instanceof Survey) {
			return (Survey) source.get(key);
		}
		String stringValue = this.getSingleValue(key);
		if (stringValue == null || stringValue.equals("")) {
			return null;
		}
		return new Survey(stringValue);
	}

	public Description getDescriptionValue(String key) {
		if(source.containsKey(key) && source.get(key) instanceof Description) {
			return (Description) source.get(key);
		}
		String stringValue = this.getSingleValue(key);
		if (stringValue == null) {
			return null;
		}
		return new Description(stringValue);
	}

	public String[] getValues(String key) {
		Object result = source.get(key);
		if(result == null) {
			return null;
		}
		else if (result instanceof String[]) {
			return (String[])result;
		}
		else if(result instanceof String) {
			return new String[] { (String) result };
		}
		else if(result instanceof byte[]) {
			try {
				return new String[] { new String((byte[])result, "UTF-8") };
			} catch (Exception e) {
				return null;
			}
		}
		else {
			return null;		
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

	public void setFieldsOnly(Boolean fieldsOnly) {
		this.fieldsOnly = fieldsOnly;
	}
	public boolean isFieldsOnly() {
		return this.fieldsOnly;
	}
	
	/**
	 * Remove the given field from the map
	 */
	public void remove( String key )
	{
		if ( key != null && source != null && exists( key ) )
			source.remove( key );
	}

}
