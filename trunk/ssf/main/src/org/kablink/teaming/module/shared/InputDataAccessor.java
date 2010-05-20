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
	
	/**
	 * Mark this as limited to fields only as specified in the definition file
	 */
	public void setFieldsOnly(Boolean fieldsOnly);
	public boolean isFieldsOnly();
		
	/**
	 * Returns a set of keys whose values might be meaningfully representable 
	 * in string. Therefore it is important to note that the returned set may not 
	 * represent all the keys available in the object. Exactly what keys are
	 * in the returned set depends on the actual implementation of this interface.
	 * For example, for XML implementation of this interface, more elements are
	 * representable in string (because everything is string in XML), hence the
	 * returned set might contain more keys than typical implementations do.
	 * On the other hand, for more strongly-typed implementation of this interface,
	 * the keys representing non-String primitive type fields (eg. boolean field)
	 * may not be included in the returned set.
	 * <p>
	 * Since this method does not guarantee to return ALL keys from the accessor
	 * (as explained above), this method must NEVER be used to iterate over the
	 * entire input data set. Instead, it should only be used by an application
	 * that needs access to values of pure string type which does not necessarily
	 * have valid representation in other primitive type (for example, the boolean
	 * data type in Java are represented as "true" or "false" in string, and we
	 * are assuming that the application is not interested in having access to
	 * those values because certain level of validation was already performed
	 * by the language system. Likewise, the application is not interested in
	 * the string representation of valid numeric values). 
	 * <p>
	 * The fact that a key is included in the returned set does not necessarily
	 * mean that its associated value is meaningfully represented in string. It
	 * simply means that its value might be a string that the application is
	 * interested in. So the returned set might be more comprehensive than necessary
	 * depending on the implementation, and the value associated with the key
	 * may not actually be a string (hence the word "potential"). 
	 * <p>
	 * For the above reasons, the caller is expected to use 
	 * <code>String[] getValues(String)</code> method to retrieve the values of 
	 * each key in the returned set. If the key doesn't really represent a string
	 * (or an array of strings) value, then it will return <code>null</code>. 
	 * <p>
	 * All this complexity and seemingly-confusing semantics are to allow maximum
	 * flexibility in the implementation of this interface. Some implementations
	 * may have difficulty figuring out the types of values on its own without
	 * aid from the external entity (such as the caller itself). This relaxed
	 * semantics allows for an opportunity for self-contained implementation.
	 * <p>
	 * If there's no key meeting the criteria, it returns an empty set.
	 * 
	 */
	public Set<String> keySetForPotentialStringValues();
}
