package com.sitescape.ef.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.security.acl.AclContainer;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.web.WebKeys;
/**
 *
 * @author Jong Kim
 */
public class BasicIndexUtils {
    
    // The following fields represent field names. 
    // Note: This defines only those fields that are common across all index
    // documents in the system. Additional fields that are not common must NOT
    // be defined in this class. 
    
    public static final String UID_FIELD = "_uid";
    public static final String DOC_TYPE_FIELD = "_docType";
    public static final String THIS_CLASS_FIELD = "_class";
    public static final String ALL_TEXT_FIELD = "_allText";
    public static final String READ_ACL_FIELD = "_readAcl";
    public static final String READ_DEF_ACL_FIELD = "_readDefAcl";
    public static final String GROUP_VISIBILITY_FIELD = "_groupVis";
    public static final String TAG_FIELD = "_tagField";
    public static final String ACL_TAG_FIELD = "_aclTagField";
    
    
    // Defines field values
    public static final String READ_ACL_ALL = "all";
    public static final String TAG = "TAG";
    public static final String TAG_ACL_PRE = "ACL";
    public static final String GROUP_ANY = "any";
    
    
    // The following fields represent valid values for DOC_TYPE_FIELD.
    
    public final static String DOC_TYPE_BINDER 		= "binder";
    public final static String DOC_TYPE_ENTRY 		= "entry";
    public final static String DOC_TYPE_ATTACHMENT	= "attachment"; 
    
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
    
    public static void addUid(Document doc, String uid) {
        doc.add(new Field(UID_FIELD, uid, Field.Store.YES, Field.Index.UN_TOKENIZED));    
        doc.add(new Field(THIS_CLASS_FIELD, getClassName(uid), Field.Store.NO, Field.Index.UN_TOKENIZED));
    }
    
    public static void addDocType(Document doc, String docType) {
        doc.add(new Field(DOC_TYPE_FIELD, docType, Field.Store.YES, Field.Index.UN_TOKENIZED));
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
	    Field uidField = doc.getField(BasicIndexUtils.UID_FIELD);
	    if(uidField == null)
	        throw new LuceneException("Document must contain a field with name " + BasicIndexUtils.UID_FIELD);	  
       
	    Field docTypeField = doc.getField(BasicIndexUtils.DOC_TYPE_FIELD);
	    if(docTypeField == null)
	        throw new LuceneException("Document must contain a field with name " + BasicIndexUtils.DOC_TYPE_FIELD);	  
	    
	    Field classField = doc.getField(BasicIndexUtils.THIS_CLASS_FIELD);
	    if(classField == null)
	        throw new LuceneException("Document must contain a field with name " + BasicIndexUtils.THIS_CLASS_FIELD);	  
    }
    
    public static String getUid(Document doc) {
	    Field uidField = doc.getField(BasicIndexUtils.UID_FIELD);
	    if(uidField == null)
	        throw new LuceneException("Document must contain a field with name " + BasicIndexUtils.UID_FIELD);	  
	    return uidField.stringValue();
    }
    
    public static String getDocType(Document doc) {
	    Field docTypeField = doc.getField(BasicIndexUtils.DOC_TYPE_FIELD);
	    if(docTypeField == null)
	        throw new LuceneException("Document must contain a field with name " + BasicIndexUtils.DOC_TYPE_FIELD);	  
	    return docTypeField.stringValue();
    }
    
    public static String getClassName(Document doc) {
	    Field classField = doc.getField(BasicIndexUtils.THIS_CLASS_FIELD);
	    if(classField == null)
	        throw new LuceneException("Document must contain a field with name " + BasicIndexUtils.THIS_CLASS_FIELD);	  
	    return classField.stringValue();
    }
    public static  void addAllText(Document doc, String text) {
        doc.add(allTextField(text));
    }
    
    public static  Field allTextField(String text) {
        return new Field(ALL_TEXT_FIELD, text, Field.Store.NO, Field.Index.TOKENIZED);
    }   
    public static void addReadAcls(Document doc, AclContainer container, Object entry, AclManager aclManager) {
        // Add ACL field. We only need to index ACLs for read access. 
        Field racField;
        if (entry instanceof AclControlled) {
	        StringBuffer pIds = new StringBuffer();
	        //only want to index acls on the entry itself.  Otherwise we use READ_DEF_ACL
	        if (((AclControlled)entry).getInheritAclFromParent() == false) {
	        	Set readMemberIds = aclManager.getMembers(container, (AclControlled) entry, AccessType.READ);
	        	for(Iterator i = readMemberIds.iterator(); i.hasNext();) {
	        		pIds.append(i.next()).append(" ");
	        	}
	        }
	        // I'm not sure if putting together a long string value is more
	        // efficient than processing multiple short strings... We will see.
	        if (pIds.length() != 0)
	          racField = new Field(READ_ACL_FIELD, pIds.toString(), Field.Store.YES, Field.Index.TOKENIZED);
	        else
	          racField = new Field(READ_DEF_ACL_FIELD, READ_ACL_ALL, Field.Store.YES, Field.Index.TOKENIZED);
        }
        else {
            racField = new Field(READ_DEF_ACL_FIELD, READ_ACL_ALL, Field.Store.YES, Field.Index.TOKENIZED);
        }
        
        doc.add(racField);
    } 
    
    public static String buildAclTag(String tag, String aclId)
    {
    	String aclTag = BasicIndexUtils.TAG_ACL_PRE + aclId + BasicIndexUtils.TAG + tag;
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
