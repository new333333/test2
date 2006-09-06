package com.sitescape.ef.search.docbuilder.mapped;

import java.util.Date;
import java.util.Iterator;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.docbuilder.IndexDocumentBuilder;

/**
 * @author Jong Kim
 *
 */
public class MappedIndexDocumentBuilder implements IndexDocumentBuilder {

    // Use a single field name for all class names regardless of the depth
    // of class hierarchy expressed in the mapping file. This implies that,
    // for a class, all of its super-class names, if specified, are indexed 
    // against the same field, which would allow a classname-based query
    // to match a document (ie, an instance of a class) as long as the
    // document represents an instance of either the specified class or a 
    // subclass of the specified class. In other words, polymorphic query.

    private ObjectIndexMapping objectIndexMapping;

    public ObjectIndexMapping getObjectIndexMapping() {
        return objectIndexMapping;
    }
    public void setObjectIndexMapping(ObjectIndexMapping objectIndexMapping) {
        this.objectIndexMapping = objectIndexMapping;
    }
    
    public Document buildIndexDocument(Object obj) {
        String className = obj.getClass().getName();
        ObjectIndexMapping.ClassSpec classSpec = objectIndexMapping.getClassSpec(className);
        if(classSpec == null) {
            throw new ClassNotMappedException("Mapping not defined for class [" + obj.getClass().getName() + "]");
        }
        Document doc = new Document();
        
        processProperties(obj, doc, classSpec, className);
        
        return doc;
    }
    
    public String obtainIndexDocumentUid(Object obj) {
        String className = obj.getClass().getName();
        ObjectIndexMapping.ClassSpec classSpec = objectIndexMapping.getClassSpec(className);
        if(classSpec == null) {
            throw new ClassNotMappedException("Mapping not defined for class [" + obj.getClass().getName() + "]");
        }
               
        // Find top class spec
        while(classSpec instanceof ObjectIndexMapping.SubClass)
            classSpec = ((ObjectIndexMapping.SubClass) classSpec).getParentClass();
        
        ObjectIndexMapping.IdProperty idProperty = ((ObjectIndexMapping.TopClass) classSpec).getIdProperty();
        Object idPropertyValue = idProperty.getGetter().get(obj);
        String idPropertyStrValue = idProperty.getToStringConverter().toString(idPropertyValue);
        
        return BasicIndexUtils.makeUid(className, idPropertyStrValue);
    }
    
    /*
    public Term toTerm(Object obj) {
        String className = obj.getClass().getName();
        ObjectIndexMapping.ClassSpec classSpec = objectIndexMapping.getClassSpec(className);
        if(classSpec == null) {
            throw new ClassNotMappedException("Mapping not defined for class [" + obj.getClass().getName() + "]");
        }
               
        // Find top class spec
        while(classSpec instanceof ObjectIndexMapping.SubClass)
            classSpec = ((ObjectIndexMapping.SubClass) classSpec).getParentClass();
        
        ObjectIndexMapping.IdProperty idProperty = ((ObjectIndexMapping.TopClass) classSpec).getIdProperty();
        Object idPropertyValue = idProperty.getGetter().get(obj);
        String idPropertyStrValue = idProperty.getToStringConverter().toString(idPropertyValue);
        
        return new Term(IndexUtils.UID_FIELD, IndexUtils.getUid(className, idPropertyStrValue));
    }*/
    
