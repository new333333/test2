package com.sitescape.ef.search;

import org.apache.lucene.search.Query;

/**
 * @author Jong Kim
 *
 */
public class SearchExpr {
    private String simpleText;
    private Query query;
    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }
    public String getSimpleText() {
        return simpleText;
    }
    public void setQuery(Query query) {
        this.query = query;
    }
    public String toString() {
        return simpleText;
    }
    public Query getQuery() {
        return query;
    }
}
