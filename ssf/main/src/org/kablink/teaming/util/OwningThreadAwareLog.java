/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.util;

import org.apache.commons.logging.Log;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;

/**
 * A log wrapper that is aware of the information stored in the request context
 * about the owning/parent thread, and use that information in the message output.
 * 
 * @author Jong
 *
 */
public class OwningThreadAwareLog implements Log {
	
	private Log realLog; // real logger wrapped by this object
	
	public OwningThreadAwareLog(Log realLog) {
		this.realLog = realLog;
	}

	public boolean isDebugEnabled() {
		return realLog.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return realLog.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return realLog.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return realLog.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return realLog.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return realLog.isWarnEnabled();
	}

	public void trace(Object message) {
		realLog.trace(owningThreadInfo() + message);
	}

	public void trace(Object message, Throwable t) {
		realLog.trace(owningThreadInfo() + message, t);
	}

	public void debug(Object message) {
		realLog.debug(owningThreadInfo() + message);
	}

	public void debug(Object message, Throwable t) {
		realLog.debug(owningThreadInfo() + message, t);
	}

	public void info(Object message) {
		realLog.info(owningThreadInfo() + message);
	}

	public void info(Object message, Throwable t) {
		realLog.info(owningThreadInfo() + message, t);
	}

	public void warn(Object message) {
		realLog.warn(owningThreadInfo() + message);
	}

	public void warn(Object message, Throwable t) {
		realLog.warn(owningThreadInfo() + message, t);
	}

	public void error(Object message) {
		realLog.error(owningThreadInfo() + message);
	}

	public void error(Object message, Throwable t) {
		realLog.error(owningThreadInfo() + message, t);
	}

	public void fatal(Object message) {
		realLog.fatal(owningThreadInfo() + message);
	}

	public void fatal(Object message, Throwable t) {
		realLog.fatal(owningThreadInfo() + message, t);
	}

	private String owningThreadInfo() {
		String result = "";
		RequestContext rc = RequestContextHolder.getRequestContext();
		if(rc != null) {
			String owningThreadName = rc.getOwningThreadName();
			if(owningThreadName != null && !owningThreadName.equals("")) {
				// Owning thread name specified
				if(!owningThreadName.equals(Thread.currentThread().getName())) {
					// Owning thread name differs from the name of the currently executing thread.
					// Include the owning thread name as part of the log message. This is to aid
					// with debugging or trouble shooting.
					result = "<" + owningThreadName + "> - ";
				}
			}
		}
		return result;
	}
}
