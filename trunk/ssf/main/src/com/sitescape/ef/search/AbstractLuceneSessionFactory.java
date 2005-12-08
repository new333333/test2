package com.sitescape.ef.search;

import com.sitescape.ef.context.request.RequestContextHolder;

/**
 *
 * @author Jong Kim
 */
public abstract class AbstractLuceneSessionFactory implements LuceneSessionFactory {

    public LuceneSession openSession() throws LuceneException {
        String indexName = RequestContextHolder.getRequestContext().getZoneName();
        return openSession(indexName);
    }
}
