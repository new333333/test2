package com.sitescape.ef.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderIcon extends AbstractFieldBuilder {

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
		return new Field[0];
    	/*
		String val = (String) getFirstElement(dataElemValue);
    	val = val.trim();
        
		if(val.length() == 0) {
			return new Field[0];
		}
		else {
		    Field allTextField = BasicIndexUtils.allTextField(val);
		
		    Field nameField = new Field(EntryIndexUtils.NAME_FIELD, val, true, true, true); 
		        
		    Field name1Field = Field.Keyword(EntryIndexUtils.NAME1_FIELD, val.substring(0, 1));
		    
		    return new Field[] {allTextField, nameField, name1Field};
		}
		*/
    }
}
