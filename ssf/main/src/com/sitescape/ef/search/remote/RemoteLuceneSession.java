package com.sitescape.ef.search.remote;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.lucene.SsfIndexInterface;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;


import java.rmi.*;

/**
 *
 * @author Jong Kim
 */
public class RemoteLuceneSession implements LuceneSession {

	private SsfIndexInterface index;
	private String indexName;
	
	public  RemoteLuceneSession(String indexName) throws LuceneException {
		try {
			// TEMPORARY, need to think about how to let the client
			// setup new rmi index servers. (Possibly have a generic 
			// RMI service listener that then kicks off individually named
			// index servers.
			String servicename = "rmi://localhost/SSFINDEXER";
			index = (SsfIndexInterface)Naming.lookup(servicename);
			if (index == null) {
				throw new LuceneException("Could not find index service: " + servicename);
			}
			this.indexName = indexName;
		} catch (Exception e) { throw new LuceneException(e);}
	}
	
    public void addDocument(Document doc) throws LuceneException {
	    Field uidField = doc.getField(BasicIndexUtils.UID_FIELD);
	    if(uidField == null)
	        throw new LuceneException("Document must contain a UID with field name " + BasicIndexUtils.UID_FIELD);	    
    	try {
    		index.addDocument(indexName, uidField.stringValue(), doc);
    	} catch (RemoteException re) {throw new LuceneException(re);}
    }

    public void deleteDocument(String uid) throws LuceneException {
    	try {
    		index.deleteDocument(indexName, uid);
    	} catch (RemoteException re) {throw new LuceneException(re);}
    }

    public void deleteDocuments(Term term) throws LuceneException {
    	try {
    		index.deleteDocuments(indexName,term);
    	} catch (RemoteException re) {throw new LuceneException(re);}
    }

    public void deleteDocuments(Query query) throws LuceneException {
    	try {
    		index.deleteDocuments(indexName,query);
    	} catch (RemoteException re) {throw new LuceneException(re);}
    }

    public Hits search(Query query) throws LuceneException {
    	try {
    		return index.search(indexName, query);
    	} catch (RemoteException re) {throw new LuceneException(re);}
    }

    public Hits search(Query query, int offset, int size) throws LuceneException {
    	Hits hits;
    	try {
    		hits = index.search(indexName, query,offset,size);
    	} catch (RemoteException re) {throw new LuceneException(re);}
        return hits;
    }

    public Hits search(Query query, Sort sort) throws LuceneException {
    	try {
    		return index.search(indexName, query);
    	} catch (RemoteException re) {throw new LuceneException(re);}
    }

    public Hits search(Query query, Sort sort, int offset, int size) throws LuceneException {
    	Hits hits;
    	try {
    		hits = index.search(indexName, query,offset,size);
    	} catch (RemoteException re) {throw new LuceneException(re);}
        return hits;
    }

    public void flush() throws LuceneException {
    	try {
    		index.commit(indexName);
    	} catch (RemoteException re) {throw new LuceneException(re);}
        
    }

    public void close() throws LuceneException {
        // No special cleanup procedure required to close this session.
        // Is this right??
    }

}
