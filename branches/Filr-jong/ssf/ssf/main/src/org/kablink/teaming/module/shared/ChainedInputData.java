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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.survey.Survey;

public class ChainedInputData implements InputDataAccessor {

	private List<InputDataAccessor> chainedAccessors;
	private Boolean fieldsOnly;
	
	public ChainedInputData() {
		chainedAccessors = new ArrayList<InputDataAccessor>();
		fieldsOnly = false;
	}
	
	public void addAccessor(InputDataAccessor inputDataAccessor) {
		chainedAccessors.add(inputDataAccessor);
	}
		
	public boolean exists(String key) {
		for(InputDataAccessor accessor : chainedAccessors)
			if(accessor.exists(key))
				return true;
		return false;
	}

	public int getCount() {
		// We assume that each member accessor contains a disjoint set of data,
		// and therefore simply sums up the individual members for total count.
		int count = 0;
		for(InputDataAccessor accessor : chainedAccessors)
			count += accessor.getCount();
		return count;
	}

	public Date getDateValue(String key) {
		Date value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getDateValue(key);
			if(value != null)
				return value;
		}
		return null;
	}

	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence) {
		Event value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getEventValue(key, hasDuration, hasRecurrence);
			if(value != null)
				return value;
		}
		return null;
	}

	public Object getSingleObject(String key) {
		Object value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getSingleObject(key);
			if(value != null)
				return value;
		}
		return null;
	}

	public String getSingleValue(String key) {
		String value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getSingleValue(key);
			if(value != null)
				return value;
		}
		return null;
	}

	public Survey getSurveyValue(String nameValue) {
		Survey value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getSurveyValue(nameValue);
			if(value != null)
				return value;
		}
		return null;
	}

	public Description getDescriptionValue(String key) {
		Description value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getDescriptionValue(key);
			if(value != null)
				return value;
		}
		return null;
	}

	public String[] getValues(String key) {
		String[] value = null;
		for(InputDataAccessor accessor : chainedAccessors) {
			value = accessor.getValues(key);
			if(value != null)
				return value;
		}
		return null;
	}

	public void setFieldsOnly(Boolean fieldsOnly) {
		this.fieldsOnly = fieldsOnly;
	}
	public boolean isFieldsOnly() {
		return this.fieldsOnly;
	}
	
}
