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

import com.sitescape.team.search.BasicIndexUtils;
/**
 *
 * @author Jong Kim
 */
public class FieldBuilderCheck extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default checkbox implementation ignores args.  

        Boolean val = (Boolean) getFirstElement(dataElemValue);
        if(val == null) {
            return new Field[0];
        }
        else {
            Field field = new Field(makeFieldName(dataElemName), val.toString(), Field.Store.NO, Field.Index.UN_TOKENIZED);
            return new Field[] {field};
        }
    }

}
