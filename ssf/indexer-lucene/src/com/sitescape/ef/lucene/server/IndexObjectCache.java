package com.sitescape.ef.lucene.server;

import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;
/**
 * Title: IndexObjectCache
 * Description: Caches the IndexObjects
 * Copyright:    Copyright (c) 2005
 * Company: SiteScape
 * @author Roy
 * @version 1.0
 */

public class IndexObjectCache {

    private static Map indexMap;

    public IndexObjectCache() {
        indexMap = new HashMap();
    }

    public synchronized IndexObject getIndexObject (String indexName) throws RemoteException {
        IndexObject indobj = (IndexObject)indexMap.get(indexName);
        if (indobj == null) {
            indobj = new IndexObject(indexName);
            indexMap.put(indexName,indobj);
        }
        return indobj;
    }

}