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

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderDateTime extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default date implementation ignores args.  
        
        Date val = (Date) getFirstElement(dataElemValue);
        if(val == null) {
            return new Field[0];
        }
        else {
        	// all dates are relative to current time zone, so we need to remember hh:mm:ss
            Field field = new Field(makeFieldName(dataElemName), DateTools.dateToString(val,DateTools.Resolution.SECOND),Field.Store.YES,Field.Index.UN_TOKENIZED);
            return new Field[] {field};
        }
    }

}
