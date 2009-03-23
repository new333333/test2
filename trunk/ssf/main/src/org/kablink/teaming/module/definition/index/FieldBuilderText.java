/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.definition.index;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.util.search.Constants;


/**
 *
 * @author Jong Kim
 */
public class FieldBuilderText extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
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
           	Field sortTextField = new Field(makeFieldName(Constants.SORT_FIELD_PREFIX + dataElemName), val.toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED); 
           	Field textField = new Field(makeFieldName(dataElemName), val, Field.Store.YES, Field.Index.TOKENIZED); 
           	if (!fieldsOnly) {
               	Field allTextField = BasicIndexUtils.allTextField(val);
               	return new Field[] {allTextField, textField, sortTextField};
           	} else {
               	return new Field[] {textField, sortTextField};
           	}
        }
    }
}
