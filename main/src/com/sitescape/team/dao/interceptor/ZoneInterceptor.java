package com.sitescape.team.dao.interceptor;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;

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
