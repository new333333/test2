package com.sitescape.ef.search.docbuilder.mapped;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.sitescape.ef.util.EntityResolver;
import com.sitescape.ef.search.docbuilder.mapped.property.DefaultToDateConverter;
import com.sitescape.ef.search.docbuilder.mapped.property.DefaultToStringConverter;
import com.sitescape.ef.search.docbuilder.mapped.property.Getter;
import com.sitescape.ef.search.docbuilder.mapped.property.ToDateConverter;
import com.sitescape.ef.search.docbuilder.mapped.property.ToStringConverter;
import com.sitescape.ef.util.ReflectHelper;

/**
 * @author Jong Kim
 *
 */
public class ObjectIndexMapping implements InitializingBean {
    
	protected final Log logger = LogFactory.getLog(getClass());

	private static final String CLASS_NAME_ATTRIBUTE = "name";
	private static final String CLASS_DOCTYPE_ATTRIBUTE = "doctype";
    private static final String IDPROPERTY_ELEMENT = "idProperty";
    private static final String IDPROPERTY_NAME_ATTRIBUTE = "name";
    private static final String URL_ELEMENT = "url";
    private static final String URL_NAME_ATTRIBUTE = "name";
    private static final String URL_FIELD_ATTRIBUTE = "field";    
    private static final String PROPERTY_ELEMENT = "property";
    private static final String SUBCLASS_ELEMENT = "subclass";
    private static final String PROPERTY_NAME_ATTRIBUTE = "name";
    private static final String PROPERTY_TYPE_ATTRIBUTE = "type";
    private static final String TEXT_ELEMENT = "text";
    private static final String UNSTORED_ELEMENT = "unstored";
    private static final String UNINDEXED_ELEMENT = "unindexed";
    private static final String KEYWORD_ELEMENT = "keyword";
    private static final String FIELD_ELEMENT = "field";
    private static final String TEXT_NAME_ATTRIBUTE = "name";
    private static final String TEXT_STORE_TERM_VECTOR_ATTRIBUTE = "store-term-vector";
    private static final String UNSTORED_NAME_ATTRIBUTE = "name";
    private static final String UNSTORED_STORE_TERM_VECTOR_ATTRIBUTE = "store-term-vector";
    private static final String UNINDEXED_NAME_ATTRIBUTE = "name";
    private static final String KEYWORD_NAME_ATTRIBUTE = "name";
    private static final String KEYWORD_TYPE_ATTRIBUTE = "type";
    private static final String FIELD_NAME_ATTRIBUTE = "name";
    private static final String FIELD_STORE_ATTRIBUTE = "store";
    private static final String FIELD_INDEX_ATTRIBUTE = "index";
    private static final String FIELD_TOKEN_ATTRIBUTE = "token";
    private static final String FIELD_STORE_TERM_VECTOR_ATTRIBUTE = "store-term-vector";
    private static final String FIELD_BOOST_ATTRIBUTE = "boost";
    private static final String TO_STRING_CONVERTER_ELEMENT = "to-string-converter";
    private static final String TO_STRING_CONVERTER_CLASS_ATTRIBUTE = "class";
    private static final String TO_DATE_CONVERTER_ELEMENT = "to-date-converter";
    private static final String TO_DATE_CONVERTER_CLASS_ATTRIBUTE = "class";
    
    private static final String ID_NAME_DEFAULT = "id";
    private static final String URL_NAME_DEFAULT = "url";
    private static final boolean STORE_TERM_VECTOR_DEFAULT = false;
    private static final String KEYWORD_TYPE_DEFAULT = "java.lang.String";
    private static final boolean FIELD_STORE_DEFAULT = true;
    private static final boolean FIELD_INDEX_DEFAULT = true;
    private static final boolean FIELD_TOKEN_DEFAULT = true;
    
    private static final DefaultToStringConverter defToStringConverter = new DefaultToStringConverter();
    private static final DefaultToDateConverter defToDateConverter = new DefaultToDateConverter();
    
    private Map classMap = new HashMap(); // Map of class name to class object.
    
    private String mappingFilePath;

