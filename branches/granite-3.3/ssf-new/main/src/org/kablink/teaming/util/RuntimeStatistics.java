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
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.module.report.impl.ReportModuleImpl;
import org.kablink.teaming.util.aopalliance.InvocationStatisticsInterceptor;
import org.kablink.teaming.util.aopalliance.LoggingInterceptor;
import org.kablink.teaming.util.cache.ClassInstanceCache;
import org.kablink.teaming.util.cache.DefinitionCache;
import org.kablink.teaming.web.servlet.listener.SessionListener.ActiveSessionCounter;
import org.kablink.util.EventsStatistics;
import org.kablink.util.dao.hibernate.DSConnectionProvider;
import org.springframework.beans.factory.InitializingBean;

public class RuntimeStatistics implements InitializingBean, RuntimeStatisticsMBean {

	private Log logger = LogFactory.getLog(getClass());
	
	private EventsStatistics methodInvocationStatistics = new EventsStatistics();
	private EventsStatistics soapInvocationStatistics = new EventsStatistics();
	private EventsStatistics webdavInvocationStatistics = new EventsStatistics();
	private EventsStatistics daoInvocationStatistics = new EventsStatistics();
	
	public void setInvocationStatisticsInterceptor(
			InvocationStatisticsInterceptor invocationStatisticsInterceptor) {
		invocationStatisticsInterceptor.setEventsStatistics(methodInvocationStatistics);
	}
	
	public void setWsLoggingInterceptor(LoggingInterceptor loggingInterceptor) {
		loggingInterceptor.setEventsStatistics(soapInvocationStatistics);
	}

	public void setSsfsLoggingInterceptor(LoggingInterceptor loggingInterceptor) {
		loggingInterceptor.setEventsStatistics(webdavInvocationStatistics);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		KablinkDao.setEventsStatistics(daoInvocationStatistics);
	}
	
