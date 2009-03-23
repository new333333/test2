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
package org.kablink.teaming.dao.interceptor;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;


public class ZoneInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	
	public boolean onFlushDirty(Object entity, Serializable id, 
			Object[] currentState, Object[] previousState, String[] propertyNames,
			Type[] types) throws CallbackException {
		return injectZoneId(entity, id, currentState, propertyNames);
	}

	public boolean onSave(Object entity, Serializable id, Object[] state, 
			String[] propertyNames, Type[] types) throws CallbackException {
		return injectZoneId(entity, id, state, propertyNames);
	}
	
	protected boolean injectZoneId(Object entity, Serializable id, Object[] state, String[] propertyNames) throws CallbackException {
		for(int i = 0; i < propertyNames.length; i++) {
			if(propertyNames[i].equals(ObjectKeys.FIELD_ZONE)) {
				if(state[i] == null) {
					state[i] = getContextZoneId();
					return true;
				}
				else {
					return false;
				}
			}
		}
		
		return false;
		//throw new CallbackException("Domain object " + id + " is missing " + ZONE_ID_FIELD_NAME + " field");		
	}
	
	private Long getContextZoneId() throws CallbackException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if(rc != null) {
			Long zoneId = rc.getZoneId();
			if(zoneId != null)
				return zoneId;
			else
				throw new CallbackException("No zone id available in the request context");
		}
		else {
			throw new CallbackException("No request context available");
		}
	}
}
