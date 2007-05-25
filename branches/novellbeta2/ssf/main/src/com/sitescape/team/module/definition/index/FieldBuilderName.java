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

import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

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
