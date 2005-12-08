package com.sitescape.ef.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.module.folder.index.IndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderTitle extends AbstractFieldBuilder {

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default title implementation ignores args.  
    	
    	//This routine doesn't return anything because the title is indexed as part 
    	//  of the default data items (such as creationDate, modificationDate, etc)
    	return new Field[0];
        
        /**
         * 
         String val = (String) getFirstElement(dataElemValue);
         val = val.trim();
        
         if(val.length() == 0) {
            return new Field[0];
         }
         else {
	         Field allTextField = IndexUtils.allTextField(val);
	
	         Field titleField = new Field(IndexUtils.TITLE_FIELD, val, true, true, true); 
	            
	         Field title1Field = Field.Keyword(IndexUtils.TITLE1_FIELD, val.substring(0, 1));
	        
	         return new Field[] {allTextField, titleField, title1Field};
         }
        **/
    	
    }

}
