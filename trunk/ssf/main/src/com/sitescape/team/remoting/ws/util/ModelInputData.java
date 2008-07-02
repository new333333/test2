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
package com.sitescape.team.remoting.ws.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.data.ParserException;

import com.sitescape.team.domain.Event;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.remoting.ws.model.CustomBooleanField;
import com.sitescape.team.remoting.ws.model.CustomDateField;
import com.sitescape.team.remoting.ws.model.DefinableEntity;
import com.sitescape.team.remoting.ws.model.CustomLongArrayField;
import com.sitescape.team.remoting.ws.model.CustomStringArrayField;
import com.sitescape.team.remoting.ws.model.CustomStringField;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.util.DateUtil;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.ObjectPropertyNotFoundException;
import com.sitescape.team.util.SpringContextUtil;

public class ModelInputData implements InputDataAccessor {

	private static Log logger = LogFactory.getLog(ModelInputData.class);
	
	private DefinableEntity entity;
	
	public ModelInputData(DefinableEntity entity) {
		this.entity = entity;
	}

	public boolean exists(String key) {
		return getSingleObject(key) != null; 
	}

	public int getCount() {
		return -1;
	}

	public Date getDateValue(String key) {
		Object obj = getSingleObject(key);
		if(obj == null)
			return null;
		else if(obj instanceof Date) {
			return (Date) obj;
		}
		else if(obj instanceof Calendar) {
			return ((Calendar) obj).getTime(); 
		}
		else if(obj instanceof String) {
			return DateUtil.parseDate((String) obj); 
		}
		else {
			return null;
		}
	}

	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence) {
		String textVal = getSingleValue(key);
		if(textVal != null) {
			List<Event> events;
			try {
				events = getIcalModule().parseEvents(new StringReader(textVal));
				return events.get(0);
			} catch (IOException e) {
				logger.warn(e.toString());
			} catch (ParserException e) {
				logger.warn(e.toString());
			}
		}
		return null;
	}

	public Object getSingleObject(String key) {
		Object obj = null;
		try {
			// Try static fields first
			return InvokeUtil.invokeGetter(entity, key);
		}
	    catch (ObjectPropertyNotFoundException e) {
	    	// Try custom/dynamic fields
	    	obj = entity.findCustomBooleanField(key);
	    	if(obj != null) 
	    		return ((CustomBooleanField)obj).getValue();
	    	
	    	obj = entity.findCustomDateField(key);
	    	if(obj != null) 
	    		return ((CustomDateField)obj).getValue();
	    	
	    	obj = entity.findCustomLongArrayField(key);
	    	if(obj != null) 
	    		return ((CustomLongArrayField)obj).getValues();
	    	
	    	obj = entity.findCustomStringArrayField(key);
	    	if(obj != null) 
	    		return ((CustomStringArrayField)obj).getValues();
	    	
	    	obj = entity.findCustomStringField(key);
	    	if(obj != null)
	    		return ((CustomStringField)obj).getValue();
	    	
	    	return null;
	    }	
	}

	public String getSingleValue(String key) {
		Object obj = getSingleObject(key);
		if(obj != null) {
			if(obj instanceof Long[])
				obj = ((Long[])obj)[0];
			else if(obj instanceof String[])
				obj = ((String[])obj)[0];
		}
		if(obj == null)
			return null;
		else if(obj instanceof String)
			return (String) obj;
		else
			return obj.toString();
	}

	public Survey getSurveyValue(String key) {
		String textVal = getSingleValue(key);
		if(textVal != null) {
			return new Survey(textVal);
		}
		return null;
	}

	public String[] getValues(String key) {
		String[] values = null;
		Object obj = getSingleObject(key);
		if(obj != null) {
			if(obj instanceof Long[]) {
				Long[] longValues = (Long[]) obj;
				values = new String[longValues.length];
				for(int i = 0; i < values.length; i++)
					values[i] = String.valueOf(longValues[i]);
			}
			else if (obj instanceof String[]) {
				values = (String[]) obj;
			}
			else if(obj instanceof String) {
				values = new String[] {(String)obj};
			}
			else {
				values = new String[] {obj.toString()};
			}
		}
		return values;
	}
	
	public IcalModule getIcalModule() {
		return (IcalModule) SpringContextUtil.getBean("icalModule");
	}
}
