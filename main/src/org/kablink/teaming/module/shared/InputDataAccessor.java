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
import java.util.Set;

import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.survey.Survey;


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
	 * Return count of elements if the information is available. 
	 * Returns -1 if not applicable to the specific implementation of the interface.
	 * @return
	 */
	public int getCount();

	public Survey getSurveyValue(String nameValue);
	
	public Description getDescriptionValue(String key);
	
	/**
	 * Mark this as limited to fields only as specified in the definition file
	 */
	public void setFieldsOnly(Boolean fieldsOnly);
	public boolean isFieldsOnly();
}
