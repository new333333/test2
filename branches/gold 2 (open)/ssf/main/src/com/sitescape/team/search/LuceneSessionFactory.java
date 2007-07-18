/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.search;

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
