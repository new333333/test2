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

import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderUserListSelect extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default radio implementation ignores args.  
        
        Field[] fields = new Field[dataElemValue.size()];
        String fieldName = makeFieldName(dataElemName);
       
        String val;
        Field field;
        int i = 0;
        for(Iterator it = dataElemValue.iterator(); it.hasNext(); i++) {
            val = (String) it.next();
	        field = new Field(fieldName, val, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        fields[i] = field;
        }
        
        return fields;
    }

}
