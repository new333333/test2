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

import com.sitescape.team.context.request.RequestContextHolder;

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
