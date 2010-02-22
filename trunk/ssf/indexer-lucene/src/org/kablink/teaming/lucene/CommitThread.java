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
package org.kablink.teaming.lucene;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.util.PropsUtil;

public class CommitThread extends Thread {

	private Log logger = LogFactory.getLog(getClass());

	// access protected by "this"
    private boolean stop = false;
    
    private String indexName;
    private LuceneProvider luceneProvider;
    
    private int commitNumberOps;
    private long commitTimeInterval; // in milli second

    public CommitThread(String indexName, LuceneProvider luceneProvider) {
    	super("CT-" + indexName);
    	this.indexName = indexName;
    	this.luceneProvider = luceneProvider;
    	
		this.commitNumberOps = PropsUtil.getInt("lucene.index.commit.number.ops", 1000);
		long interval = PropsUtil.getInt("lucene.index.commit.time.interval", 3600);
		this.commitTimeInterval = interval * 1000;
    	
    	logger.info("Commit thread instantiated: commitNumberDocs=" + commitNumberOps + ", commitTimeInterval=" + interval);
    }
    
    public void run() {
    	long timeout;
    	LuceneProvider.CommitStat commitStat;
    	while(true) {
    		synchronized(this) {
    			commitStat = luceneProvider.getCommitStat();
    			while(!stop && !weGotWorkToDo(commitStat)) {
    				try {
    					timeout = timeoutTilNextCommit(commitStat);
    					if(logger.isDebugEnabled())
    						logger.debug("About to call wait() with timeout=" + timeout);
						wait(timeout);
						if(logger.isDebugEnabled())
							logger.debug("Returned from wait()");
					} catch (InterruptedException e) {
						logger.warn("This shouldn't happen", e);
					}
					commitStat = luceneProvider.getCommitStat();
    			}
    			if(stop) {
    				logger.info("Stop request in place. Exiting commit thread.");
    				return;
    			}
    		}
    		try {
    			if(logger.isDebugEnabled())
    				logger.debug("Invoking commit on lucene provider");
    			luceneProvider.commit();
    		}
    		catch(Exception e) {
    			logger.error("Error invoking commit. This requires immediate attention of administrator", e);
    		}
    	}
    }
    
    // called with monitor locked
    private boolean weGotWorkToDo(LuceneProvider.CommitStat commitStat) {
    	boolean result = false;
    	if(commitStat.getNumberOfOpsSinceLastCommit() >= commitNumberOps) {
    		// We have enough docs to commit.
    		result = true;
    	}
    	else if(commitStat.getNumberOfOpsSinceLastCommit() > 0) {
    		// We have potentially "some" change since the beginning of last commit.
    		// This does not necessarily mean that we really do have some uncommitted change. 
    		// The way Lucene works, there is no way to determine this 100% accurately and
    		// that's not something we must have. Approximation is good enough.
    		if(System.currentTimeMillis() - commitStat.getFirstOpTimeSinceLastCommit() >= commitTimeInterval)
        		result = true;
    	}
    	if(logger.isDebugEnabled())
    		logger.debug("Called weGotWorkToDo(), result=" + result + 
    				", numberOfOpsSinceLastCommit=" + commitStat.getNumberOfOpsSinceLastCommit() + 
    				", firstOpTimeSinceLastCommit=" + commitStat.getFirstOpTimeSinceLastCommit());
    	return result;
    }
    
    // called with monitor locked
    private long timeoutTilNextCommit(LuceneProvider.CommitStat commitStat) {
    	long timeout;
    	if(commitStat.getNumberOfOpsSinceLastCommit() > 0 && commitStat.getFirstOpTimeSinceLastCommit() > 0) {
    		timeout = commitStat.getFirstOpTimeSinceLastCommit() + commitTimeInterval - System.currentTimeMillis();
    		if(timeout <= 0)
    			timeout = 1;
    	}
    	else {
    		timeout = commitTimeInterval;
    	}
    	if(logger.isDebugEnabled())
    		logger.debug("Called timeoutTilNextCommit(), result=" + timeout);
    	return timeout;
    }

    /**
     * Called when the process wants this thread to stop.
     */
    public synchronized void setStop() {
    	if(logger.isDebugEnabled())
    		logger.debug("Called setStop() on " + getName());
        stop = true;
        notifyAll();
    }

    /**
     * Called to indicate that one or more docs (adds and/or deletes) have been processed.
     */
    public synchronized void someDocsProcessed() {
    	if(logger.isTraceEnabled())
    		logger.trace("Called someDocsProcessed() on " + getName());
    	notifyAll();
    }
}
