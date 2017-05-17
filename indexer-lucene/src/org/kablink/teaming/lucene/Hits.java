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

import gnu.trove.set.hash.TLongHashSet;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.ScoreDoc;
import org.kablink.teaming.lucene.util.SearchFieldResult;
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
    // Matching documents. This field is set on the server side.
    private List<Map<String,Object>> documents;
    
    // This optional field is for internal use only. Must NOT be used directly by the
    // application code. If true, the document represents a net folder file/entry/comment
    // that is accessible to the user via ACL granted through sharing. The value of false
    // doesn't necessarily mean the opposite, so shouldn't be interpreted in one particular
    // way. For example it may simply mean that the pertaining information is unknown.
    // This field is set on the server side.
    private boolean[] noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl; // all elements initialized to false

    ///// THE FOLLOWING TWO FIELDS ARE CLIENT-SIDE FIELDS (that is, they are set on the client side) /////
    ////  THESE FIELDS ARE FOR INTERNAL USE ONLY. APPLIATION MUST NOT RELY ON THIS.
    
    // Indicates whether or not there is at least one more document in the search index
    // that matches the search query (including ACL filter) that isn't returned in this
    // object.
    private boolean thereIsMore = false;
    
    // Indicates whether or not the user has access to each document in this object.
    // This information is finalized after combining the information from the search
    // index and the result of dynamic ACL checking if any.
    private boolean[] aclCheckResult;     
    
    // Indicate that the result is only partial because one or more error was encountered 
    // during the processing. This field is not meaningful if the search operation was
    // set up to fail fast which is the default mode.
    private boolean partialListDueToError = false;
    
    public Hits(int length) {
        this.size = length;
        documents = new ArrayList<Map<String,Object>>(length);
        noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl = new boolean[length];
     }

    public Hits(List<Map<String,Object>> documents) {
    	this.size = documents.size();
    	this.totalHits = documents.size();
    	this.totalHitsApproximate = false;
    	this.documents = documents;
    }
    
    public List<Map<String,Object>> getDocuments() {
    	return documents;
    }
    
    public Map<String,Object> doc(int n) {
    	return documents.get(n);
    }

    public int length() {
        return this.size;
    }

    public void truncate(int newSize) {
    	if(newSize > size)
    		throw new IllegalArgumentException("New size bigger than existing size");
    	// Adjust documents
    	for(int i = size-1; i >= newSize; i--) {
    		documents.remove(i);
    	}
    	// We are NOT going to adjust noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl
    	// since there is no exposed interface with which caller can get hold on the entire
    	// array at once (unlike documents).
    	size = newSize;
    }
    
    public boolean noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl(int n) {
    	return noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl[n];
    }

    public boolean isVisible(int n) {
    	return aclCheckResult[n];
    }
    
    public boolean isPartialListDueToError() {
		return partialListDueToError;
	}

	public void setPartialListDueToError(boolean partialListDueToError) {
		this.partialListDueToError = partialListDueToError;
	}

	public static Hits transfer(org.apache.lucene.search.IndexSearcher searcher, org.apache.lucene.search.TopDocs topDocs,
            int offset, 
            int maxSize, 
            List<String> fieldNames,
            TLongHashSet noIntrinsicAclStoredButAccessibleThroughExtendedAcl_entryIds, 
            TLongHashSet noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds, 
            boolean totalHitsApproximate) throws IOException {
        if (topDocs == null) return new Hits(0);
    	int length = (topDocs.scoreDocs == null)? 0: topDocs.scoreDocs.length;
        length = Math.min(length - offset, maxSize);
        if (length <= 0) return new Hits(0);
        Hits ss_hits = new Hits(length);
        ScoreDoc[] hits = topDocs.scoreDocs;
        Document doc;
        String entityType;
        String entryStrId;
        long entryId;
        boolean checkExtendedAcl = 
        		((noIntrinsicAclStoredButAccessibleThroughExtendedAcl_entryIds != null &&
        		noIntrinsicAclStoredButAccessibleThroughExtendedAcl_entryIds.size() > 0) ||
        		(noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds != null &&
        				noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds.size() > 0));
        for(int i = 0; i < length; i++) {
        	doc = searcher.doc(hits[offset + i].doc);
        	if(checkExtendedAcl) {
	        	entityType = doc.get(Constants.ENTITY_FIELD);
	        	if(entityType != null && Constants.ENTITY_TYPE_FOLDER_ENTRY.equals(entityType)) {
	        		entryStrId = doc.get(Constants.DOCID_FIELD);
	        		if(entryStrId != null) {
	        			try {
	        				entryId = Long.parseLong(entryStrId);
	        				if((noIntrinsicAclStoredButAccessibleThroughExtendedAcl_entryIds != null &&
	        						noIntrinsicAclStoredButAccessibleThroughExtendedAcl_entryIds.contains(entryId)) ||
	        						(noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds != null &&
	        								noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds.contains(entryId))) {
	    	        			// This doc represents a folder entry or reply/comment or attachment that doesn't
	    	        			// have its intrinsic ACL indexed with it but instead have share-granted ACL
	    	        			// that made it pass the caller's regular ACL filter. We want to pass this
	    	        			// information to the caller so that the caller wouldn't have to apply 
	    	        			// post-filtering on this doc.
	    	        			ss_hits.setNoIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl(true, i);
	    	        		}
	        			}
	        			catch(NumberFormatException e) {}
	        		}
	        	}
        	}
            ss_hits.addDoc(toMap(doc, fieldNames));
            //ss_hits.setScore(hits[offset + i].score, i);
        }
        ss_hits.setTotalHits(topDocs.totalHits);
        ss_hits.setTotalHitsApproximate(totalHitsApproximate);
        return ss_hits;
    }
	
	public static Hits transfer(List<Long> entityIds, boolean totalHitsApproximate) {
		Hits ss_hits = new Hits(entityIds.size());	
		for(Long entityId:entityIds) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.ENTITY_ID_FIELD, entityId);
			ss_hits.addDoc(map);
		}
        ss_hits.setTotalHits(entityIds.size());
        ss_hits.setTotalHitsApproximate(totalHitsApproximate);
        return ss_hits;
	}

    private static Map<String,Object> toMap(Document doc, List<String> fieldNames) {
		Fieldable fld;
		HashMap<String,Object> map = new HashMap<String,Object>();
		//enumerate thru all the fields, and add to the map if relevant
		List<Fieldable> flds = doc.getFields();
		for(int i = 0; i < flds.size(); i++) {
			fld = (Fieldable)flds.get(i);
			// Include the field only if there's no restriction on fields or the field
			// passes the restriction.
			if(fieldNames == null || fieldNames.contains(fld.name()) || isRequiredField(fld.name())) {
				//TODO This hack needs to go.
				if (isDateField(fld.name())) {
					try {
						map.put(fld.name(), DateTools.stringToDate(fld.stringValue()));
					} catch (ParseException e) {
						map.put(fld.name(), new Date());
					}
				} else if(fld instanceof NumericField) {
					map.put(fld.name(), ((NumericField) fld).getNumericValue());
	            } else if (!map.containsKey(fld.name())) {
	            	map.put(fld.name(), fld.stringValue());
	            } else {
	            	Object obj = map.get(fld.name());
	            	SearchFieldResult val;
	            	if (obj instanceof String) {
	            		val = new SearchFieldResult();
	            		//replace
	            		map.put(fld.name(), val);
	            		val.addValue((String)obj);
	            	} else {
	            		val = (SearchFieldResult)obj;
	            	}
	            	val.addValue(fld.stringValue());
	            } 
			}
        }
		return map;
    }
    
    private static boolean isRequiredField(String name) {
    	// The following fields are needed when the client conducts ACL checking
    	return (Constants.ENTRY_ACL_PARENT_ID_FIELD.equals(name) || Constants.RESOURCE_DRIVER_NAME_FIELD.equals(name) || Constants.RESOURCE_PATH_FIELD.equals(name) || Constants.DOC_TYPE_FIELD.equals(name));
    }
    
	private static boolean isDateField(String fieldName) {
    	
    	if (fieldName == null) return false;
	    	
    	if (fieldName.equals(Constants.CREATION_DATE_FIELD)) return true;
	    	
	    if (fieldName.equals(Constants.MODIFICATION_DATE_FIELD)) return true;

    	if (fieldName.equals(Constants.LASTACTIVITY_FIELD)) return true;    	

    	if (fieldName.endsWith(Constants.EVENT_FIELD_START_DATE)) return true;

    	if (fieldName.endsWith(Constants.EVENT_FIELD_CALC_START_DATE)) return true;

    	if (fieldName.endsWith(Constants.EVENT_FIELD_LOGICAL_START_DATE)) return true;

	    if (fieldName.endsWith(Constants.EVENT_FIELD_END_DATE)) return true;
	    
	    if (fieldName.endsWith(Constants.EVENT_FIELD_CALC_END_DATE)) return true;
	    
	    if (fieldName.endsWith(Constants.EVENT_FIELD_LOGICAL_END_DATE)) return true;
	    
	    if (fieldName.equals("due_date")) return true;
	    	
	    return false;
    }

	/*
    public void setDoc(Map doc, int n) {
    	documents.set(n, doc);
    }
    */

    public void addDoc(Map doc) {
    	documents.add(doc);
    }
    
    public void setNoIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl(boolean value, int n) {
    	noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl[n] = value;
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

	public void setAclCheckResult(boolean[] aclCheckResult) {
		this.aclCheckResult = aclCheckResult;
	}

}