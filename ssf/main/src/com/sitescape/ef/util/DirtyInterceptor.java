package com.sitescape.ef.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * HIbernate session interceptor to keep a list of dirty objects.  
 * This is used to gather the list of objects that need to be re-indexed
 * @author Janet McCann
 *
 */
public class DirtyInterceptor extends EmptyInterceptor {
	ArrayList changes = new ArrayList();
	public boolean onFlushDirty(
			Object entity, 
			Serializable id, 
			Object[] currentState, 
			Object[] previousState, 
			String[] propertyNames, 
			Type[] types) {
		//keep track of changes for indexing
		changes.add(entity);
		return false;
	}
	public List getDirtyList() {
		return changes;
	}

}
