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
package org.kablink.teaming.search;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.util.SPropsUtil;

public abstract class AbstractLuceneSession implements LuceneSession {
	
	protected Log logger;
	
	private boolean inited = false;
	private long readFloor = 0; // in milliseconds
	private long writeFloor = 0; // in milliseconds
	
	protected AbstractLuceneSession(Log logger) {
		this.logger = logger;
	}
	
	// Used for read
	/*
	protected void endRead(long begin, String methodName, Long contextUserId, String aclQueryStr, 
			int mode, Query query, Sort sort, int offset, int size, int resultLength,
			int filterSuccessCount, int filterFailureCount, int serviceCallCount) {
		init();
		if(logger.isTraceEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.trace(diff + " ms, " + methodName + ", result=" + resultLength + 
						", contextUserId=" + contextUserId + 
						", aclQueryStr=[" + aclQueryStr + 
						"], mode=" + mode + 
						", query=[" + ((query==null)? "" : query.toString()) + 
						"], sort=[" + ((sort==null)? "" : sort.toString()) + 
						"], offset=" + offset +
						", size=" + size +
						", filterSuccessCount=" + filterSuccessCount +
						", filterFailureCount=" + filterFailureCount + 
						", serviceCallCount=" + serviceCallCount);
		}
		else if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.debug(diff + " ms, " + methodName + ", result=" + resultLength + 					
						", offset=" + offset +
						", size=" + size +
						", filterSuccessCount=" + filterSuccessCount +
						", filterFailureCount=" + filterFailureCount + 
						", serviceCallCount=" + serviceCallCount);
		}
	}*/

	// Used for read
	protected void endRead(long begin, String methodName, Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, 
			int mode, Query query, Sort sort, int offset, int size, int resultLength,
			int filterSuccessCount, int filterFailureCount, int serviceCallCount) {
		init();
		if(logger.isTraceEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.trace(diff + " ms, " + methodName + ", result=" + resultLength + 
						", contextUserId=" + contextUserId + 
						", baseAclQueryStr=[" + baseAclQueryStr + 
						", extendedAclQueryStr=[" + extendedAclQueryStr + 
						"], mode=" + mode + 
						", query=[" + ((query==null)? "" : query.toString()) + 
						"], sort=[" + ((sort==null)? "" : sort.toString()) + 
						"], offset=" + offset +
						", size=" + size +
						", filterSuccessCount=" + filterSuccessCount +
						", filterFailureCount=" + filterFailureCount + 
						", serviceCallCount=" + serviceCallCount);
		}
		else if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.debug(diff + " ms, " + methodName + ", result=" + resultLength + 					
						", offset=" + offset +
						", size=" + size +
						", filterSuccessCount=" + filterSuccessCount +
						", filterFailureCount=" + filterFailureCount + 
						", serviceCallCount=" + serviceCallCount);
		}
	}

	// Used for read
	protected void endRead(long begin, String methodName, List input, Map output) {
		init();
		if(logger.isTraceEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.trace(diff + " ms, " + methodName + ", input=" + input + ", output=" + output);			
		}
		else if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.debug(diff + " ms, " + methodName);
		}
	}
	
	// Used for read
	protected void endRead(long begin, String methodName, Query query, String result) {
		init();
		if(logger.isTraceEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.trace(diff + " ms, " + methodName + ", result=" + result + ", query=[" + query.toString() + "]");			
		}
		else if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.debug(diff + " ms, " + methodName + ", result=" + result);
		}
	}

	// Used for read
	protected void endRead(long begin, String methodName, Query query, int length) {
		endRead(begin, methodName, query, String.valueOf(length));
	}

	// Used for read
	protected void endRead(long begin, String methodName, String aclQueryStr, String result) {
		init();
		if(logger.isTraceEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.trace(diff + " ms, " + methodName + ", result=" + result + ", aclQuery=[" + aclQueryStr + "]");			
		}
		else if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.debug(diff + " ms, " + methodName + ", result=" + result);
		}
	}

	// Used for read
	protected void endRead(long begin, String methodName, String aclQueryStr, int length) {
		endRead(begin, methodName, aclQueryStr, String.valueOf(length));
	}

	// Used for read
	protected void endRead(long begin, String methodName, String aclQueryStr, String binderPath, boolean result) {
		init();
		if(logger.isTraceEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.trace(diff + " ms, " + methodName + ", result=" + result + ", binderPath=[" + binderPath + "], aclQuery=[" + aclQueryStr + "]");			
		}
		else if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) readFloor)
				logger.debug(diff + " ms, " + methodName + ", result=" + result);
		}
	}

	// Used for write
	protected void endWrite(long begin, String methodName) {
		init();
		if(logger.isDebugEnabled()) {
			double diff = (System.nanoTime() - begin)/1000000.0;
			if(diff >= (double) writeFloor)
				logger.debug(diff + " ms, " + methodName);
		}
	}

	private void init() {
		if(!inited) {
			readFloor = SPropsUtil.getLong("debug.lucene.read.ms.floor", 0L);
			writeFloor = SPropsUtil.getLong("debug.lucene.write.ms.floor", 0L);
			inited = true;
		}
	}

}
