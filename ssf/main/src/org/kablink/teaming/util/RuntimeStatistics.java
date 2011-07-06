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
package org.kablink.teaming.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.report.impl.ReportModuleImpl;
import org.kablink.teaming.util.cache.ClassInstanceCache;
import org.kablink.teaming.util.cache.DefinitionCache;
import org.kablink.teaming.web.servlet.listener.SessionListener.ActiveSessionCounter;

public class RuntimeStatistics implements RuntimeStatisticsMBean {

	private Log logger = LogFactory.getLog(getClass());
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#clearSimpleProfiler()
	 */
	@Override
	public void clearSimpleProfiler() {
		SimpleProfiler.clear();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#disableSimpleProfiler()
	 */
	@Override
	public void disableSimpleProfiler() {
		SimpleProfiler.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#dumpSimpleProfiler()
	 */
	@Override
	public void dumpSimpleProfilerToLog() {
		SimpleProfiler.dumpToLog();
	}

	public String dumpSimpleProfilerAsString() {
		return SimpleProfiler.dumpAsString();
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#enableSimpleProfiler()
	 */
	@Override
	public void enableSimpleProfiler() {
		SimpleProfiler.enable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#isSimpleProfilerEnabled()
	 */
	@Override
	public boolean isSimpleProfilerEnabled() {
		return SimpleProfiler.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#getLoginInfoLastDaySize()
	 */
	@Override
	public int getLoginInfoLastDaySize() {
		return ReportModuleImpl.getLoginInfoLastDaySize();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#getClassInstanceCacheSize()
	 */
	@Override
	public int getClassInstanceCacheSize() {
		return ClassInstanceCache.size();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#getDefinitionCacheSize()
	 */
	@Override
	public int getDefinitionCacheSize() {
		return DefinitionCache.size();
	}
	
	public void dumpAllToLog() {
		String str = propertiesAsString();
		logger.info(Constants.NEWLINE + str);
		this.dumpSimpleProfilerToLog();
	}

	public String dumpAllAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append(propertiesAsString()).append(Constants.NEWLINE).append(Constants.NEWLINE).append(dumpSimpleProfilerAsString());
		return sb.toString();
	}
	
	public int getWebActiveSessionCount() {
		return ActiveSessionCounter.getActiveSessionCount();
	}
	
	public int getWebPeakActiveSessionCount() {
		return ActiveSessionCounter.getPeakActiveSessionCount();
	}

	private String propertiesAsString() {
		StringBuilder sb = new StringBuilder();
		
		Method[] methods = getClass().getDeclaredMethods();
		for(Method method:methods) {
			int modifiers = method.getModifiers();
			if(!Modifier.isPublic(modifiers))
				continue;
			String methodName = method.getName();
			Class[] paramTypes = method.getParameterTypes();
			if((methodName.startsWith("get") || 
					(methodName.startsWith("is") && (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)))) && 
					paramTypes.length == 0) {
				// This is a getter.
				try {
					Object returnValue = method.invoke(this);
					if(sb.length() > 0)
						sb.append(Constants.NEWLINE);
					String propertyName = methodName.substring(methodName.startsWith("get")? 3:2);
					sb.append(propertyName).append(": ").append(returnValue.toString());
				} catch (Exception e) {/*skip this property*/}
			}
		}
		
		/*
		sb.
		append("SimpleProfilerEnabled: ").append(isSimpleProfilerEnabled()).append(Constants.NEWLINE).
		append("LoginInfoLastDaySize: " ).append(getLoginInfoLastDaySize()).append(Constants.NEWLINE).
		append("ClassInstanceCacheSize: ").append(getClassInstanceCacheSize()).append(Constants.NEWLINE).
		append("DefinitionCacheSize: ").append(getDefinitionCacheSize());
		*/
		
		return sb.toString();
	}
}
