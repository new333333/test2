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
package org.kablink.teaming.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.EventsStatistics;
import org.kablink.util.dao.hibernate.DynamicDialect;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class KablinkDao extends HibernateDaoSupport {
	protected Log logger = LogFactory.getLog(getClass());
	protected boolean debugEnabled = logger.isDebugEnabled();
	
	protected int inClauseLimit=1000;

	private boolean inited = false;
	private long floor = 0; // in milliseconds
	
	private static EventsStatistics eventsStatistics; // set by external code

    /**
     * Called after bean is initialized.  
     */
	@Override
	protected void initDao() throws Exception {
		//some database limit the number of terms 
		inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
	}

	protected void end(long beginInNanoseconds, String methodName) {
		init();
		
		EventsStatistics es = eventsStatistics; // copy reference first
		if(es != null && es.isEnabled())
			es.addEvent(methodName, System.nanoTime()-beginInNanoseconds);
		
		if(debugEnabled) {
			double diff = (System.nanoTime() - beginInNanoseconds)/1000000.0;
			if(diff >= (double) floor)
				logger.debug(diff + " ms, " + methodName);
		}	
	}

	private void init() {
		if(!inited) {
			floor = SPropsUtil.getLong("debug.dao.ms.floor", 0L);
			inited = true;
		}
	}
	
	protected boolean isBinderQueryCacheable() {
		return SPropsUtil.getBoolean("binder.query.cacheable", true);
	}
	
	protected boolean isMembershipQueryCacheable() {
		return SPropsUtil.getBoolean("membership.query.cacheable", true);
	}
	
	protected boolean isReservedBinderQueryCacheable() {
		return SPropsUtil.getBoolean("reservedbinder.query.cacheable", true);
	}

	protected boolean isPrincipalQueryCacheable() {
		return SPropsUtil.getBoolean("principal.query.cacheable", true);
	}
	
	protected boolean isWorkAreaFunctionMembershipQueryCacheable() {
		return SPropsUtil.getBoolean("workAreaFunctionMembership.query.cacheable", true);
	}
	
	protected boolean isFolderEntryQueryCacheable() {
		return SPropsUtil.getBoolean("folderentry.query.cacheable", true);
	}
	
	protected boolean lookupByRange() {
		return SPropsUtil.getBoolean("case.insensitive.by.range." + DynamicDialect.getDatabaseType().name(), false);
	}

	public static void setEventsStatistics(EventsStatistics es) {
		eventsStatistics = es;
	}
}
