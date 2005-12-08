package com.sitescape.ef.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

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
    
}
