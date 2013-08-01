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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.search.postfilter.PostFilterCallback;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

public abstract class AbstractLuceneSession {
	
	private Log logger;
	
	private boolean inited = false;
	private long readFloor = 0; // in milliseconds
	private long writeFloor = 0; // in milliseconds
	
	protected AbstractLuceneSession(Log logger) {
		this.logger = logger;
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

	protected int adjustSearchSizeToFindOutIfThereIsMoreWhenPostFilteringInvolved(int size) {
		if(size < 0 || size == Integer.MAX_VALUE) // unbounded search
			return size;
		else // bounded search - get one more than requested
			return size + 1;
	}
	
	protected Hits setClientSideFields(Hits hits, int offset, int origSize) {
		if(origSize > 0 && origSize < Integer.MAX_VALUE) { // bounded
			if(hits.isTotalHitsApproximate()) {
				// The search total is approximate. Use look-ahead element to determine whether there's at least one more match.
				if(hits.length() > origSize) {
					hits.setLength(origSize); // Do not return the look-ahead element in this current result
					hits.setThereIsMore(true); // Indicate that there is at least one more match
				}				
			}
			else {
				// The search total is exact. In this case, there is no need for look-ahead, since whether or not there's at least 
				// one more match is easily computable from the information returned from the server.
				if(hits.getTotalHits() > offset + origSize)
					hits.setThereIsMore(true); // 
			}			
		}
		return hits;
	}
	
	protected PostFilterCallback getPostFilterCallback() {
		return new PostFilterCallback() {
			public boolean doFilter(Document doc, boolean noAclButAccessibleThroughSharing) {
				if(noAclButAccessibleThroughSharing)
					return true; // This doc represents an entry the user has access via sharing. Need not consult file system for access test.
				NumericField field = (NumericField) doc.getFieldable(Constants.ENTRY_ACL_PARENT_ID_FIELD);
				if(field == null)
					return true; // doesn't require access check
				if(field.getNumericValue().longValue() >= 0)
					return true; // doesn't require access check
				String resourceDriverName = doc.get(Constants.RESOURCE_DRIVER_NAME_FIELD);
				if(Validator.isNull(resourceDriverName)) {
					logger.warn("Can not perform access check because resource driver name is missing on this doc: " + doc.toString());
					return false; // fails the test
				}
				String resourcePath = doc.get(Constants.RESOURCE_PATH_FIELD);
				if(resourcePath == null)
					resourcePath = "";
				AclResourceSession session = org.kablink.teaming.module.shared.SearchUtils.openAclResourceSession(resourceDriverName);				
				if(session == null)
					return false; // can not perform access check on this
				try {
					session.setPath(resourcePath);
					return session.exists();
				}
				finally {
					session.close();
				}
			}
		};
	}
	
	protected Hits searchWithPostFiltering(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
			int offset, int size, PostFilterCallback callback) throws LuceneException {
		Hits hits = doSearchWithPostFiltering(contextUserId, aclQueryStr, mode, query, sort, offset, adjustSearchSizeToFindOutIfThereIsMoreWhenPostFilteringInvolved(size), callback);
		return setClientSideFields(hits, offset, size);
	}
	
	private Hits doSearchWithPostFiltering(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
			int offset, int size, PostFilterCallback callback) throws LuceneException {
		List<Document> result = new ArrayList<Document>();
		
		
		Hits hits = invokeSearchService(contextUserId, aclQueryStr, mode, query, sort, offset, size);
		
		return hits;
		
		// TODO $$$$$$$$$$$$$$$$$$$$$$$$
	}
	
	protected Hits invokeSearchService(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort, int offset, int size) throws LuceneException {
		throw new UnsupportedOperationException("Subclass must override this method");
	}
}
