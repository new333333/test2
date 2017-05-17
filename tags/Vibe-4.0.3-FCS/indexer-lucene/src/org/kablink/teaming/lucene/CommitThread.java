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
import org.kablink.teaming.lucene.LuceneProvider.IndexingResource;

public class CommitThread extends Thread {

	private Log logger = LogFactory.getLog(getClass());

	// access protected by "this"
    private boolean stop = false;
    
    private LuceneProvider luceneProvider;

    public CommitThread(String indexName, LuceneProvider luceneProvider) {
    	super("CT-" + indexName);
    	this.luceneProvider = luceneProvider;
    	
    	int commitTimeInterval = luceneProvider.getLuceneProviderManager().getCommitTimeInterval();
    	int commitNumberOps = luceneProvider.getLuceneProviderManager().getCommitNumberOps();
    	
    	logger.info("Commit thread instantiated: commitNumberDocs=" + commitNumberOps + ", commitTimeInterval=" + commitTimeInterval);
    }
    
    public void run() {
    	long timeout;
    	IndexingResource indexingResource;
    	while(true) {
    		synchronized(this) {
    			// It is crucial that we call this (to get an indexing resource) 
    			// for each iteration (as opposed to getting it once outside of
    			// the loop) because different instances can come and go over time.
    			indexingResource = luceneProvider.getIndexingResource();
    			while(!stop && !weGotWorkToDo(indexingResource.getCommitStat())) {
    				try {
    					timeout = timeoutTilNextCommit(indexingResource.getCommitStat());
    					if(logger.isDebugEnabled())
    						logger.debug("About to call wait() with timeout=" + timeout);
						wait(timeout);
						if(logger.isDebugEnabled())
							logger.debug("Returned from wait()");
					} catch (InterruptedException e) {
						logger.warn("This shouldn't happen", e);
					}
	    			indexingResource = luceneProvider.getIndexingResource();
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
    	if(commitStat.getNumberOfOpsSinceLastCommit() >= luceneProvider.getLuceneProviderManager().getCommitNumberOps()) {
    		// We have enough docs to commit.
    		result = true;
    	}
    	else if(commitStat.getNumberOfOpsSinceLastCommit() > 0) {
    		// We have potentially "some" change since the beginning of last commit.
    		// This does not necessarily mean that we really do have some uncommitted change. 
    		// The way Lucene works, there is no way to determine this 100% accurately and
    		// that's not something we must have. Approximation is good enough.
    		if(System.currentTimeMillis() - commitStat.getFirstOpTimeSinceLastCommit() >= luceneProvider.getLuceneProviderManager().getCommitTimeInterval()*1000L)
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
    		timeout = commitStat.getFirstOpTimeSinceLastCommit() + luceneProvider.getLuceneProviderManager().getCommitTimeInterval()*1000L - System.currentTimeMillis();
    		if(timeout <= 0)
    			timeout = 1;
    	}
    	else {
    		timeout = luceneProvider.getLuceneProviderManager().getCommitTimeInterval()*1000L;
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
    	if(luceneProvider.getLuceneProviderManager().getCommitCheckForWorkInCallingThread()) {
        	// Instead of waking up the thread and have it check for potential work, we check
        	// for work here in calling thread and wake up the thread only if there seems to
        	// be work for the thread to do.
        	if(weGotWorkToDo(luceneProvider.getIndexingResource().getCommitStat())) {
            	if(logger.isTraceEnabled())
            		logger.trace("Called someDocsProcessed() on " + getName() + ": Notifying the thread");
        		notifyAll();
        	}
        	else {
            	if(logger.isTraceEnabled())
            		logger.trace("Called someDocsProcessed() on " + getName() + ": No need to notify the thread");    		
        	}    		
    	}
    	else {
    		// Wake up the thread so that it can check for potential work.
        	if(logger.isTraceEnabled())
        		logger.trace("Called someDocsProcessed() on " + getName() + ": Always notifying the thread");    		
        	notifyAll();    		
    	}
    }
}
