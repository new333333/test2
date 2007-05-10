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

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderProfileNames extends AbstractFieldBuilder {

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
