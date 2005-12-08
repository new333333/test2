package com.sitescape.ef.util;

import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.ef.util.EntityResolver;

/**
 *
 * @author Jong Kim
 */
public class XmlClassPathConfigFiles extends ClassPathConfigFiles 
	implements InitializingBean {
    
    protected boolean validating = true;
    protected org.dom4j.Document[] docs; 
    
    public boolean isValidating() {
        return validating;
    }
    public void setValidating(boolean validating) {
        this.validating = validating;
    }
    
    public void afterPropertiesSet() throws Exception {
        int size = size();
        docs = new org.dom4j.Document[size];
        for(int i = 0; i < size; i++) {
            SAXReader reader = new SAXReader(validating);  
            if(validating) {
                // The following code turns on XML schema-based validation
                // features specific to Apache Xerces2 parser. Therefore it
                // will not work when a different parser is used. 
                reader.setFeature("http://apache.org/xml/features/validation/schema", true); // Enables XML Schema validation
                reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true); // Enables full (if slow) schema checking
            }
            reader.setEntityResolver(new EntityResolver());
            docs[i] = reader.read(this.getAsInputStream(i));
        }
    }
    
    /**
     * Returns the first file as <code>org.dom4j.Document</code>. 
     * 
     * @return
     */
    public org.dom4j.Document getAsDom4jDocument() {
        return getAsDom4jDocument(0);
    }
    
    /**
     * Returns the specified file as <code>org.dom4j.Document</code>. 
     * 
     * @param index
     * @return
     */
    public org.dom4j.Document getAsDom4jDocument(int index) {
        return docs[index];
    }
}
