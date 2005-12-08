package com.sitescape.ef.search.remote;

import com.sitescape.ef.search.AbstractLuceneSessionFactory;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;

/**
 *
 * @author Jong Kim
 */
public class RemoteLuceneSessionFactory extends AbstractLuceneSessionFactory {

    public LuceneSession openSession(String indexName) throws LuceneException {
        return new RemoteLuceneSession(indexName);
    }
}