    public void afterPropertiesSet() throws Exception {
        Document doc = parse(new ClassPathResource(mappingFilePath));
        
        build(doc);        
    }
    
    public void setMappingFilePath(String mappingFilePath) {
        this.mappingFilePath = mappingFilePath;
    }
    
    public ClassSpec getClassSpec(String className) {
        return (ClassSpec) classMap.get(className);
    }
    
    private Document parse(Resource mappingFileResource) throws IOException, MappingException {
        SAXReader reader = new SAXReader(true);
        reader.setEntityResolver(new EntityResolver());
        Document doc;
        try {
            doc = reader.read(mappingFileResource.getInputStream());
        }  
        catch (DocumentException e) {
            logger.error("Could not configure mapping from file: " + 
                    mappingFilePath, e);
            throw new MappingException(e);
        }
        return doc;
    }
    
    private void build(Document doc) {
        Element root = doc.getRootElement();
        
        for(Iterator i = root.elementIterator(); i.hasNext();) {
            Element topClassElem = (Element) i.next();
            TopClass topClass = new TopClass();
            Element idElem = topClassElem.element(IDPROPERTY_ELEMENT);
            idElem.detach();
            topClass.doctype = topClassElem.attributeValue(CLASS_DOCTYPE_ATTRIBUTE);
            processClass(topClass, topClassElem, true);
            
            IdProperty idProperty = new IdProperty();
            idProperty.name = idElem.attributeValue(IDPROPERTY_NAME_ATTRIBUTE);
            if(idProperty.name == null)
                idProperty.name = ID_NAME_DEFAULT;
            idProperty.clazz = topClass.clazz;
            idProperty.toStringConverter = getToStringConverter(idElem.element(TO_STRING_CONVERTER_ELEMENT));
            idProperty.postProcessing();
            topClass.idProperty = idProperty;
        }
    }
    
    private void processClass(ClassSpec classSpec, Element classElem, boolean forTopClass) {
        classSpec.name = classElem.attributeValue(CLASS_NAME_ATTRIBUTE);
        try {
            classSpec.clazz = ReflectHelper.classForName(classSpec.name);
        } catch (ClassNotFoundException e) {
            throw new MappingException("Could not find class: " + classSpec.name, e);
        }
        classMap.put(classSpec.name, classSpec);
        for(Iterator i = classElem.elementIterator(); i.hasNext();) {
            Element childElem = (Element) i.next();
            if(childElem.getName().equals(URL_ELEMENT)) {
                UrlProperty urlProperty = new UrlProperty();
                urlProperty.name = childElem.attributeValue(URL_NAME_ATTRIBUTE);
                urlProperty.field = childElem.attributeValue(URL_FIELD_ATTRIBUTE);
                if(urlProperty.name == null)
                    urlProperty.name = URL_NAME_DEFAULT;
                if(urlProperty.field == null)
                    urlProperty.field = urlProperty.name;
                urlProperty.clazz = classSpec.clazz;
                urlProperty.postProcessing();
                classSpec.urlProperty = urlProperty;                
            }
            else if(childElem.getName().equals(PROPERTY_ELEMENT)) {
                Property property = processProperty(classSpec.clazz, childElem, forTopClass);
                classSpec.properties.put(property.getName(), property);
            }
            else if(childElem.getName().equals(SUBCLASS_ELEMENT)) {
                SubClass subClass = new SubClass();
                subClass.parentClass = classSpec;
                processClass(subClass, childElem, false);
            }                        
        }
    }
    
    private ToStringConverter getToStringConverter(Element toStringConverterElem) {
        ToStringConverter toStringConverter = defToStringConverter;
        if(toStringConverterElem != null) {
            toStringConverterElem.detach();
            String className = toStringConverterElem.attributeValue(TO_STRING_CONVERTER_CLASS_ATTRIBUTE);
            try {
                toStringConverter = (ToStringConverter) ReflectHelper.classForName(className).newInstance();
            } catch (InstantiationException e) {
        		throw new MappingException("InstantiationException occurred for " + className, e);
            } catch (IllegalAccessException e) {
        		throw new MappingException("IllegalAccessException occurred while instantiating " + className, e);
            } catch (ClassNotFoundException e) {
        		throw new MappingException("ClassNotFoundException occured for " + className, e);
            }
        }
        return toStringConverter;
    }
    
