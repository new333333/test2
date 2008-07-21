/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

import com.sitescape.team.SingletonViolationException;

/**
 * Index synchronization interface for searchable objects.
 * 
 * @author Jong Kim
 *
 */
public class IndexSynchronizationManager {
	private static final Log logger = LogFactory.getLog(IndexSynchronizationManager.class);

	private static final ThreadLocal requests = new ThreadLocal();
	
	private static final ThreadLocal autoFlushTL = new ThreadLocal();

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
        BasicIndexUtils.validateDocument(doc);
        getRequests().add(new Request(doc, Request.TYPE_ADD));
    }
    
    /**
     * Application calls this method to delete a document with the uid.
     * 
     * @param uid
     */
    public static void deleteDocument(String uid) {
        getRequests().add(new Request(uid, Request.TYPE_DELETE));
    }
    
    /**
     * Application calls this method to delete all document matching the term.
     * 
     * @param term
     */
    public static void deleteDocuments(Term term) {
        getRequests().add(new Request(term, Request.TYPE_DELETE));
    }
    
    /**
     * Application calls this method to delete all document matching the query.
     * 
     * @param query
     */
    public static void deleteDocuments(Query query) {
        getRequests().add(new Request(query, Request.TYPE_DELETE));
    } 
    
    /**
     * Application calls this method to change the auto flush mode associated
     * with the current session. If set to <code>true</code>, the index
     * update is made stable (i.e., flushed out to persistent storage) at the
     * end of commit, and subsequent searches will see the changes
     * immediately. Otherwise, the change may not be available for search
     * immediately. The default value is <code>false</code>, and it is highly
     * recommended not to change this mode, unless the application requires
     * the change to be made available immediately upon the completion of the
     * session.  
     * <p>
     * Note: Setting auto flush mode to <code>true</code> does not mean that
     * the changes will be flushed out after each add or delete request.
     * It will only be flushed out at the end of the commit after all add 
     * and delete requests are sent to the indexer.   
     * 
     * @param autoFlush
     */
    public static void setAutoFlush(boolean autoFlush) {
        autoFlushTL.set(Boolean.valueOf(autoFlush));
    }
    
    public static void begin() {
        // If same thread was never re-used for another request or transaction
        // in the system, this initialization wouldn't be necessary, since 
        // we would be working with fresh new thread local object. 
        // However, because containers tend to re-use threads (thread pooling),
        // we must ensure that the thread context is clear when we start a
        // new session for index synchronization. 
        clear();
    }
    
    public static void applyChanges(int threshold) {
    	List req = getRequests();
    	if (req.size() >= threshold) applyChanges();
    }

    public static void applyChanges() {
        // Question - Should we send one document at a time to the indexer
        //            or batch them in a single request??
        
        try {
            if(hasWorkToDo()) {                
		        LuceneWriteSession luceneSession = getInstance().getLuceneSessionFactory().openWriteSession();
		        
		        try {
		            doCommit(luceneSession);
		        }
		        finally {
		            luceneSession.close();
		        }
            }
        }
        finally {
            clear();
        }
    }
    
    public static void discardChanges() {
    	// Discard all requests. 
    	clear(); 
    }
    
    private static List getRequests() {
        List list = (List) requests.get();
        if(list == null) {
            list = new ArrayList();
            requests.set(list);
        }
        return list;
    }
    
    private static boolean hasWorkToDo() {
        List list = (List) requests.get();
        if(list != null && list.size() > 0)
            return true;
        else
            return false;
    }
    
    private static void doCommit(LuceneWriteSession luceneSession) {
        
        Request request;
        Object obj;
        
        int addCount = 0;
        int deleteCount = 0;

        List<Request> list = (List) requests.get();
        List adds = new ArrayList();
        for (int i=0; i<list.size(); ++i) {
            request = list.get(i);
            obj = request.getObject();
            switch(request.getType()) {
            	case Request.TYPE_ADD: {
            		adds.add(obj);
            		addCount++;
            	    break;
            	} 
            	case Request.TYPE_DELETE: {
            		if (!adds.isEmpty()) {
            			//flush out the current adds
            			luceneSession.addDocuments(adds);
            			adds = new ArrayList();
            		}
            	       
            		if(obj instanceof String) {
            	        luceneSession.deleteDocument((String) obj);
            	    }
            	    else if(obj instanceof Term) {
                	    luceneSession.deleteDocuments((Term) obj);
            	    }
            	    else {
            	        luceneSession.deleteDocuments((Query) obj);
            	    }
        	        deleteCount++;
            	    break;
            	}
            }
        }
        //flush out remaining
        if (!adds.isEmpty()) luceneSession.addDocuments(adds);
   	        
        if(((Boolean) autoFlushTL.get()).booleanValue())
            luceneSession.flush();
        
		if(logger.isDebugEnabled())
			logger.debug("Update to index: add [" + addCount + "], delete [" + deleteCount + "] docs");
    }
    
    private static void clear() {
        List list = (List) requests.get();
        if(list != null)
            list.clear();
        autoFlushTL.set(Boolean.FALSE);
    }
    
    private static class Request {
        private static final int TYPE_ADD		= 1;
        private static final int TYPE_DELETE	= 2;
        
        private int type;
        private Object obj;
        
        private Request(Object obj, int type) {
            this.obj = obj;
            this.type = type;
        }
        private Object getObject() {
            return obj;
        }
        private int getType() {
            return type;
        }
    }
}
