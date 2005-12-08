package com.sitescape.ef.search;

/**
 * Creates <code>LuceneSession</code>s.
 * <p>
 * Implementors must be threadsafe. 
 * 
 * @author Jong Kim
 *
 */
public interface LuceneSessionFactory {
    
    /**
     * Open a <code>LuceneSession</code>, using the caller's context
     * zone name as the index name. 
     * 
     * @return
     * @throws LuceneException
     */
    public LuceneSession openSession() throws LuceneException;
    
    /**
     * Open a <code>LuceneSession</code>.
     * 
     * @param indexName
     * @return
     * @throws LuceneException
     */
    public LuceneSession openSession(String indexName) throws LuceneException;
}
