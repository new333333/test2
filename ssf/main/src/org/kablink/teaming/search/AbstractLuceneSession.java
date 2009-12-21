/**
 * Copyright (c) 2008-2009 Novell, Inc. All Rights Reserved. THIS WORK IS AN
 * UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL PROPRIETARY AND TRADE SECRET
 * INFORMATION OF NOVELL, INC. ACCESS TO THIS WORK IS RESTRICTED TO NOVELL,INC.
 * EMPLOYEES WHO HAVE A NEED TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE
 * OF THEIR ASSIGNMENTS AND ENTITIES OTHER THAN NOVELL, INC. WHO HAVE
 * ENTERED INTO APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE
 * USED, PRACTICED, PERFORMED COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED,
 * RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL,
 * INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 */
package org.kablink.teaming.search;

import org.apache.commons.logging.Log;
import org.apache.lucene.search.Query;
import org.kablink.teaming.util.SPropsUtil;

public class AbstractLuceneSession {
	
	private boolean inited = false;
	private long readFloor = 0;
	private long writeFloor = 0;
	
	// Used for read
	protected void endRead(Log logger, long begin, String methodName, Query query, int length) {
		init();
		if(logger.isTraceEnabled()) {
			long diff = System.currentTimeMillis() - begin;
			if(diff >= readFloor)
				logger.trace((System.currentTimeMillis()-begin) + " ms, " + methodName + ", result=" + length + ", query=[" + query.toString() + "]");			
		}
		else if(logger.isDebugEnabled()) {
			long diff = System.currentTimeMillis() - begin;
			if(diff >= readFloor)
				logger.debug((System.currentTimeMillis()-begin) + " ms, " + methodName + ", result=" + length);
		}
	}

	// Used for write
	protected void endWrite(Log logger, long begin, String methodName) {
		init();
		if(logger.isDebugEnabled()) {
			long diff = System.currentTimeMillis() - begin;
			if(diff >= writeFloor)
				logger.debug((System.currentTimeMillis()-begin) + " ms, " + methodName);
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
