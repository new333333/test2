/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.lucene;

import java.io.IOException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.kablink.util.search.Constants;


/**
 *
 * @author Jong Kim
 *
 */
public class Hits implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// The number of documents in this object. This field is set on the server side.
	private int size;
	// This may be exact or approximate depending on whether or not a search involves
	// client side-driven post filtering for access check. 
	// This field is set on the server side.
    private int totalHits = 0;
    // Indicates whether the totalHits is approximate or exact.
    // This field is set on the server side.
    private boolean totalHitsApproximate = true;
    // Indicates whether or not there is at least one more document in the search index
    // that matches the search query (including ACL filter) that isn't returned in this
    // object.
    // This field is set on the client side.
    private boolean thereIsMore = false;
        
    // Matching documents. This field is set on the server side.
    private Document[] documents;
    // Scores for the matching documents. This field is set on the server side.
    private float[] scores;

    // This optional field is for internal use only. Must NOT be used directly by the
    // application code. If true, the document represents a net folder file/entry/comment
    // that is accessible to the user via ACL granted through sharing. The value of false
    // doesn't necessarily mean the opposite, so shouldn't be interpreted in one particular
    // way. For example it may simply mean that the pertaining information is unknown.
    // This field is set on the server side.
    private boolean[] noAclButAccessibleThroughSharing; // all elements initialized to false
    
    
    public Hits(int length) {
        this.size = length;
        documents = new Document[length];
        scores = new float[length];
        noAclButAccessibleThroughSharing = new boolean[length];
     }

    // A sort of copy constructor
    public Hits(Hits hits, BitSet bitSet, int accessibleCount) {
    	this(accessibleCount);
    	int index = 0;
    	for(int i = 0; i < hits.size; i++) {
    		if(bitSet.get(i)) {
    			this.setDoc(hits.doc(i), index);
    			this.setScore(hits.score(i), index);
    			index++;
    		}
    	}
    }
    
    public void removeLast() {
    	documents[size-1] = null;
    	scores[size-1] = 0;
    	noAclButAccessibleThroughSharing[size-1] = false;
    	size -= 1;
    }
    
    public Document doc(int n) {
        return documents[n];
    }

    public int length() {
        return this.size;
    }
    
    public void setLength(int length) {
    	this.size = length;
    }

    public float score(int n) {
        return scores[n];
    }

    public boolean noAclButAccessibleThroughSharing(int n) {
    	return noAclButAccessibleThroughSharing[n];
    }

    public static Hits transfer(org.apache.lucene.search.IndexSearcher searcher, org.apache.lucene.search.TopDocs topDocs,
            int offset, int maxSize, Set<String> noAclButAccessibleThroughSharingEntryIds, boolean totalHitsApproximate) throws IOException {
        if (topDocs == null) return new Hits(0);
    	int length = (topDocs.scoreDocs == null)? 0: topDocs.scoreDocs.length;
        length = Math.min(length - offset, maxSize);
        if (length <= 0) return new Hits(0);
        Hits ss_hits = new Hits(length);
        ScoreDoc[] hits = topDocs.scoreDocs;
        Document doc;
        String entityType;
        String entryId;
        for(int i = 0; i < length; i++) {
        	doc = searcher.doc(hits[offset + i].doc);
        	if(noAclButAccessibleThroughSharingEntryIds != null) {
	        	entityType = doc.get(Constants.ENTITY_FIELD);
	        	if(entityType != null && Constants.ENTITY_TYPE_FOLDER_ENTRY.equals(entityType)) {
	        		entryId = doc.get(Constants.DOCID_FIELD);
	        		if(entryId != null && noAclButAccessibleThroughSharingEntryIds.contains(entryId)) {
	        			// This doc represents a folder entry or reply/comment or attachment that doesn't
	        			// have its intrinsic ACL indexed with it but instead have share-granted ACL
	        			// that made it pass the caller's regular ACL filter. We want to pass this
	        			// information to the caller so that the caller wouldn't have to apply 
	        			// post-filtering on this doc.
	        			ss_hits.setNoAclButAccessibleThroughSharing(true, i);
	        		}
	        	}
        	}
            ss_hits.setDoc(doc, i);
            ss_hits.setScore(hits[offset + i].score, i);
        }
        ss_hits.setTotalHits(topDocs.totalHits);
        ss_hits.setTotalHitsApproximate(totalHitsApproximate);
        return ss_hits;
    }

    public void setDoc(Document doc, int n) {
        documents[n] = doc;
    }

    public void setScore(float score, int n) {
        scores[n] = score;
    }
    
    public void setNoAclButAccessibleThroughSharing(boolean value, int n) {
    	noAclButAccessibleThroughSharing[n] = value;
    }
    
	/**
	 * @return Returns the totalHits.
	 */
	public int getTotalHits() {
		return totalHits;
	}

	/**
	 * @param totalHits The totalHits to set.
	 */
	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}

	public boolean getThereIsMore() {
		return thereIsMore;
	}

	public void setThereIsMore(boolean thereIsMore) {
		this.thereIsMore = thereIsMore;
	}

	public boolean isTotalHitsApproximate() {
		return totalHitsApproximate;
	}

	public void setTotalHitsApproximate(boolean totalHitsApproximate) {
		this.totalHitsApproximate = totalHitsApproximate;
	}

}