	public void clearAll() {
		clearSimpleProfiler();
		clearMethodInvocationStatistics();
		clearSoapInvocationStatistics();
		clearWebdavInvocationStatistics();
		clearDaoInvocationStatistics();
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#clearSimpleProfiler()
	 */
	@Override
	public void clearSimpleProfiler() {
		SimpleProfiler.clear();
	}

	private void disableSimpleProfiler() {
		SimpleProfiler.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#dumpSimpleProfiler()
	 */
	@Override
	public void dumpSimpleProfilerToLog() {
		SimpleProfiler.dumpToLog(logger);
	}

	public String dumpSimpleProfilerAsString() {
		return SimpleProfiler.dumpAsString();
	}
	
	private void enableSimpleProfiler() {
		SimpleProfiler.enable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeHelperMBean#isSimpleProfilerEnabled()
	 */
	@Override
	public boolean isSimpleProfilerEnabled() {
		return SimpleProfiler.isEnabled();
	}

	@Override
	public void setSimpleProfilerEnabled(boolean simpleProfilerEnabled) {
		if(simpleProfilerEnabled)
			enableSimpleProfiler();
		else
			disableSimpleProfiler();
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
		if(logger.isInfoEnabled())
			logger.info(Constants.NEWLINE + dumpAllAsString());
	}

	public String dumpAllAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append(propertiesAsString());
		String s = dumpSimpleProfilerAsString();
		if(s.length() > 0)
			sb.append(Constants.NEWLINE).append(Constants.NEWLINE).append(s);
		sb.append(Constants.NEWLINE).append(Constants.NEWLINE).append(dumpDaoInvocationStatisticsAsString());
		sb.append(Constants.NEWLINE).append(Constants.NEWLINE).append(dumpMethodInvocationStatisticsAsString());
		sb.append(Constants.NEWLINE).append(Constants.NEWLINE).append(dumpSoapInvocationStatisticsAsString());
		sb.append(Constants.NEWLINE).append(Constants.NEWLINE).append(dumpWebdavInvocationStatisticsAsString());
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

	@Override
	public boolean isMethodInvocationStatisticsEnabled() {
		return methodInvocationStatistics.isEnabled();
	}

	@Override
	public void setMethodInvocationStatisticsEnabled(boolean methodInvocationStatisticsEnabled) {
		if(methodInvocationStatisticsEnabled)
			methodInvocationStatistics.enable();
		else
			methodInvocationStatistics.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#clearMethodInvocationStatistics()
	 */
	@Override
	public void clearMethodInvocationStatistics() {
		methodInvocationStatistics.clear();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpMethodInvocationStatisticsToLog()
	 */
	@Override
	public void dumpMethodInvocationStatisticsToLog() {
		if(logger.isInfoEnabled())
			logger.info("Method Invocation Statistics" + Constants.NEWLINE + methodInvocationStatistics.asString());
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpMethodInvocationStatisticsAsString()
	 */
	@Override
	public String dumpMethodInvocationStatisticsAsString() {
		return "Method Invocation Statistics, " + methodInvocationStatistics.asString();
	}

	@Override
	public boolean isSoapInvocationStatisticsEnabled() {
		return soapInvocationStatistics.isEnabled();
	}

	@Override
	public void setSoapInvocationStatisticsEnabled(boolean soapInvocationStatisticsEnabled) {
		if(soapInvocationStatisticsEnabled)
			soapInvocationStatistics.enable();
		else
			soapInvocationStatistics.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#clearSoapInvocationStatistics()
	 */
	@Override
	public void clearSoapInvocationStatistics() {
		soapInvocationStatistics.clear();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpSoapInvocationStatisticsToLog()
	 */
	@Override
	public void dumpSoapInvocationStatisticsToLog() {
		if(logger.isInfoEnabled())
			logger.info("SOAP Invocation Statistics" + Constants.NEWLINE + soapInvocationStatistics.asString());
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpSoapInvocationStatisticsAsString()
	 */
	@Override
	public String dumpSoapInvocationStatisticsAsString() {
		return "SOAP Invocation Statistics, " + soapInvocationStatistics.asString();
	}
	
	@Override
	public boolean isWebdavInvocationStatisticsEnabled() {
		return webdavInvocationStatistics.isEnabled();
	}

	@Override
	public void setWebdavInvocationStatisticsEnabled(boolean webdavInvocationStatisticsEnabled) {
		if(webdavInvocationStatisticsEnabled)
			webdavInvocationStatistics.enable();
		else
			webdavInvocationStatistics.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#clearWebdavInvocationStatistics()
	 */
	@Override
	public void clearWebdavInvocationStatistics() {
		webdavInvocationStatistics.clear();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpWebdavInvocationStatisticsToLog()
	 */
	@Override
	public void dumpWebdavInvocationStatisticsToLog() {
		if(logger.isInfoEnabled())
			logger.info("WebDAV Invocation Statistics" + Constants.NEWLINE + webdavInvocationStatistics.asString());
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpWebdavInvocationStatisticsAsString()
	 */
	@Override
	public String dumpWebdavInvocationStatisticsAsString() {
		return "WebDAV Invocation Statistics, " + webdavInvocationStatistics.asString();
	}

	@Override
	public boolean isDaoInvocationStatisticsEnabled() {
		return daoInvocationStatistics.isEnabled();
	}

	@Override
	public void setDaoInvocationStatisticsEnabled(boolean daoInvocationStatisticsEnabled) {
		if(daoInvocationStatisticsEnabled)
			daoInvocationStatistics.enable();
		else
			daoInvocationStatistics.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#clearDaoInvocationStatistics()
	 */
	@Override
	public void clearDaoInvocationStatistics() {
		daoInvocationStatistics.clear();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpDaoInvocationStatisticsToLog()
	 */
	@Override
	public void dumpDaoInvocationStatisticsToLog() {
		if(logger.isInfoEnabled())
			logger.info("DAO Invocation Statistics" + Constants.NEWLINE + daoInvocationStatistics.asString());
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.util.RuntimeStatisticsMBean#dumpDaoInvocationStatisticsAsString()
	 */
	@Override
	public String dumpDaoInvocationStatisticsAsString() {
		return "DAO Invocation Statistics, " + daoInvocationStatistics.asString();
	}

	@Override
	public String dumpDSConnectionDebugInfoAsString() {
		return DSConnectionProvider.debugInfoAsString();
	}

	@Override
	public boolean isNfsTraceEnabled() {
		return SPropsUtil.getBoolean("nfs.trace.enabled", false);
	}

	@Override
	public String dumpNfsTraceOpenStreamHandles() {
		return TraceableInputStreamWrapper.getOpenStreamHandlesAsString();
	}

}
