package com.sitescape.ef.lucene.server;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;

import com.sitescape.ef.lucene.SsfIndexInterface;

/**
 * The main server for the lucene indexserver.
 * <p>
 * All access to indexes are via RMI (for now), and will be threaded
 * by the RMI subsystem. All modification transactions will be batched
 * and committed later by a watchdog thread. The commit increment is
 * settable via a config file. (We can decide if we want to add a call
 * to the API for setting that value)
 * <p>
 * Important semantics regarding the API:
 * <p>
 * 1) If the client side wants to guarantee that a very recent modification
 * is reflected in the results of a query, it should call <code>commit</code>
 * before it issues the query.
 * <p>
 * 2) There is no client-controlled rollback semantics supported.
 *
 * @author Roy Klein
 *
 */
public class SsfIndexer
    extends UnicastRemoteObject implements SsfIndexInterface {

    IndexObjectCache ioc;

    /**
     * Constructor
     *
     * @param servicename
     * @throws RemoteException
     */
    public SsfIndexer( String servicename ) throws RemoteException {
        ioc = new IndexObjectCache();
        try {
            Naming.rebind( servicename, (SsfIndexInterface)this );
        }
        catch( Exception e ) {
            System.out.println( e );
        }
    }
    /**
     * Add a document.
     *
     * @param ssfdocument
     * @throws RemoteException
     */
    public void addDocument(String indexname, String UID, Document document ) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        io.addDocument(UID, document);
    }

    /**
     * Delete all the documents which match the passed in term
     *
     * @param indexname
     * @param term
     * @throws RemoteException
     */
    public int deleteDocuments(String indexname, Term term) throws RemoteException {
          IndexObject io = ioc.getIndexObject(indexname);
          return io.deleteDocuments(term);
    }

    /**
     * Delete all the documents which match the passed in query
     *
     * @param squery
     * @throws RemoteException
     */
    public int deleteDocuments(String indexname, Query query) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        return io.deleteDocuments(query);
    }

    /**
     * Delete any document in the index with the matching uid
     *
     * @param indexname
     * @param uid
     * @throws RemoteException
     */
    public void deleteDocument(String indexname,String uid) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        io.deleteDocument(uid);
    }

    /**
     * Search for documents in the index that match the query
     *
     * @param squery
     * @throws RemoteException
     */
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        com.sitescape.ef.lucene.Hits tempHits = (com.sitescape.ef.lucene.Hits)io.search(query);
        return tempHits;
    }
    
    /**
     * Search for documents in the index that match the query
     *
     * @param squery
     * @throws RemoteException
     */
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query, Sort sort) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        com.sitescape.ef.lucene.Hits tempHits = (com.sitescape.ef.lucene.Hits)io.search(query, sort);
        return tempHits;
    }

    /**
     * Search for documents in the index that match the query. Return size hits
     * starting at offset.
     *
     * @param offset
     * @param size
     * @throws RemoteException
     */
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query, int offset, int size) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        com.sitescape.ef.lucene.Hits tempHits = io.search(query, offset, size);
        return tempHits;
    }
    
    /**
     * Search for documents in the index that match the query. Return size hits
     * starting at offset.
     *
     * @param offset
     * @param size
     * @throws RemoteException
     */
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query, Sort sort, int offset, int size) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        com.sitescape.ef.lucene.Hits tempHits = io.search(query, sort, offset, size);
        return tempHits;
    }

    /**
     * Commit all the changes to the index which are currently batched. This
     * resets the watchdog thread's timer.
     *
     * @param indexname
     * @throws RemoteException
     */
    public void commit(String indexname) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        io.commit();
    }

    /**
     * Stop this index
     * TBD
     *
     * @param indexname
     * @throws RemoteException
     */
    public void stop(String indexname) throws RemoteException {
        IndexObject io = ioc.getIndexObject(indexname);
        io.stop();
    }


}

