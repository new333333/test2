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
package org.kablink.teaming.util.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.ReflectHelper;

/**
 * Manages a cache of singleton object instances where cache entry is referenced by 
 * the class name. To take advantage of this facility, the class must meet this
 * criteria:<p>
 * 1. The class follows singleton concept, where a single instance of the class
 * can serve all requests.  
 * 2. However, the class must NOT be designed to depend on the enforcement of 
 * singleton pattern. In other word, the class must NOT bomb out when more than
 * one instance of the class is actually created. 
 * 3. The class instance must be safe for multi-threaded access. Consequently,
 * the instance should never use instance variables to store state information
 * on a per-invocation basis.
 *  
 * @author jong
 *
 */
public class ClassInstanceCache {

	private static Log logger = LogFactory.getLog(ClassInstanceCache.class);
	private static ConcurrentMap<String,Object> cache = new ConcurrentHashMap<String,Object>();
	
	public static Object getInstance(String className) {
		// This method knowingly ignores the automicity of check-then-act compound action,
		// because such offense, when it actually takes place, do not result in any
		// problem in the system. By loosening on the requirement a bit, we gain in 
		// efficiency.
		Object entry = cache.get(className);
		if(entry == null) {
			if(logger.isDebugEnabled())
				logger.debug("Cache miss. Instantiating [" + className + "]");
			entry = ReflectHelper.getInstance(className);
			cache.put(className, entry);
		}
		else {
			if(logger.isDebugEnabled())
				logger.debug("Cache hit [" + className + "]");
		}
		return entry;
	}
	
	public static int size() {
		return cache.size();
	}
}
