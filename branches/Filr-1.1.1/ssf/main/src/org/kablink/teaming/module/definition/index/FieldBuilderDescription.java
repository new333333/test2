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
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Html;
import org.kablink.util.search.Constants;
import org.kablink.util.search.FieldFactory;

public class FieldBuilderDescription extends AbstractFieldBuilder {
    
	// This is a global setting, so we can safely use an instance variable
	// to store the value, without hindering multi-threaded accesses.
	boolean indexDescriptionFieldAsis; 
	
	public FieldBuilderDescription() {
		indexDescriptionFieldAsis = SPropsUtil.getBoolean("index.description.field.asis", false);
	}
	
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default text implementation ignores args.
        
        Object firstVal = getFirstElement(dataElemValue);
        if (firstVal != null && firstVal instanceof String) {
        	//Old code could have a String here. Turn it into a Description
        	firstVal = new Description((String) firstVal);
        }
        Description val = (Description) firstVal;
        
        if(val == null)
            return new Field[0];

        String text = val.getText();
        
        if(text == null || text.length() == 0)
            return new Field[0];
        
        String strippedText;
        
        if(indexDescriptionFieldAsis)
        	strippedText = text;
        else
        	strippedText = Html.stripHtml(text).trim();
            
        //only real description field is stored as a field
        if ("description".equals(dataElemName)) {
        	// This is the built-in static description element.
        	// TODO We should really make the handling of this static description element identical to that of custom description element. 
        	// From indexing point of view, it is easy enough. But fixing application accordingly requires some work, so deferred to the
        	// later release.
        	Field descField = FieldFactory.createField(Constants.DESC_FIELD, text, Field.Store.YES, Field.Index.NO); 
        	Field descTextField = FieldFactory.createFullTextFieldIndexed(Constants.DESC_TEXT_FIELD, strippedText, false); 
        	Field descFormatField = FieldFactory.createField(Constants.DESC_FORMAT_FIELD, String.valueOf(val.getFormat()), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS); 
        	// For the built-in description field, avoid indexing the same text twice. So don't add it in the catch-all field.
     		return new Field[] {descField, descTextField, descFormatField};
        }
        else {
        	// This is a custom description element.
        	// Use a single field to store the original text and also to index the stipped text.
        	// We will not bother with the format information for now.
        	// TODO We eventually want a single handling for both static and custom description element.
        	Field descField = FieldFactory.createField(dataElemName, text, Field.Store.YES, Field.Index.NO); 
        	Field descTextField = FieldFactory.createFullTextFieldIndexed(dataElemName, strippedText, false); 
         	if (isFieldsOnly(args)) {
         		return new Field[] {descField, descTextField};
         	} else {
        		Field generalTextField = BasicIndexUtils.generalTextField(strippedText);
         		return new Field[] {generalTextField, descField, descTextField};
         	}
        }
    }

	@Override
	public String getSearchFieldName(String dataElemName) {
		// This data element maps to multiple fields.
		return null;
	}

	@Override
	public String getSortFieldName(String dataElemName) {
		// This data element does not support sorting.
		return null;
	}

	@Override
	public Field.Index getFieldIndex() {
		return Field.Index.ANALYZED;
	}

	@Override
	public Field.Store getFieldStore() {
		return Field.Store.YES;
	}

}
