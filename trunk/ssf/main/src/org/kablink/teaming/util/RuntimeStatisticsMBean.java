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

public interface RuntimeStatisticsMBean {
	
	/*
	 * Returns whether simple profiler facility is enabled or not.
	 */
	public boolean isSimpleProfilerEnabled();
	
	public void setSimpleProfilerEnabled(boolean simpleProfilerEnabled);
	
	public void clearAll();
	
	/*
	 * Clear simple profiler.
	 */
	public void clearSimpleProfiler();
	
	/*
	 * Dump a snapshot of simple profiler to logger.
	 */
	public void dumpSimpleProfilerToLog();
	
	/*
	 * Dump a snapshot of simple profiler as string.
	 */
	public String dumpSimpleProfilerAsString();
	
	/*
	 * Returns the number of entries in the cache used to keep track of the last time 
	 * login info record was created for each user/authenticator combination.
	 */
	public int getLoginInfoLastDaySize();
	
	/*
	 * Returns the number of entries in the cache used to keep track of singleton
	 * instances for classes.
	 */
	public int getClassInstanceCacheSize();
	
	/*
	 * Returns the number of entries in the cache used to keep track of XML document
	 * representations of definitions.
	 */
	public int getDefinitionCacheSize();
	
	/*
	 * Dump a snapshot of all statistics to logger.
	 */
	public void dumpAllToLog();
	
	/*
	 * Dump a snapshot of all statistics as string.
	 */
	public String dumpAllAsString();
	
	/*
	 * Return number of active sessions for web interactions
	 */
	public int getWebActiveSessionCount();
	
	/*
	 * Return number of peak active sessions for web interactions
	 */
	public int getWebPeakActiveSessionCount();
	
	/*
	 * Returns whether method invocation statistics is enabled or not.
	 */
	public boolean isMethodInvocationStatisticsEnabled();
	
	public void setMethodInvocationStatisticsEnabled(boolean methodInvocationStatisticsEnabled);
		
	/*
	 * Clear method invocation statistics.
	 */
	public void clearMethodInvocationStatistics();
	
	/*
	 * Dump a snapshot of method invocation statistics to logger.
	 */
	public void dumpMethodInvocationStatisticsToLog();
	
	/*
	 * Dump a snapshot of method invocation statistics as string.
	 */
	public String dumpMethodInvocationStatisticsAsString();
	
	/*
	 * Returns whether SOAP invocation statistics is enabled or not.
	 */
	public boolean isSoapInvocationStatisticsEnabled();
	
	public void setSoapInvocationStatisticsEnabled(boolean soapInvocationStatisticsEnabled);
		
	/*
	 * Clear SOAP invocation statistics.
	 */
	public void clearSoapInvocationStatistics();
	
	/*
	 * Dump a snapshot of SOAP invocation statistics to logger.
	 */
	public void dumpSoapInvocationStatisticsToLog();
	
	/*
	 * Dump a snapshot of SOAP invocation statistics as string.
	 */
	public String dumpSoapInvocationStatisticsAsString();
	
	/*
	 * Returns whether SOAP invocation statistics is enabled or not.
	 */
	public boolean isWebdavInvocationStatisticsEnabled();
	
	public void setWebdavInvocationStatisticsEnabled(boolean webdavInvocationStatisticsEnabled);
		
	/*
	 * Clear SOAP invocation statistics.
	 */
	public void clearWebdavInvocationStatistics();
	
	/*
	 * Dump a snapshot of SOAP invocation statistics to logger.
	 */
	public void dumpWebdavInvocationStatisticsToLog();
	
	/*
	 * Dump a snapshot of SOAP invocation statistics as string.
	 */
	public String dumpWebdavInvocationStatisticsAsString();

	/*
	 * Returns whether SOAP invocation statistics is enabled or not.
	 */
	public boolean isDaoInvocationStatisticsEnabled();
	
	public void setDaoInvocationStatisticsEnabled(boolean daoInvocationStatisticsEnabled);
		
	/*
	 * Clear SOAP invocation statistics.
	 */
	public void clearDaoInvocationStatistics();
	
	/*
	 * Dump a snapshot of SOAP invocation statistics to logger.
	 */
	public void dumpDaoInvocationStatisticsToLog();
	
	/*
	 * Dump a snapshot of SOAP invocation statistics as string.
	 */
	public String dumpDaoInvocationStatisticsAsString();
	
	/*
	 * Dump information about borrowed data source connections as string.
	 */
	public String dumpDSConnectionDebugInfoAsString();
	
	/*
	 * Returns whether Net Folder Server trace is enabled or not.
	 */
	public boolean isNfsTraceEnabled();

	/*
	 * Dump a snapshot of open stream handles which is collected as part of NFS trace.
	 */
	public String dumpNfsTraceOpenStreamHandles();
}
