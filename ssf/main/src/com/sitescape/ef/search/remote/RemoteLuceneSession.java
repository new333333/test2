package com.sitescape.ef.search.remote;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.lucene.SsfIndexInterface;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;

/**
 *
 * @author Jong Kim
 */
public class RemoteLuceneSession implements LuceneSession {

	private SsfIndexInterface index;

	private String indexName;

	public RemoteLuceneSession(String indexName) throws LuceneException {
		try {
			// TEMPORARY, need to think about how to let the client
			// setup new rmi index servers. (Possibly have a generic 
			// RMI service listener that then kicks off individually named
			// index servers.
			String servicename = "rmi://localhost/SSFINDEXER";
			index = (SsfIndexInterface) Naming.lookup(servicename);
			if (index == null) {
				throw new LuceneException("Could not find index service: "
						+ servicename);
			}
			this.indexName = indexName;
		} catch (Exception e) {
			throw new LuceneException(e);
		}
	}

	public void addDocument(Document doc) throws LuceneException {
		Field uidField = doc.getField(BasicIndexUtils.UID_FIELD);
		if (uidField == null)
			throw new LuceneException(
					"Document must contain a UID with field name "
							+ BasicIndexUtils.UID_FIELD);
		try {
			index.addDocument(indexName, uidField.stringValue(), doc);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public void addDocuments(Collection docs) {
		for (Iterator iter = docs.iterator(); iter.hasNext();) {
			Document doc = (Document) iter.next();
			addDocument(doc);
		}

	}

	public void deleteDocument(String uid) throws LuceneException {
		try {
			index.deleteDocument(indexName, uid);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public void deleteDocuments(Term term) throws LuceneException {
		try {
			index.deleteDocuments(indexName, term);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public void deleteDocuments(Query query) throws LuceneException {
		try {
			index.deleteDocuments(indexName, query);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public Hits search(Query query) throws LuceneException {
		try {
			return index.search(indexName, query);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public Hits search(Query query, int offset, int size)
			throws LuceneException {
		Hits hits;
		try {
			hits = index.search(indexName, query, offset, size);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
		return hits;
	}

	public Hits search(Query query, Sort sort) throws LuceneException {
		try {
			return index.search(indexName, query, sort);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public Hits search(Query query, Sort sort, int offset, int size)
			throws LuceneException {
		Hits hits;
		try {
			hits = index.search(indexName, query, sort, offset, size);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
		return hits;
	}

	public ArrayList getTags(Query query, String tag) throws LuceneException {
		ArrayList results = new ArrayList();
		Long id = RequestContextHolder.getRequestContext().getUserId();
		try {
			results = index.getTags(indexName, query, id, tag);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
		return results;	
	}
	public void flush() throws LuceneException {
		try {
			index.commit(indexName);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}

	}

	public void close() throws LuceneException {
		// No special cleanup procedure required to close this session.
		// Is this right??
	}

	public void optimize() {
		try {
			index.optimize(indexName);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public void updateDocument(String uid, String fieldname, String fieldvalue) {
		try {
			index.updateDocument(indexName, uid, fieldname, fieldvalue);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}

	public void updateDocuments(Query query, String fieldname, String fieldvalue) {
		Hits hits;
		try {
			index.updateDocuments(indexName, query, fieldname, fieldvalue);
		} catch (RemoteException re) {
			throw new LuceneException(re);
		}
	}
}
