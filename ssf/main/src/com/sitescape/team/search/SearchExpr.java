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
