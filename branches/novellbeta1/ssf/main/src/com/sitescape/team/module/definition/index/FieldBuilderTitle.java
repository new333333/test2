/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.team.module.folder.index.IndexUtils;

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
