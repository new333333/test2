/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import org.apache.commons.logging.Log;

import org.kablink.teaming.util.RuntimeStatistics;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * Class used within the GWT server code to dump profiling information
 * to the system log.
 * 
 * @author drfoster@novell.com
 */
public class GwtServerProfiler {
	private boolean m_debugEnabled;	// true -> m_debugLogger has debugging enabled.  false -> It doesn't.
	private Log     m_debugLogger;	// The Log that profiling information is written to.
	private long    m_debugBegin;	// If m_debugEnabled is true, contains the system time in MS that start() was called.
	private String  m_debugFrom;	// Contains information about where the profile is being used from.
	
	private static int	m_profilerDepth;	// Current depth of GwtServerProfiler objects.

	/*
	 * Class constructor.
	 */
	private GwtServerProfiler(Log logger) {
		// Initialize this object...
		this();
		
		// ...store the parameter...
		setDebugLogger(logger);
		
		// ...and initialize everything else.
		setDebugEnabled((null != logger) && logger.isDebugEnabled());
	}
	
	/*
	 * Constructor method.
	 * 
	 * Inhibits this class from being instantiated directly.
	 */
	private GwtServerProfiler() {
		super();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isDebugEnabled() {return m_debugEnabled;}
	public Log     getDebugLogger() {return m_debugLogger; }
	public long    getDebugBegin()  {return m_debugBegin;  }
	public String  getDebugFrom()   {return m_debugFrom;   }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDebugEnabled(boolean debugEnabled) {m_debugEnabled = debugEnabled;}
	public void setDebugLogger( Log     debugLogger)  {m_debugLogger  = debugLogger; }
	public void setDebugBegin(  long    debugBegin)   {m_debugBegin   = debugBegin;  }
	public void setDebugFrom(   String  debugFrom)    {m_debugFrom    = debugFrom;   }
	
	/**
	 * Creates a GwtServerProfiler object based on a Log and called
	 * from string.  If the logger has debugging enabled, dumps a
	 * message to the log regarding the profiling being started.
	 * 
	 * @param logger
	 * @param from
	 * 
	 * @return
	 */
	public static GwtServerProfiler start(Log logger, String from) {
		// Create the GwtServerProfiler to return...
		GwtServerProfiler reply = new GwtServerProfiler(logger);

		// ...perform any logging required...
		reply.setDebugFrom(from);
		if (reply.isDebugEnabled()) {
			reply.setDebugBegin(System.currentTimeMillis());
			reply.getDebugLogger().debug(from + ":  Starting...");
		}

		// ...interact with the SimpleProfiler as required...
		SimpleProfiler.start(reply.m_debugFrom);
		m_profilerDepth += 1;
		if (1 == m_profilerDepth) {
			RuntimeStatistics rs = ((RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics"));
			rs.setDaoInvocationStatisticsEnabled(true);
			rs.clearDaoInvocationStatistics();
		}

		// ...and return the GwtServerProfiler object.
		return reply;
	}
	
	public static GwtServerProfiler start(String from) {
		// Always use the initial form of the method.
		return start(null, from);
	}
	
	/**
	 * If the logger has debugging enabled, dumps a message
	 * to the log regarding how long an operation took.
	 * 
	 * Stops the associated SimpleProfiler.
	 * 
	 * If this is the outermost GwtServerProfiler object, stops the
	 * collection of DAO invocation statistics.
	 */
	public void stop() {
		// If we have a logger with debugging enabled...
		if (isDebugEnabled()) {
			// ...stop the logging...
			long diff = System.currentTimeMillis() - m_debugBegin;
			getDebugLogger().debug(m_debugFrom + ":  Ended in " + diff + " (ms)");
		}

		// ...and stop the SimpleProfiler.
		SimpleProfiler.stop(m_debugFrom);
		if (0 < m_profilerDepth) {
			m_profilerDepth -= 1;
			if (0 == m_profilerDepth) {
				RuntimeStatistics rs = ((RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics"));
				rs.setDaoInvocationStatisticsEnabled(false);
			}
		}
	}
}
