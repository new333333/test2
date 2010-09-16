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

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.util.search.Constants;

public abstract class FieldBuilderGeneric extends AbstractFieldBuilder {
	
	public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
	}
   
	protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
		String strToIndex = getStringToIndex(dataElemValue);
		if(strToIndex != null) {
			Field sortField = null;
			if(getSortFieldNeeded(args))
				sortField = new Field(Constants.SORT_FIELD_PREFIX + makeFieldName(dataElemName), strToIndex.toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED); 
           	Field field = new Field(makeFieldName(dataElemName), strToIndex, getFieldStore(), getFieldIndex()); 
           	Field allTextField = null;
           	if (!isFieldsOnly(args))
           		allTextField =  BasicIndexUtils.allTextField(strToIndex);
           	if(sortField != null) {
           		if(allTextField != null)
           			return new Field[] {field, sortField, allTextField};
           		else
           			return new Field[] {field, sortField};
           	}
           	else {
           		if(allTextField != null)
           			return new Field[] {field, allTextField};
           		else
           			return new Field[] {field};
           	}
		}
		else {
			return new Field[0];
		}
    }

	// Default implementation. This can be overriden by subclass.
	protected String getStringToIndex(Set dataElemValue) {
		String result = null;
		Object val = getFirstElement(dataElemValue);
		if(val instanceof String) {
			String sVal = (String) val;
			sVal = sVal.trim();
			if(sVal.length() > 0)
				result = sVal;
		}
		return result;
	}
	
	protected abstract boolean isSortFieldNeeded();
	
	protected abstract Field.Store getFieldStore();

	protected abstract Field.Index getFieldIndex();
	
	private boolean getSortFieldNeeded(Map args) {
		boolean sortFieldNeeded = isSortFieldNeeded();
		if(isSortFieldNeeded(args) != null) {
			// Runtime argument takes precedence over the static attribute of the class.
			// This flexibility allows multiple data types re-use the same class.
			sortFieldNeeded = isSortFieldNeeded(args).booleanValue();
		}
		return sortFieldNeeded;
	}
	
}
