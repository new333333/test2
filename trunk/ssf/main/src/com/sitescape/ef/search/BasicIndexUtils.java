package com.sitescape.ef.search;

import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclManager;

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

    // Defines field values
    public static final String READ_ACL_ALL = "all";

    // The following fields represent valid values for DOC_TYPE_FIELD.
    
    public final static String DOC_TYPE_ENTRY 		= "entry";
    public final static String DOC_TYPE_ATTACHMENT	= "attachment"; 
    
    private static final String UID_DELIM = "_";
    
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
        doc.add(Field.Keyword(UID_FIELD, uid));    
        doc.add(new Field(THIS_CLASS_FIELD, getClassName(uid), false, true, false));
    }
    
    public static void addDocType(Document doc, String docType) {
        doc.add(Field.Keyword(DOC_TYPE_FIELD, docType));
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
        return new Field(ALL_TEXT_FIELD, text, false, true, true);
    }   
    public static void addReadAcls(Document doc, Binder binder, Entry entry, AclManager aclManager) {
        // Add ACL field. We only need to index ACLs for read access. 
        Field racField;
        if(entry instanceof AclControlledEntry) {
	        StringBuffer pIds = new StringBuffer();
	        Set readMemberIds = aclManager.getMembers(binder, (AclControlledEntry) entry, AccessType.READ);
	        for(Iterator i = readMemberIds.iterator(); i.hasNext();) {
	            pIds.append(i.next()).append(" ");
	        }
	        // I'm not sure if putting together a long string value is more
	        // efficient than processing multiple short strings... We will see.
	        if (pIds.length() != 0)
	          racField = new Field(READ_ACL_FIELD, pIds.toString(), true, true, true);
	        else
	          racField = new Field(READ_DEF_ACL_FIELD, READ_ACL_ALL, true, true, true);
        }
        else {
            racField = new Field(READ_DEF_ACL_FIELD, READ_ACL_ALL, true, true, true);
        }
        
        doc.add(racField);
    }    
}
