package com.sitescape.ef.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import com.sitescape.ef.lucene.Hits;

/**
 * The main runtime interface between application and Lucene service. 
 * <p>
 * It is not intended that implementors be threadsafe. Instead each thread
 * should obtain its own instance from a <code>LuceneSessionFactory</code>.
 * <p>
 * Important semantics regarding the API:
 * <p>
 * 1) The API does not guarantee that changes made to the index through
 * a method call is flushed out to disk immediately. This implies that
 * the subsequent call may or may not see the changes made by the 
 * previous calls even when made through the same session. 
 * If the application requires the changes to be synchronized to disk
 * immediately, it must call <code>flush</code> immediately after the
 * operation. Then it is guaranteed that the next call will see the changes.
 * <p> 
 * 2) The API says nothing about when, how often, or under what circumstances
 * the Lucene service actually flushes changes out to the persistent store
 * OTHER THAN the fact that it guarantees synchronization when the application 
 * explicitly calls <code>flush</code> method. Other details are left to
 * the service implementation.
 * <p>
 * 3) There is no client-controlled rollback semantics supported.
 * 
 * @author Jong Kim
 *
 */
public interface LuceneSession {
    /**
     * Add a document.
     * 
     * @param doc
     * @throws LuceneException
     */
    public void addDocument(Document doc) throws LuceneException;
    
    /**
     * Delete the document identified by the uid. 
     * 
     * @param uid
     * @throws LuceneException
     */
    public void deleteDocument(String uid) throws LuceneException;
    
    /**
     * Delete all documents matching the term.
     * 
     * @param term
     * @throws LuceneException
     */
    public void deleteDocuments(Term term) throws LuceneException;    
    
    /**
     * Apply the query and delete all matching documents.
     * 
     * @param query
     * @throws LuceneException
     */
    public void deleteDocuments(Query query) throws LuceneException;
    
    /**
     * Search and return the entire result set.
     * 
     * @throws LuceneException
     */
    public Hits search(Query query) throws LuceneException;
    
    /**
     * Search and return only the portion of the result specified.
     * 
     * @param query
     * @param offset
     * @param size
     * @return
     * @throws LuceneException
     */
    public Hits search(Query query, int offset, int size) throws LuceneException;
    
    /**
     * Force the <code>LuceneSession</code> to flush.
     * <p>
     * Flushing is the process of synchronizing the underlying persistent store
     * (ie, index files on disk) with persistable state held in memory.
     * 
     * @throws LuceneException
     *
     */
    public void flush() throws LuceneException;
    
    /**
     * End the <code>LuceneSession</code> by disconnecting from the Lucene
     * service and cleaning up. Note that this does NOT implicitly perform
     * <code>flush</code> operation. In other words, <code>flush</code>
     * must be invoked explicitly by the caller before closing the session
     * if that was intended. Once <code>close</code> method is called, the 
     * session object is no longer usable.
     * 
     * @throws LuceneException
     *
     */
    public void close() throws LuceneException; 
}
