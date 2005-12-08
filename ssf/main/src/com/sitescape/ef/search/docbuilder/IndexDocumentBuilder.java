package com.sitescape.ef.search.docbuilder;

import org.apache.lucene.document.Document;

/**
 * @author Jong Kim
 *
 */
public interface IndexDocumentBuilder {
    
    /**
     * Convert the object to a corresponding Lucene document for indexing.
     * It is REQUIRED that the document contains a UID field. 
     * 
     * @param obj
     * @return
     */
    public Document buildIndexDocument(Object obj);
    
    /**
     * Convert the object to a Lucene term that, when executed against the
     * index, would return the corresponding Lucene document and nothing else.
     *
     * @param obj
     * @return
     */
    //public Term toTerm(Object obj);
    
    /**
     * Get the object's UID.
     */
    public String obtainIndexDocumentUid(Object obj);
}
