package com.sitescape.ef.lucene.server;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;


/**
 * Title: IndexObject
 * Description: The main object for each Index
 * Copyright:    Copyright (c) 2005
 * Company: SiteScape, Inc.
 * @author Roy Klein
 * @version 1.0
 */

public class IndexObject  {


    private String indexName;
    private File indexDir;

    protected final Log logger = LogFactory.getLog(getClass());
    
    private IndexWriter indexWriter;
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    // Needed for synchronizing reads & writes
    private final static int READER = 0;
    private final static int WRITER = 1;
    private final static int SEARCHER = 2;

    private int lastReaderWriter = SEARCHER;

    private BatchQueue addQ, delQ;
    private WatchDog watchDog;

    long startTime, stopTime;
    Object SyncObj = new Object();


    /**
     * Constructor - make sure the index directory either exists, or
     * can be created. The indexname will be appended to the basedirectory
     * (as specified in the options file) to find the directory where the index
     * lives.
     *
     * @param indexname
     *
     * @throws RemoteException
     */
    public IndexObject(String indexname) throws RemoteException {
    	
    	String indexDirectory;
    	java.util.Properties properties;
    	try {
	    	java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream("lucene-server.properties");
	    	properties = new Properties();
	    	properties.load(stream);
	    	stream.close();
    	} catch (IOException ioe) {throw new RemoteException("The lucene-server.properties could not be found"); }
    	
    	String topIndexDir = properties.getProperty("index.root.dir");
    	logger.info("Properties: " + properties.toString());
    	if (topIndexDir != null) {
    		indexDirectory = new String(topIndexDir + System.getProperty("file.separator")+ indexname);
    	} else {
    		indexDirectory = new String("c:/temp/luceneindex/" + indexname);
    	}
    	logger.info("indexDirectory is: " + indexDirectory);
        // See if there's already a directory by that name
        indexName = indexDirectory;

        // Check if the directory exists, make sure it's actually a dir, and not a file.
        indexDir = new File(indexDirectory);
        if (indexDir.exists()) {
            if(!indexDir.isDirectory())
                throw new RemoteException("The index name: " + indexname + " exists, but is not useable as an index directory");
        } else {
          // create the dir if it doesn't already exist
            try {
                openIndexWriter();
                closeWriter();
            } catch (Exception e) {
                throw new RemoteException("The index name: " + indexname + " could not be opened: " + e.toString());
            }
        }
        // Initialize the batch Queue's
        addQ = new BatchQueue();
        delQ = new BatchQueue();

        // Start up a watchdog thread to make sure the queues are emptied
        // on a regular interval
    	String wdinterval = properties.getProperty("watchdog.interval");
    	if (wdinterval != null) {
    		int wdi = Integer.parseInt(wdinterval);
    		
    		if (wdi == 0) {
    			wdi = 2000;
    		}
    		watchDog = new WatchDog(this,wdi);
    	} else {
    		watchDog = new WatchDog(this,20000);
    	}
        watchDog.start();
    }

    /**
     * Add a document. The batch queue will make sure there's only one
     * document with this UID on the add q at any one time.
     *
     * @param ssfdocument
     *
     * @throws RemoteException
     */
    public synchronized void addDocument(String UID, Document document) throws RemoteException {
    	logger.info("addDocument:UID = " + UID);    	
    	SsfDocument sdoc = new SsfDocument();
    	sdoc.setUID(UID);
    	sdoc.setDocument(document);
        addQ.enqueue(sdoc);
    }

    /**
     * Delete any document in the index with the matching uid. If the doc
     * is on the AddQ, delete it. then add the delete to the delQ so that
     * if it was in the index, it'll be deleted there too.
     *
     * @param uid
     *
     * @throws RemoteException
     */
    public synchronized void deleteDocument(String uid) throws RemoteException {
    	logger.info("deleteDocument:UID = " + uid);
        addQ.dequeue(uid);
        SsfDocument deldoc = new SsfDocument();
        deldoc.setUID(uid);
        deldoc.setDocument(new org.apache.lucene.document.Document());
        delQ.enqueue(deldoc);
    }

    /**
     * Delete all the documents which match the passed in term
     *
     * @param term
     *
     * @throws RemoteException
     */
    public synchronized int deleteDocuments(Term term) throws RemoteException {
    	logger.info("deleteDocuments:TERM = " + term.toString());
    	int retval = 0;
        // first, commit all previous transactions on the Queue
        this.commit();
        try {
            openIndexReader();
            try {
                retval = indexReader.delete(term);
            } catch (IOException ioe) {}
        } finally {
            closeReader();
        }
        return retval;
    }

    /**
     * Delete all the documents which match the passed in query
     *
     * @param query
     *
     * @throws RemoteException
     */
    public synchronized int deleteDocuments(Query query) throws RemoteException {
    	logger.info("deleteDocuments:QUERY = " + query.toString());
    	int retval = 0;
        Hits hits;
        commit();
        try {
            hits = indexSearcher.search(query);
            if (hits.length() <= 0) return 0;
            openIndexReader();
            for (int i = 0; i < hits.length(); i++) {
                indexReader.delete(hits.id(i));
            }
        } catch (IOException ioe) {
            throw new RemoteException("Couldn't open reader");
        } finally {
            closeReader();
        }
        return hits.length();
    }


