package com.sitescape.ef.module.definition.index;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.Event;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.search.BasicIndexUtils;

public class FieldBuilderEvent extends AbstractFieldBuilder {
    public String makeFieldName(String dataElemName, String fieldName) {
        //Just use the data name concatenated with the field name. It is guaranteed to be unique within its definition
        return dataElemName + BasicIndexUtils.DELIMITER + fieldName;
    }
	   protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
	        // This default text implementation ignores args.
	        
	        Event ev = (Event) getFirstElement(dataElemValue);
	        
	        if (ev == null)
	            return new Field[0];

			// range check to see if this event is in range
	    	Field evDtStartField = new Field(makeFieldName(dataElemName,EntityIndexUtils.EVENT_FIELD_START_DATE), DateTools.dateToString(ev.getDtStart().getTime(),DateTools.Resolution.SECOND),Field.Store.YES,Field.Index.UN_TOKENIZED);
		    Field evDtEndField = new Field(makeFieldName(dataElemName,EntityIndexUtils.EVENT_FIELD_END_DATE), DateTools.dateToString(ev.getDtEnd().getTime(),DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
	        
	        return new Field[] {evDtStartField, evDtEndField};
	    }

}
