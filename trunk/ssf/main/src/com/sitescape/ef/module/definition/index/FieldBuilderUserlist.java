package com.sitescape.ef.module.definition.index;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderUserlist extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default radio implementation ignores args.  
        
        Field[] fields = new Field[dataElemValue.size()];
        String fieldName = makeFieldName(dataElemName);
       
        Long val;
        Field field;
        int i = 0;
        for(Iterator it = dataElemValue.iterator(); it.hasNext(); i++) {
	        val = Long.valueOf((String)it.next());
	        field = new Field(fieldName, val.toString(), Field.Store.NO, Field.Index.UN_TOKENIZED);
	        fields[i] = field;
        }
        
        return fields;
    }
}