    /**
     * Commit all the changes that are queued up.
     *
     * @param indexname
     *
     * @throws RemoteException
     */
    public synchronized void commit() throws RemoteException{
        startTime = System.currentTimeMillis();

        try {
            emptyDelQ();
            emptyAddQ();
        } catch (RemoteException re) {}

        stopTime = System.currentTimeMillis();
        System.out.println("Commit time: " + (stopTime - startTime) + " ms");
        watchDog.resetTimer();
        watchDog.interrupt();
    }
    /**
     * Stop this index
     *
     * @param indexname
     *
     * @throws RemoteException
     */
    public synchronized void stop() throws RemoteException{
        startTime = System.currentTimeMillis();

        try {
            emptyDelQ();
            emptyAddQ();
        } catch (RemoteException re) {}

        stopTime = System.currentTimeMillis();
        System.out.println("Commit time: " + (stopTime - startTime) + " ms");
        watchDog.setStop();
        watchDog.interrupt();
    }
    /**
     * Empty the batch queue of all deleted docs
     *
     * @throws RemoteException
     */
    private void emptyDelQ() throws RemoteException {
        System.out.println("delQ has: " + delQ.size() + " entries");
        if (delQ.size() == 0) return;
        try {
            openIndexReader();
            for (int i=0; i<delQ.size(); i++) {
                SsfDocument sdoc = (SsfDocument)delQ.dequeue();
                try {
                    indexReader.delete(new Term("_uid", sdoc.getUID()));
                } catch (IOException ioe) {
                    throw new RemoteException("Error emptying the Delete Queue: ", ioe);
                }
            }
        } finally {
            closeReader();
        }
    }


    /**
     * Empty the batch queue of all added docs
     *
     * @throws RemoteException
     */
    private void emptyAddQ() throws RemoteException {
        Analyzer manlzr = new MixedCaseAnalyzer();
        System.out.println("addQ has: " + addQ.size() + " entries");
        if (addQ.size() == 0) return;
        try {
            openIndexWriter();
            for (int i=0; i<addQ.size(); i++) {
                SsfDocument sdoc = (SsfDocument)addQ.dequeue();
                try {
                    indexWriter.addDocument(sdoc.getDocument(),manlzr);
                } catch (IOException ioe) {
                    throw new RemoteException("Error emptying the Add Queue: ", ioe);
                }
            }
        } finally {
            try {
                closeWriter();
            } catch (Exception e) {
                throw new RemoteException("Could not close index writer for directory [" + this.indexDir + "]", e);
            }
        }
    }

    /**
     * Search for documents in the index that match the query
     *
     * @param squery
     *
     * @throws RemoteException
     */
    public com.sitescape.ef.lucene.Hits search(Query query) throws RemoteException{
    	logger.info("search:QUERY = " + query.toString());
    	return this.search(query,0,-1);
    }


    /**
     * Search for documents in the index that match the query. Return size hits
     * starting at offset.
     *
     * @param query
     * @param offset
     * @param size
     *
     * @throws RemoteException
     */
    public com.sitescape.ef.lucene.Hits search(Query query, int offset, int size) throws RemoteException{
    	logger.info("search:QUERY = " + query.toString() + "offset = " + offset + "size = " + size);
    	/* Comment out for now.  This wipes out all optimizations, however, if
         * we decided that all searches MUST reflect all previous changes, then
         * we'll want this in here.
         * // first, process all the transactions in the batch queues
         * watchDog.resetTimer();
         * commit();
         */
        //open a new searcher if necessary
        openIndexSearcher();
        try {
            org.apache.lucene.search.Hits hits = indexSearcher.search(query);
            System.out.println("SEARCH: There were " + hits.length() + " matching hits on query: " + query.toString());
            return com.sitescape.ef.lucene.Hits.transfer(hits,offset,size);
        } catch (IOException e) {
            throw new RemoteException("Error searching index '" + indexName + "'", e);
        }
    }

    /**
     * Open a reader
     *
     * @throws RemoteException
     */
    private void openIndexReader() throws RemoteException {
        if(this.indexReader == null) {
            try {
                this.indexReader = IndexReader.open(this.indexDir);
            } catch (IOException e) {
                throw new RemoteException("Could not open index reader for directory [" + this.indexDir + "]", e);
            }
        }
    }

    /**
     * Open a searcher
     *
     * @throws RemoteException
     */
    private void openIndexSearcher() throws RemoteException {
        if ((this.indexSearcher == null) || !lastAccess(SEARCHER)) {
            try {
                this.indexSearcher = new IndexSearcher(this.indexName);
            } catch (IOException e) {
                throw new RemoteException("Could not open index searcher for directory [" + this.indexDir + "]", e);
            }
        }
    }

    /**
     * Open a writer
     *
     * @throws RemoteException
     */
    private void openIndexWriter() throws RemoteException {
        if(this.indexWriter == null) {
            try {
                boolean create = true;
                if(this.indexDir.exists())
                    create = false;
                this.indexWriter = new IndexWriter(this.indexDir, new StandardAnalyzer(), create);
            } catch (IOException e) {
                throw new RemoteException("Could not open index writer for directory [" + this.indexDir + "]", e);
            }
        }
    }

    /**
     * Keep track of the last access to this index.
     * it's a small optimization that will keep us
     * from opening new Searchers until it is necessary
     *
     * @param currentcaller
     */
    private boolean lastAccess(int currentCaller) {
        synchronized(SyncObj) {
            if (currentCaller != lastReaderWriter) {
                lastReaderWriter = currentCaller;
                return false;
            }
            return true;
        }
    }


    /**
     * Close the reader
     */
    private void closeReader() {
        try {
            if (this.indexReader != null) {
                this.indexReader.close();
                this.indexReader = null;
            }
        } catch
            (java.io.IOException jio) {}
        lastAccess(READER);
    }

    /**
     * Close the writer
     */
    private void closeWriter() {
        try {
            this.indexWriter.close();
            this.indexWriter = null;
        } catch (java.io.IOException jio) {}
            lastAccess(WRITER);
    }

}