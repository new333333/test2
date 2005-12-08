package com.sitescape.ef.search.local;

import com.sitescape.ef.search.AbstractLuceneSessionFactory;
import com.sitescape.ef.search.LuceneSession;

/**
 * @author Jong Kim
 *
 */
public class LocalLuceneSessionFactory extends AbstractLuceneSessionFactory {
    
    public LuceneSession openSession(String indexName) {
        // TODO needs to be reimplemented.

        String zoneId = "liferay.com"; // bad!!
        
        // Simply ignore index name, and use the hard-coded company name.
        // This means that the index files are all combined into one, 
        // ignoring the user-specified index name, under the same ugly
        // company. 
        return new LocalLuceneSession(zoneId);
    }
}
