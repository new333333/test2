package com.sitescape.ef.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.util.SimpleProfiler;

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
    
    /**
     * Warning: For use by framework only. Not to be called directly by application code.
     *
     */
    public static void begin() {
        // If same thread was never re-used for another request or transaction
        // in the system, this initialization wouldn't be necessary, since 
        // we would be working with fresh new thread local object. 
        // However, because containers tend to re-use threads (thread pooling),
        // we must ensure that the thread context is clear when we start a
        // new session for index synchronization. 
        clear();
    }
    
    /**
     * Warning: For use by framework only. Not to be called directly by application code.
     *
     */
    public static void applyChanges() {
        // Question - Should we send one document at a time to the indexer
        //            or batch them in a single request??
        
        try {
            if(hasWorkToDo()) {
                SimpleProfiler prof = new SimpleProfiler("Index update");
                prof.begin();
                
		        LuceneSession luceneSession = getInstance().getLuceneSessionFactory().openSession();
		        
		        try {
		            doCommit(luceneSession);
		        }
		        finally {
		            luceneSession.close();
		        }
		        
		        prof.end();
		        prof.logDebug();
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
    
    private static void doCommit(LuceneSession luceneSession) {
        
        Request request;
        Object obj;
        
        int addCount = 0;
        int deleteCount = 0;

        List list = (List) requests.get();
        for(Iterator i = ((List) requests.get()).iterator(); i.hasNext();) {
            request = (Request) i.next();
            obj = request.getObject();
            switch(request.getType()) {
            	case Request.TYPE_ADD: {
            		luceneSession.addDocument((Document) obj);
            		addCount++;
            	    break;
            	}
            	case Request.TYPE_DELETE: {
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
        
        if(((Boolean) autoFlushTL.get()).booleanValue())
            luceneSession.flush();
        
        logger.info("Update to index: add [" + addCount + "], delete [" + deleteCount + "] requests");
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
