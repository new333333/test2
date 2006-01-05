package com.sitescape.ef.module.definition.index;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.Event;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.BasicIndexUtils;

public class FieldBuilderEvent extends AbstractFieldBuilder {
    public String makeFieldName(String dataElemName, String fieldName) {
        // e.g. data element name = "abc" -> field name = "_event#abc"
        return EntryIndexUtils.EVENT_FIELD + BasicIndexUtils.DELIMITER + dataElemName + BasicIndexUtils.DELIMITER + fieldName;
    }
	   protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
	        // This default text implementation ignores args.
	        
	        Event ev = (Event) getFirstElement(dataElemValue);
	        
	        if(ev == null)
	            return new Field[0];

			// range check to see if this event is in range
	    	Field evDtStartField = Field.Keyword(makeFieldName(dataElemName,EntryIndexUtils.EVENT_FIELD_START_DATE), ev.getDtStart().getTime());
		    Field evDtEndField = Field.Keyword(makeFieldName(dataElemName,EntryIndexUtils.EVENT_FIELD_END_DATE), ev.getDtEnd().getTime());
	        
	        return new Field[] {evDtStartField, evDtEndField};
	    }

}