    private void processProperties(Object obj, Document doc, ObjectIndexMapping.ClassSpec classSpec, String thisClassName) {
        if(classSpec instanceof ObjectIndexMapping.SubClass) {
            ObjectIndexMapping.ClassSpec parentClassSpec = ((ObjectIndexMapping.SubClass) classSpec).getParentClass();
            //Field otherClassesField = new Field(IndexUtils.OTHER_CLASSES_FIELD, parentClassSpec.getName(), false, true, false);
            //doc.add(otherClassesField);
            processProperties(obj, doc, parentClassSpec, thisClassName);
        }
        else {
            ObjectIndexMapping.TopClass topClass = (ObjectIndexMapping.TopClass) classSpec;
            BasicIndexUtils.addDocType(doc, topClass.getDocType());
            ObjectIndexMapping.IdProperty idProperty = topClass.getIdProperty();
            Object idPropertyValue = idProperty.getGetter().get(obj);
            String idPropertyStrValue = idProperty.getToStringConverter().toString(idPropertyValue);
            BasicIndexUtils.addUid(doc, BasicIndexUtils.makeUid(thisClassName, idPropertyStrValue));            
        }
        
        ObjectIndexMapping.UrlProperty urlProperty = classSpec.getUrlProperty();
        if(urlProperty != null) {
            String urlPropertyValue = (String) urlProperty.getGetter().get(obj);
            Field urlField = new Field(urlProperty.getField(), urlPropertyValue, Field.Store.YES, Field.Index.NO);
            doc.add(urlField);
        }
        
        for(Iterator i = classSpec.getProperties().values().iterator(); i.hasNext();) {
            ObjectIndexMapping.Property property = (ObjectIndexMapping.Property) i.next();
            Field field = toField(obj, property);
            if(field != null)
                doc.add(field);
        }
    }
    
    private Field toField(Object obj, ObjectIndexMapping.Property property) {        
        Object propertyValue = property.getGetter().get(obj);
        
        if(propertyValue == null)
            return null;
        
        ObjectIndexMapping.FieldSpec fieldSpec = property.getFieldSpec();
        Field field = null;
        if(fieldSpec instanceof ObjectIndexMapping.FieldText) {
            ObjectIndexMapping.FieldText fieldText = (ObjectIndexMapping.FieldText) fieldSpec;
            String propertyStrValue = property.getToStringConverter().toString(propertyValue);
            field = new Field(fieldText.getName(), propertyStrValue, Field.Store.YES, Field.Index.TOKENIZED, 
            		(fieldText.getStoreTermVector() ? Field.TermVector.YES:Field.TermVector.NO) );
        }
        else if(fieldSpec instanceof ObjectIndexMapping.FieldUnstored) {
            ObjectIndexMapping.FieldUnstored fieldUnstored = (ObjectIndexMapping.FieldUnstored) fieldSpec;
            String propertyStrValue = property.getToStringConverter().toString(propertyValue);
            field = new Field(fieldUnstored.getName(), propertyStrValue, Field.Store.NO, Field.Index.TOKENIZED,
            		(fieldUnstored.getStoreTermVector() ? Field.TermVector.YES:Field.TermVector.NO) );
        }
        else if(fieldSpec instanceof ObjectIndexMapping.FieldUnindexed) {
            ObjectIndexMapping.FieldUnindexed fieldUnindexed = (ObjectIndexMapping.FieldUnindexed) fieldSpec;
            String propertyStrValue = property.getToStringConverter().toString(propertyValue);
            field = new Field(fieldUnindexed.getName(), propertyStrValue, Field.Store.YES, Field.Index.NO);            
        }
        else if(fieldSpec instanceof ObjectIndexMapping.FieldKeyword) {
            ObjectIndexMapping.FieldKeyword fieldKeyword = (ObjectIndexMapping.FieldKeyword) fieldSpec;
            if(fieldKeyword.getType().equals("java.util.Date")) {
                Date propertyDateValue = property.getToDateConverter().toDate(propertyValue);
                field = new Field(fieldKeyword.getName(), DateTools.dateToString(propertyDateValue,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
            }
            else { // java.lang.String
                String propertyStrValue = property.getToStringConverter().toString(propertyValue);
                field = new Field(fieldKeyword.getName(), propertyStrValue, Field.Store.YES, Field.Index.UN_TOKENIZED);
            }
        }
        else if(fieldSpec instanceof ObjectIndexMapping.FieldField) {
            ObjectIndexMapping.FieldField fieldField = (ObjectIndexMapping.FieldField) fieldSpec;
            String propertyStrValue = property.getToStringConverter().toString(propertyValue);
            field = new Field(fieldField.getName(), propertyStrValue, 
            			(fieldField.getStore() ? Field.Store.YES : Field.Store.NO),
            			(fieldField.getIndex() ? (fieldField.getToken() ? Field.Index.TOKENIZED : Field.Index.UN_TOKENIZED) : Field.Index.NO),
            			(fieldField.getStore() ? Field.TermVector.YES : Field.TermVector.NO));
        }
        return field;
    }
}
