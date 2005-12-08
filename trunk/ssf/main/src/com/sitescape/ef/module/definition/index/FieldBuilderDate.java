package com.sitescape.ef.module.definition.index;

import java.util.Date;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderDate extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        // e.g. data element name = "abc" -> field name = "date#abc"
        return INDEXING_TYPE_DATE + DELIMITER + dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default date implementation ignores args.  
        
        Date val = (Date) getFirstElement(dataElemValue);
        if(val == null) {
            return new Field[0];
        }
        else {
            Field field = Field.Keyword(makeFieldName(dataElemName), val);
            return new Field[] {field};
        }
    }

}
