package com.sitescape.ef.module.definition.index;

import java.util.Date;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;

import com.sitescape.team.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderDate extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default date implementation ignores args.  
        
        Date val = (Date) getFirstElement(dataElemValue);
        if(val == null) {
            return new Field[0];
        }
        else {
            Field field = new Field(makeFieldName(dataElemName), DateTools.dateToString(val,DateTools.Resolution.SECOND),Field.Store.YES,Field.Index.UN_TOKENIZED);
            return new Field[] {field};
        }
    }

}
