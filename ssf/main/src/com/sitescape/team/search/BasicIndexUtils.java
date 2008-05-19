/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import static com.sitescape.util.search.Constants.*;
/**
 *
 * @author Jong Kim
 */
public class BasicIndexUtils {
    
    // The following fields represent field names. 
    // Note: This defines only those fields that are common across all index
    // documents in the system. Additional fields that are not common must NOT
    // be defined in this class. 
    
    private static final String UID_DELIM = "_";
    //use to separate field ids and names
    public static final String DELIMITER = "#";
   
    public static String makeUid(String className, String id) {
        return className + UID_DELIM + id;
    }
    
    public static String makeUid(String className, Long id) {
        return makeUid(className, id.toString());
    }
    
    public static String getClassName(String uid) {
        return uid.substring(0, uid.indexOf(UID_DELIM));
    }
    
    public static String getId(String uid) {
        return uid.substring(uid.indexOf(UID_DELIM) + 1);
    }
    
    public static void addUid(Document doc, String uid, boolean fieldsOnly) {
        doc.add(new Field(UID_FIELD, uid, Field.Store.YES, Field.Index.UN_TOKENIZED));    
        doc.add(new Field(THIS_CLASS_FIELD, getClassName(uid), Field.Store.NO, Field.Index.UN_TOKENIZED));
    }
    
    public static void addDocType(Document doc, String docType, boolean fieldsOnly) {
        doc.add(new Field(DOC_TYPE_FIELD, docType, Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    
    public static void addAttachmentType(Document doc, String attType, boolean fieldsOnly) {
        doc.add(new Field(ATTACHMENT_TYPE_FIELD, attType, Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    /*
    public static Document createDocument(String uid, String docType, String className) {
        Document doc = new Document();
        doc.add(Field.Keyword(UID_FIELD, uid));
        doc.add(Field.Keyword(DOC_TYPE_FIELD, docType));
        doc.add(Field.Keyword(THIS_CLASS_FIELD, className));
        return doc;
    }*/
    
    /**
     * Make sure that the document contains all required fields. 
     * 
     * @param doc
     * @throws LuceneException if validation fails
     */
    public static void validateDocument(Document doc) throws LuceneException {
	    Field uidField = doc.getField(UID_FIELD);
	    if(uidField == null)
	        throw new LuceneException("Document must contain a field with name " + UID_FIELD);	  
       
	    Field docTypeField = doc.getField(DOC_TYPE_FIELD);
	    if(docTypeField == null)
	        throw new LuceneException("Document must contain a field with name " + DOC_TYPE_FIELD);	  
	    
	    Field classField = doc.getField(THIS_CLASS_FIELD);
	    if(classField == null)
	        throw new LuceneException("Document must contain a field with name " + THIS_CLASS_FIELD);	  
    }
    
    public static String getUid(Document doc) {
	    Field uidField = doc.getField(UID_FIELD);
	    if(uidField == null)
	        throw new LuceneException("Document must contain a field with name " + UID_FIELD);	  
	    return uidField.stringValue();
    }
    
    public static String getDocType(Document doc) {
	    Field docTypeField = doc.getField(DOC_TYPE_FIELD);
	    if(docTypeField == null)
	        throw new LuceneException("Document must contain a field with name " + DOC_TYPE_FIELD);	  
	    return docTypeField.stringValue();
    }
    
    public static String getClassName(Document doc) {
	    Field classField = doc.getField(THIS_CLASS_FIELD);
	    if(classField == null)
	        throw new LuceneException("Document must contain a field with name " + THIS_CLASS_FIELD);	  
	    return classField.stringValue();
    }
    
    public static  void addFileContents(Document doc, String text) {
    	Field contents = new Field(TEMP_FILE_CONTENTS_FIELD, text, Field.Store.NO, Field.Index.TOKENIZED);
        doc.add(contents);
    }
    
    public static  Field allTextField(String text) {
        return new Field(ALL_TEXT_FIELD, text, Field.Store.NO, Field.Index.TOKENIZED);
    }   
   
    public static String buildAclTag(String tag, String aclId)
    {
    	String aclTag = TAG_ACL_PRE + aclId + TAG + tag;
    	return aclTag;
    }
    
    
	// This method reads thru the results from a search, finds the unique community tags, 
	// and places them into an array of hashmaps(each hashmap contains the tag 
    // and it's occurence count), in alphabetic order.    
	public static List sortCommunityTags(List entries) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique tagfields.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("\\s");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j];

		    	if (strTag.equals("")) continue;
		    	
		    	Integer tagCount = (Integer) tagMap.get(strTag);
		    	if (tagCount == null) {
		    		tagMap.put(strTag, new Integer(1));
		    	}
		    	else {
		    		int intTagCount = tagCount.intValue();
		    		tagMap.put(strTag, new Integer(intTagCount+1));
		    	}
		    }
		}
		
		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}





	// This method reads thru the results from a search, finds the unique personal tags, 
	// and places them into an array of hashmaps(each hashmap contains the tag 
    // and it's occurence count), in alphabetic order.
	//
	public static List sortPersonalTags(List entries) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();

		User user = RequestContextHolder.getRequestContext().getUser();
    	long userId = user.getId();
    	
    	for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map) entries.get(i);
			String strTags = (String) entry.get(WebKeys.SEARCH_ACL_TAG_ID);
			if (strTags == null || "".equals(strTags))
				continue;

			String[] strTagArray = strTags.split(TAG_ACL_PRE);
			for (int j = 0; j < strTagArray.length; j++) {
				String strTag = strTagArray[j].trim();
				if (strTag.equals(""))
					continue;

				// Ignore these entries as they refer to community entries.
				if (strTag.startsWith(READ_ACL_ALL + TAG))
					continue;

				String strUserIdTag = userId + TAG;

				// We are going to get only the personal tags relating to the
				// user
				if (strTag.startsWith(strUserIdTag)) {
					String thisTag = strTag.substring(strUserIdTag.length()).trim();

					if (thisTag.equals(""))
						continue;

					Integer tagCount = (Integer) tagMap.get(thisTag);
					if (tagCount == null) {
						tagMap.put(thisTag, new Integer(1));
					} else {
						int intTagCount = tagCount.intValue();
						tagMap.put(thisTag, new Integer(intTagCount + 1));
					}
				} else
					continue;
			}
		}

		// sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}
	
}
