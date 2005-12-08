package com.sitescape.ef.lucene;

import java.rmi.*;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Sort;

/**
 * Title: SsfIndexInterface
 * Description: This is the interface definition to be implemented by the server.
 * Copyright:    Copyright (c) 2005
 * Company: SiteScape, Inc.
 * @author Roy Klein
 * @version 1.0
 */

public interface SsfIndexInterface extends java.rmi.Remote {
    public void addDocument( String indexname, String UID, Document document ) throws RemoteException;
    public void deleteDocument(String indexname, String uid) throws RemoteException;
    public int deleteDocuments(String indexname, Term term) throws RemoteException;
    public int deleteDocuments(String indexname, Query query) throws RemoteException;
    public void commit(String indexname) throws RemoteException;
    public void stop(String indexname) throws RemoteException;
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query) throws RemoteException;
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query, int offset, int size) throws RemoteException;
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query, Sort sort) throws RemoteException;
    public com.sitescape.ef.lucene.Hits search (String indexname, Query query, Sort sort, int offset, int size) throws RemoteException;
}
