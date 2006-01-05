package com.sitescape.ef.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;
import com.sitescape.ef.search.BasicIndexUtils;
/**
 *
 * @author Jong Kim
 */
public class FieldBuilderCheck extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        // e.g. data element name = "abc" -> field name = "check#abc"
        return INDEXING_TYPE_CHECK + BasicIndexUtils.DELIMITER + dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default checkbox implementation ignores args.  

        Boolean val = (Boolean) getFirstElement(dataElemValue);
        if(val == null) {
            return new Field[0];
        }
        else {
            Field field = new Field(makeFieldName(dataElemName), val.toString(), false, true, false);
            return new Field[] {field};
        }
    }

}
