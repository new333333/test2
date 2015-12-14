/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.ehcache;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheKey;
import org.kablink.teaming.util.cache.DefinitionCache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class DefinitionCacheEventListener implements CacheEventListener {

	private static Log logger = LogFactory.getLog(DefinitionCacheEventListener.class);
	
	public DefinitionCacheEventListener() {
		if(logger.isDebugEnabled())
			logger.debug("Instantiated");
	}
	
	@Override
	public void dispose() {
		if(logger.isDebugEnabled())
			logger.debug("dispose");
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		if(logger.isDebugEnabled())
			logger.debug("notifyElementEvicted [" + getDefinitionIdForLog(element) + "]");
		DefinitionCache.invalidate(getDefinitionId(element));
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		if(logger.isDebugEnabled())
			logger.debug("notifyElementExpired [" + getDefinitionIdForLog(element) + "]");
		DefinitionCache.invalidate(getDefinitionId(element));
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		if(logger.isDebugEnabled())
			logger.debug("notifyElementPut [" + getDefinitionIdForLog(element) + "]");
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		if(logger.isDebugEnabled())
			logger.debug("notifyElementRemoved [" + getDefinitionIdForLog(element) + "]");
		DefinitionCache.invalidate(getDefinitionId(element));
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		if(logger.isDebugEnabled())
			logger.debug("notifyElementUpdated [" + getDefinitionIdForLog(element) + "]");
		DefinitionCache.invalidate(getDefinitionId(element));
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
		if(logger.isDebugEnabled())
			logger.debug("notifyRemoveAll");
		DefinitionCache.clear();
	}

    public Object clone() throws CloneNotSupportedException {
		if(logger.isDebugEnabled())
			logger.debug("clone");
    	super.clone();
    	return new DefinitionCacheEventListener();
    }
    
    private String getDefinitionId(Element element) {
    	Serializable sk = element.getKey();
    	if(sk instanceof CacheKey) {
    		org.hibernate.cache.CacheKey ck = (org.hibernate.cache.CacheKey) sk;
    		Serializable k = ck.getKey();
    		if(k instanceof String) {
    			return (String) k;
    		}
    		else {
    			return null;
    		}
    	}
    	else {
    		return null;
    	}
    }
    
    private String getDefinitionIdForLog(Element element) {
    	Serializable sk = element.getKey();
    	if(sk instanceof CacheKey) {
    		org.hibernate.cache.CacheKey ck = (org.hibernate.cache.CacheKey) sk;
    		Serializable k = ck.getKey();
    		if(k instanceof String) {
    			return (String) k;
    		}
    		else {
    			return k.getClass().getName(); // shouldn't happen
    		}
    	}
    	else {
    		return sk.getClass().getName(); // shouldn't happen
    	}
    }
    
}
