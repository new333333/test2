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

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.team.domain.Description;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderDescription extends AbstractFieldBuilder {
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default text implementation ignores args.
        
        Description val = (Description) getFirstElement(dataElemValue);
        
        if(val == null)
            return new Field[0];

        String text = val.getText();
        
        if(text == null || text.length() == 0)
            return new Field[0];
            
        Field allTextField = BasicIndexUtils.allTextField(text);
        //only real description field is stored as a field
        if ("description".equals(dataElemName)) {
        	Field descField = new Field(EntityIndexUtils.DESC_FIELD, text, Field.Store.YES, Field.Index.TOKENIZED); 
            return new Field[] {allTextField, descField};
        } else {
            return new Field[] {allTextField};
       }
     }

}
