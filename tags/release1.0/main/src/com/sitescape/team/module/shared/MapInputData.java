/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.shared;

import java.util.Date;
import java.util.Map;

import com.liferay.portlet.journal.action.GetStructureAction;
import com.sitescape.team.InternalException;
import com.sitescape.team.domain.Event;
import com.sitescape.team.survey.Survey;
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
	
	public Survey getSurveyValue(String key)
	{
		if(source.containsKey(key) && source.get(key) instanceof Survey) {
			return (Survey) source.get(key);
		}
		String stringValue = this.getSingleValue(key);
		if (stringValue == null || "".equals(stringValue)) {
			return null;
		}
		return new Survey(stringValue);
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