    private ToDateConverter getToDateConverter(Element toDateConverterElem) {
        ToDateConverter toDateConverter = defToDateConverter;
        if(toDateConverterElem != null) {
            toDateConverterElem.detach();
            String className = toDateConverterElem.attributeValue(TO_DATE_CONVERTER_CLASS_ATTRIBUTE);
            try {
                toDateConverter = (ToDateConverter) ReflectHelper.classForName(className).newInstance();
            } catch (InstantiationException e) {
        		throw new MappingException("InstantiationException occurred for " + className, e);
            } catch (IllegalAccessException e) {
        		throw new MappingException("IllegalAccessException occurred while instantiating " + className, e);
            } catch (ClassNotFoundException e) {
        		throw new MappingException("ClassNotFoundException occured for " + className, e);
            }
        }
        return toDateConverter;
    }
    
    private Property processProperty(Class clazz, Element propertyElem, boolean forTopClass) {
        Property property = new Property();
        property.clazz = clazz;
        property.name = propertyElem.attributeValue(PROPERTY_NAME_ATTRIBUTE);
        property.type = propertyElem.attributeValue(PROPERTY_TYPE_ATTRIBUTE);
        
        Element toStringConverterElem = (Element) propertyElem.element(TO_STRING_CONVERTER_ELEMENT);
        property.toStringConverter = getToStringConverter(toStringConverterElem);
        
        Element toDateConverterElem = (Element) propertyElem.element(TO_DATE_CONVERTER_ELEMENT);
        property.toDateConverter = getToDateConverter(toDateConverterElem);
                
        Element fieldSpecElem = (Element) propertyElem.elements().get(0);
        if(fieldSpecElem.getName().equals(TEXT_ELEMENT)) {
            FieldText field = new FieldText();
            field.name = fieldSpecElem.attributeValue(TEXT_NAME_ATTRIBUTE);
            field.storeTermVector = toBoolean(fieldSpecElem.attributeValue(TEXT_STORE_TERM_VECTOR_ATTRIBUTE), STORE_TERM_VECTOR_DEFAULT);
            property.fieldSpec = field;
        }
        else if(fieldSpecElem.getName().equals(UNSTORED_ELEMENT)) {
            FieldUnstored field = new FieldUnstored();
            field.name = fieldSpecElem.attributeValue(UNSTORED_NAME_ATTRIBUTE);
            field.storeTermVector = toBoolean(fieldSpecElem.attributeValue(UNSTORED_STORE_TERM_VECTOR_ATTRIBUTE), STORE_TERM_VECTOR_DEFAULT);
            property.fieldSpec = field;
        }
        else if(fieldSpecElem.getName().equals(UNINDEXED_ELEMENT)) {
            FieldUnindexed field = new FieldUnindexed();
            field.name = fieldSpecElem.attributeValue(UNINDEXED_NAME_ATTRIBUTE);
            property.fieldSpec = field;
        }
        else if(fieldSpecElem.getName().equals(KEYWORD_ELEMENT)) {
            FieldKeyword field = new FieldKeyword();
            field.name = fieldSpecElem.attributeValue(KEYWORD_NAME_ATTRIBUTE);
            field.type = fieldSpecElem.attributeValue(KEYWORD_TYPE_ATTRIBUTE);
            if(field.type == null)
                field.type = KEYWORD_TYPE_DEFAULT;
            property.fieldSpec = field;
        }
        else if(fieldSpecElem.getName().equals(FIELD_ELEMENT)) {
            FieldField field = new FieldField();
            field.name = fieldSpecElem.attributeValue(FIELD_NAME_ATTRIBUTE);
            field.store = toBoolean(fieldSpecElem.attributeValue(FIELD_STORE_ATTRIBUTE), FIELD_STORE_DEFAULT);
            field.index = toBoolean(fieldSpecElem.attributeValue(FIELD_INDEX_ATTRIBUTE), FIELD_INDEX_DEFAULT);
            field.token = toBoolean(fieldSpecElem.attributeValue(FIELD_TOKEN_ATTRIBUTE), FIELD_TOKEN_DEFAULT);
            field.storeTermVector = toBoolean(fieldSpecElem.attributeValue(FIELD_STORE_TERM_VECTOR_ATTRIBUTE), STORE_TERM_VECTOR_DEFAULT);
            property.fieldSpec = field;
        }
        return property.postProcessing(forTopClass);
    }
        
