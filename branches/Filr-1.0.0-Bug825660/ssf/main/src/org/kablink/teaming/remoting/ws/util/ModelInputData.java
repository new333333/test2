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
package org.kablink.teaming.remoting.ws.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.remoting.ws.model.CustomBooleanField;
import org.kablink.teaming.remoting.ws.model.CustomDateField;
import org.kablink.teaming.remoting.ws.model.CustomEventField;
import org.kablink.teaming.remoting.ws.model.CustomLongArrayField;
import org.kablink.teaming.remoting.ws.model.CustomStringArrayField;
import org.kablink.teaming.remoting.ws.model.CustomStringField;
import org.kablink.teaming.remoting.ws.model.DefinableEntity;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.DateUtil;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.util.StringUtil;

import net.fortuna.ical4j.data.ParserException;

public class ModelInputData implements InputDataAccessor {

	private static Log logger = LogFactory.getLog(ModelInputData.class);
	
	private DefinableEntity entity;
	private Boolean fieldsOnly;
	
	public ModelInputData(DefinableEntity entity) {
		this.entity = entity;
		this.fieldsOnly = false;
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
		if(entity.isEventAsIcalString()) {
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
		}
		else {
			Object val =  getSingleObject(key);
			if(val != null && val instanceof org.kablink.teaming.remoting.ws.model.Event) {
				org.kablink.teaming.remoting.ws.model.Event eventVal = (org.kablink.teaming.remoting.ws.model.Event) val;
				return org.kablink.teaming.remoting.ws.model.Event.toDomainModel(eventVal);
			}
		}
		return null;
	}

	public Object getSingleObject(String key) {
		Object obj = null;
		try {
			// Try static fields first
			obj = InvokeUtil.invokeGetter(entity, key);
			if (obj instanceof String) return StringCheckUtil.check((String)obj);
			if (obj instanceof String[]) {
				return doStringCheck((String[])obj);
			}
			return obj;
		}
	    catch (ObjectPropertyNotFoundException e) {
	    	// Try custom/dynamic fields
	    	obj = entity.findCustomStringField(key);
	    	if(obj != null)
	    		return StringCheckUtil.check(((CustomStringField)obj).getValue());
 
	    	obj = entity.findCustomBooleanField(key);
	    	if(obj != null) 
	    		return ((CustomBooleanField)obj).getValue();
	    	
	    	obj = entity.findCustomDateField(key);
	    	if(obj != null) 
	    		return ((CustomDateField)obj).getValue();
	    	
	    	obj = entity.findCustomEventField(key);
	    	if(obj != null) 
	    		return ((CustomEventField)obj).getValue();
	    	
	    	obj = entity.findCustomLongArrayField(key);
	    	if(obj != null) 
	    		return ((CustomLongArrayField)obj).getValues();
	    	
	    	obj = entity.findCustomStringArrayField(key);
	    	if(obj != null) {
	    		return doStringCheck(((CustomStringArrayField)obj).getValues());
	    	}
	    	
	    	// None prevailed. Let's try if interpreting the key as a graph representation can locate the data.
	    	if(key.contains(".")) {
	    		String[] keyElems = StringUtil.split(key, ".");
	    		if(keyElems != null && keyElems.length > 1) {
	    			obj = entity;
	    			for(String keyElem:keyElems) {
	    				try {
		    				obj = InvokeUtil.invokeGetter(obj, keyElem);
		    				if(obj == null)
		    					break;
	    				}
	    				catch(ObjectPropertyNotFoundException exc) {
	    					obj = null;
	    					break;
	    				}
	    			}
	    		}
	    	}
	    	
		    return obj;	    	
	    }	    	
	}

	private String[] doStringCheck(String[] strs) {
		//need new array
		String[] newVals = new String[strs.length];
    	for (int i=0; i<strs.length; ++i) {
    		newVals[i] = StringCheckUtil.check(strs[i]);
    	}
    	return newVals;
	}
	
	public String getSingleValue(String key) {
		Object obj = getSingleObject(key);
		if(obj != null) {
			if(obj instanceof Long[] && ((Long[])obj).length!=0)
				obj = ((Long[])obj)[0];
			else if(obj instanceof String[] && ((String[])obj).length!=0)
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

	public Description getDescriptionValue(String key) {
		return new Description(getSingleValue(key));
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

	public void setFieldsOnly(Boolean fieldsOnly) {
		this.fieldsOnly = fieldsOnly;
	}
	public boolean isFieldsOnly() {
		return this.fieldsOnly;
	}

}
