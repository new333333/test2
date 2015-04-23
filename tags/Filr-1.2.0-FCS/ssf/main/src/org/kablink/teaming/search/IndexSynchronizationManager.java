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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.util.search.Constants;


/**
 * Index synchronization interface for searchable objects.
 * 
 * @author Jong Kim
 *
 */
public class IndexSynchronizationManager {
	private static final Log logger = LogFactory.getLog(IndexSynchronizationManager.class);

	private static final ThreadLocal requests = new ThreadLocal();
	
	private static final ThreadLocal nodeNamesTL = new ThreadLocal();
	
	private static final ThreadLocal forceSequentialTL = new ThreadLocal();

    private LuceneSessionFactory luceneSessionFactory;
    
    private static IndexSynchronizationManager indexSynchronizationManager; // Singleton!
    
    public IndexSynchronizationManager() {
        if(indexSynchronizationManager != null)
            throw new SingletonViolationException(IndexSynchronizationManager.class);
        
        indexSynchronizationManager = this;
    }
        
    public static IndexSynchronizationManager getInstance() {
        return indexSynchronizationManager;
    }
    
    public void setLuceneSessionFactory(LuceneSessionFactory luceneSessionFactory) {
        this.luceneSessionFactory = luceneSessionFactory;
    }
    protected LuceneSessionFactory getLuceneSessionFactory() {
        return this.luceneSessionFactory;
    }
    
    /**
     * Application calls this method to add a document.
     * 
     * @param doc
     * @throws LuceneException
     */
    public static void addDocument(Document doc) throws LuceneException {
    	if(logger.isDebugEnabled())
    		logger.debug("addDocument(" + doc.toString() + ")");
    	
        BasicIndexUtils.validateDocument(doc);
        getRequests().addAddRequest(doc);
    }
    
    /**
     * Application calls this method to delete a document with the uid.
     * 
     * @param uid
     */
    public static void deleteDocument(String uid) {
    	if(logger.isDebugEnabled())
    		logger.debug("deleteDocument(" + uid + ")");
    	
    	deleteDocuments(new Term(Constants.UID_FIELD, uid));
    }
    
    /**
     * Application calls this method to delete all document matching the term.
     * 
     * @param term
     */
    public static void deleteDocuments(Term term) {
    	if(logger.isDebugEnabled())
    		logger.debug("deleteDocuments(" + term.toString() + ")");
    		
    	getRequests().addDeleteRequest(term);
    }
        
    public static void setNodeNames(String[] nodeNames) {
    	if(logger.isDebugEnabled()) {
    		if(nodeNames != null && nodeNames.length > 0)
    			logger.debug("setNodeNames(" + nodeNames[0] + "...)");
    		else
    			logger.debug("setNodeNames()");
    	}
    	
    	nodeNamesTL.set(nodeNames);
    }
    
    public static void clearNodeNames() {
    	if(logger.isDebugEnabled())
    		logger.debug("clearNodeNames()");
    	
    	nodeNamesTL.set(null);
    }
    
    public static void setForceSequential() {
    	if(logger.isDebugEnabled())
    		logger.debug("setForceSequential()");
    	
    	forceSequentialTL.set(Boolean.TRUE);
    }
    
    public static boolean isForceSequential() {
    	if(logger.isTraceEnabled())
    		logger.trace("isForceSequential()");
    	
    	return Boolean.TRUE.equals(forceSequentialTL.get());
    }
    
    public static void clearForceSequential() {
    	if(logger.isDebugEnabled())
    		logger.debug("clearForceSequential()");
    	
    	forceSequentialTL.set(null);
    }
    
    public static void begin() {
    	if(logger.isTraceEnabled())
    		logger.trace("begin()");
    	
        // If same thread was never re-used for another request or transaction
        // in the system, this initialization wouldn't be necessary, since 
        // we would be working with fresh new thread local object. 
        // However, because containers tend to re-use threads (thread pooling),
        // we must ensure that the thread context is clear when we start a
        // new session for index synchronization.
    	// 
    	// 03/21/2013 Update: It turns out this is more harmful than useful.
    	// After many years of production, we never had a use case where we
    	// had to roll back or abandon index update requests in a particular
    	// thread context. As such, just blindly flushing everything would
    	// not have negative affect. Besides, clearing context here causes
    	// serious problem (e.g bug #807665), so we would rather NOT clear
    	// the context here.
        //clear();
    }
    
    public static void applyChanges(int threshold) {
    	if(logger.isTraceEnabled())
    		logger.trace("applyChanges(" + threshold + ")");
    	
    	if (getRequests().size() >= threshold) 
    		applyChanges();
    }

    public static int applyChanges() {
    	if(logger.isTraceEnabled())
    		logger.trace("applyChanges()");
    	
        try {
            if(hasWorkToDo()) {                
		        LuceneWriteSession luceneSession = getInstance().getLuceneSessionFactory().openWriteSession((String[]) nodeNamesTL.get());
		        
		        try {
		            return doCommit(luceneSession);
		        }
		        finally {
		            luceneSession.close();
		        }
            }
            else {
            	return 0;
            }
        }
        finally {
            clear();
        }
    }
    
    public static void discardChanges() {
    	if(logger.isDebugEnabled())
    		logger.debug("discardChanges()");
    	
    	// Discard all requests. 
    	clear(); 
    }
    
    private static Requests getRequests() {
    	Requests req = (Requests) requests.get();
    	if(req == null) {
    		req = new Requests();
    		requests.set(req);
    	}
    	return req;
    }
    
    private static boolean hasWorkToDo() {
    	Requests req = (Requests) requests.get();
    	if(req != null) {
    		if(req.size() > 0)
    			return true;
    		else
    			return false;
    	}
    	else {
    		return false;
    	}
     }
    
    private static int doCommit(LuceneWriteSession luceneSession) {   	        
        ArrayList objs = getRequests().getList();
        if(objs.size() > 0) {
        	luceneSession.addDeleteDocuments(objs);
        }        
		if(logger.isDebugEnabled())
			logger.debug("Update to index: add [" + getRequests().getAddsCount() + "], delete [" + getRequests().getDeletesCount() + "] docs");
		return objs.size();
    }
    
    private static void clear() {
    	Requests req = (Requests) requests.get();
    	if(req != null)
    		req.clear();
    }
    
    private static class Requests {
    	private ArrayList list = new ArrayList();
    	private int addsCount;
    	private int deletesCount;
    	
    	ArrayList getList() {
    		return list;
    	}
    	
    	void incrAddsCount() {
    		addsCount++;
    	}
    	
    	void incrDeletesCount() {
    		deletesCount++;
    	}
    	
    	int getAddsCount() {
    		return addsCount;
    	}
    	
    	int getDeletesCount() {
    		return deletesCount;
    	}
    	
    	void addAddRequest(Object req) {
    		getList().add(req);
    		incrAddsCount();
    	}
    	
    	void addDeleteRequest(Object req) {
    		getList().add(req);
    		incrDeletesCount();
    	}
    	
    	int size() {
    		return getList().size();
    	}
    	
    	void clear() {
    		list.clear();
    		addsCount = 0;
    		deletesCount = 0;
    	}
    }
    
}
