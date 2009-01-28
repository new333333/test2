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

import com.sitescape.team.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderText extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    public String makeSortName(String dataElemName) {
    	//Return _sort_dataElemName
    	return "_sort_" + dataElemName;
    }
    public String fixUpValue(String val) {
    	return val.toLowerCase();
    }
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default text implementation ignores args.
              
        /*
        boolean store = false;
        boolean index = true;
        boolean token = true;
        boolean storeTermVector = false;
        
        Boolean bool;
        if((bool = (Boolean) args.get("store")) != null)
            store = bool.booleanValue();
        if((bool = (Boolean) args.get("index")) != null)
            index = bool.booleanValue();
        if((bool = (Boolean) args.get("token")) != null)
            token = bool.booleanValue();
        if((bool = (Boolean) args.get("storeTermVector")) != null)
            storeTermVector = bool.booleanValue();
        */
        
        String val = (String) getFirstElement(dataElemValue);
        
        if(val == null) {
            return new Field[0];
        }
        else {
           	Field textField = new Field(makeFieldName(dataElemName), val, Field.Store.YES, Field.Index.TOKENIZED); 
           	Field sortField = new Field(makeSortName(dataElemName), fixUpValue(val), Field.Store.YES, Field.Index.UN_TOKENIZED); 
           	if (!fieldsOnly) {
               	Field allTextField = BasicIndexUtils.allTextField(val);
               	return new Field[] {allTextField, textField,sortField};
           	} else {
               	return new Field[] {textField,sortField};
           	}
        }
    }
}
