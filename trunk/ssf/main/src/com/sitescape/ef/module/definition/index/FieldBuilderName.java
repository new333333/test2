package com.sitescape.ef.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderName extends AbstractFieldBuilder {

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        String val = (String) getFirstElement(dataElemValue);
         val = val.trim();
        
         if(val.length() == 0) {
            return new Field[0];
         }
         else {
	         Field allTextField = BasicIndexUtils.allTextField(val);
	
	         Field nameField = new Field(EntityIndexUtils.NAME_FIELD, val, Field.Store.YES, Field.Index.TOKENIZED); 
	            
	         Field name1Field = new Field(EntityIndexUtils.NAME1_FIELD, val.substring(0, 1), Field.Store.YES,Field.Index.UN_TOKENIZED);
	        
	         return new Field[] {allTextField, nameField, name1Field};
         }
    }

}