    private boolean toBoolean(String strValue, boolean nullDefault) {
        if(strValue == null)
            return nullDefault;
        else
            return Boolean.valueOf(strValue).booleanValue();
    }
    
    public static class ClassSpec {
        String name;
        Class clazz;
        UrlProperty urlProperty;
        Map properties = new HashMap(); // map of property names to property objects
        
        public Class getClazz() {
            return clazz;
        }
        public String getName() {
            return name;
        }
        public UrlProperty getUrlProperty() {
            return urlProperty;
        }
        public Map getProperties() {
            return properties; 
        }
    }
    
    public static class TopClass extends ClassSpec {
        IdProperty idProperty;
        String doctype;
        
        public IdProperty getIdProperty() {
            return idProperty;
        }
        public String getDocType() {
            return doctype;
        }
    }
    
    public static class SubClass extends ClassSpec {
        ClassSpec parentClass;
        
        public ClassSpec getParentClass() {
            return parentClass;
        }
    }
    
    public static class IdProperty {
        String name;
        Class clazz; // class this is a property of
        Getter getter;
        ToStringConverter toStringConverter;
        void postProcessing() {
            this.getter = new Getter(clazz, name, true);
        }        
        public Getter getGetter() {
            return getter;
        }
        public String getName() {
            return name;
        }
        public ToStringConverter getToStringConverter() {
            return toStringConverter;
        }
    }
    
    public static class UrlProperty {
        String name;
        String field;
        Class clazz; // class this is a property of
        Getter getter;
        void postProcessing() {
            this.getter = new Getter(clazz, name, true);
        }        
        public String getField() {
            return field;
        }
        public Getter getGetter() {
            return getter;
        }
        public String getName() {
            return name;
        }
    }
    
    public static class Property {
        String name;
        String type;
        Class clazz; // class this is a property of
        FieldSpec fieldSpec;
        Getter getter;
        ToStringConverter toStringConverter;
        ToDateConverter toDateConverter;
        Property postProcessing(boolean forTopClass) {
            this.getter = new Getter(clazz, name, ((forTopClass)? true:false));
            return this;
        }
        
        public Class getClazz() {
            return clazz;
        }
        public FieldSpec getFieldSpec() {
            return fieldSpec;
        }
        public Getter getGetter() {
            return getter;
        }
        public ToStringConverter getToStringConverter() {
            return toStringConverter;
        }
        public ToDateConverter getToDateConverter() {
            return toDateConverter;
        }
        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
    }

    public static class FieldSpec {
        String name;
        
        public String getName() {
            return name;
        }
    }
    
    public static class FieldText extends FieldSpec {
        boolean storeTermVector;
        
        public boolean getStoreTermVector() {
            return storeTermVector;
        }
    }
    
    public static class FieldUnstored extends FieldSpec {
        boolean storeTermVector;
        
        public boolean getStoreTermVector() {
            return storeTermVector;
        }
    }
    
    public static class FieldUnindexed extends FieldSpec {
    }
    
    public static class FieldKeyword extends FieldSpec {
        String type;
        
        public String getType() {
            return type;
        }
    }
    
    public static class FieldField extends FieldSpec {
        boolean store;
        boolean index;
        boolean token;
        boolean storeTermVector;
        Float boost;
        
        public Float getBoost() {
            return boost;
        }
        public boolean getIndex() {
            return index;
        }
        public boolean getStore() {
            return store;
        }
        public boolean getStoreTermVector() {
            return storeTermVector;
        }
        public boolean getToken() {
            return token;
        }
    }
}
