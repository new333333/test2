package com.sitescape.ef.search.local;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.sitescape.ef.util.LuceneUtil;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;

/**
 * This implementation provides access to local Lucene index.
 * 
 * @author Jong Kim
 *
 */
public class LocalLuceneSession implements LuceneSession {
    // Note: I'm not convinced that this implementation makes good use of Lucene,
    //       primarily due to my lack of intimiate knowledge of Lucene. 
    //       Two major implementation questions:
    //       1) Is it allowed to open more than one IndexWriter (using multiple
    //       instances of sessions in multiple threads) in a program? 
    //       What are the ramifications when we do that?
    //       2) What is the best practise around using IndexSearcher? Specifically,
    //       should it be opened and closed for each query or re-used for multiple
    //       queries? If re-used for extended period of time, then I assume that
    //       the index snapshot represented by the handle won't incorporate the
    //       changes that may have been made to the index since the handle was
    //       obtained. Is that right?
    
    // Updated Note: This implementation is rewritten to simply use Liferay's 
    // local index support, which is not very scalable. Since local index 
    // configuration should only be used for testing and/or demo installation,
    // I wouldn't bother with making this a production quality service. 
    
    private static final Log logger = LogFactory.getLog(LocalLuceneSession.class);

    private String indexPath;

    public LocalLuceneSession(String indexPath) {
        this.indexPath = indexPath;
    }
    
    public void addDocument(Document doc) {
	    if (doc.getField(BasicIndexUtils.UID_FIELD) == null)
	        throw new LuceneException("Document must contain a UID with field name " + BasicIndexUtils.UID_FIELD);

	    IndexWriter indexWriter = null;
        
        try {
            indexWriter = LuceneUtil.getWriter(indexPath);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not open writer on the index [" + this.indexPath + "]", e);
        }
        
        try {
            indexWriter.addDocument(doc);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not add document to the index [" + indexPath + "]", e);
        }
        finally {
            try {
                indexWriter.close();
            }
            catch(IOException e) {
            }
        }
    }

    public void deleteDocument(String uid) {
        deleteDocuments(new Term(BasicIndexUtils.UID_FIELD, uid));
    }
    
    public void deleteDocuments(Term term) {
        IndexReader indexReader = null;
        
        try {
            indexReader = LuceneUtil.getReader(indexPath);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not open reader on the index [" + this.indexPath + "]", e);
        }
        
        try {
            indexReader.delete(term);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not delete documents from the index [" + indexPath + "]", e);
        }
        finally {
            try {
                indexReader.close();
            }
            catch(IOException e) {
            }
        }    
    }
    
    public void deleteDocuments(Query query) {
        IndexSearcher indexSearcher = null;
        
        try {
            indexSearcher = LuceneUtil.getSearcher(indexPath);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not open searcher on the index [" + this.indexPath + "]", e);
        }

        try {
            deleteDocs(indexSearcher.search(query));
        } 
        catch (IOException e) {
            throw new LuceneException("Error searching index [" + indexPath + "]", e);
        }
        finally {
            try {
                indexSearcher.close();
            }
            catch(IOException e) {}
        }
    }
    
    public com.sitescape.ef.lucene.Hits search(Query query) {
        return this.search(query, 0, -1);
    }

    public com.sitescape.ef.lucene.Hits search(Query query, int offset, int size) {
        IndexSearcher indexSearcher = null;
        
        try {
            indexSearcher = LuceneUtil.getSearcher(indexPath);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not open searcher on the index [" + this.indexPath + "]", e);
        }

        try {
            org.apache.lucene.search.Hits hits = indexSearcher.search(query);
            if(size < 0)
                size = hits.length();
            com.sitescape.ef.lucene.Hits tempHits = com.sitescape.ef.lucene.Hits.transfer(hits, offset, size);
            tempHits.setTotalHits(hits.length());
            return tempHits;
        } 
        catch (IOException e) {
            throw new LuceneException("Error searching index [" + indexPath + "]", e);
        }
        finally {
            try {
                indexSearcher.close();
            }
            catch(IOException e) {}
        }
    }
    
    public com.sitescape.ef.lucene.Hits search(Query query, Sort sort) {
        return this.search(query, 0, -1);
    }

    public com.sitescape.ef.lucene.Hits search(Query query, Sort sort, int offset, int size) {
    	Hits hits = null;
        IndexSearcher indexSearcher = null;
        
        try {
            indexSearcher = LuceneUtil.getSearcher(indexPath);
        } 
        catch (IOException e) {
            throw new LuceneException("Could not open searcher on the index [" + this.indexPath + "]", e);
        }

        try {
        	if (sort == null) 
        		hits = indexSearcher.search(query);
        	else
        		hits = indexSearcher.search(query,sort);
            if(size < 0)
                size = hits.length();
            com.sitescape.ef.lucene.Hits tempHits = com.sitescape.ef.lucene.Hits.transfer(hits, offset, size);
            tempHits.setTotalHits(hits.length());
            return tempHits;
        } 
        catch (IOException e) {
            throw new LuceneException("Error searching index [" + indexPath + "]", e);
        }
        finally {
            try {
                indexSearcher.close();
            }
            catch(IOException e) {}
        }
    }

    public void flush() {
        // Because Liferay's Lucene functions (on which this implementation
        // is based) are atomic in that it flushes out after each operation,
        // there is no separate flush to perform. Nothing to do.
    }
    
    public void close() {
        // Nothing to do 
    }
    
    private int deleteDocs(org.apache.lucene.search.Hits hits) {
        int length = hits.length();
        
        if(length > 0) {
            IndexReader indexReader = null;
            try {
                indexReader =  LuceneUtil.getReader(indexPath);
            } 
            catch (IOException e) {
                throw new LuceneException("Could not open reader on the index [" + this.indexPath + "]", e);
            }
	        
	        try {
		        for(int i = 0; i < length; i++) {
		            int docId = hits.id(i);
		            indexReader.delete(docId);
		        }
	        }
	        catch(IOException e) {
	            throw new LuceneException("Could not delete documents from the index [" + indexPath + "]", e);
	        }
	        finally {
	            try {
	                indexReader.close();
	            }
	            catch(IOException e) {}
	        }
        }
        
        return length;        
    }

}
