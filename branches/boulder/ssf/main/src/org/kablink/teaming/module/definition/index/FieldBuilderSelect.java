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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.dom4j.Element;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.search.BasicIndexUtils;


/**
 *
 * @author Jong Kim
 */
public class FieldBuilderSelect extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
       	fieldsOnly = (Boolean)args.get(DefinitionModule.INDEX_FIELDS_ONLY);
        if (fieldsOnly == null) fieldsOnly = Boolean.FALSE;
        
        Element entryElement = (Element)args.get(DefinitionModule.DEFINITION_ELEMENT);
        Set dataElemValueCaptions = getEntryElementValueCaptions(entity, dataElemName, entryElement);
        args.put(DefinitionModule.INDEX_CAPTION_VALUES, dataElemValueCaptions);
        
        if(dataElemValue != null)
            return build(dataElemName, dataElemValue, args);
        else
            return null;
    }

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default radio implementation ignores args.  
        
    	Set dataElemValueCaptions = (Set) args.get(DefinitionModule.INDEX_CAPTION_VALUES);
    	
    	Field[] fields = new Field[dataElemValue.size() + dataElemValueCaptions.size() + 1];
        String fieldName = makeFieldName(dataElemName);
       
        String val;
        String allText = "";
        Field field;
        int i = 1;
        for(Iterator it = dataElemValue.iterator(); it.hasNext(); i++) {
            val = (String) it.next();
	        field = new Field(fieldName, val, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        fields[i] = field;
        }
        
        fieldName = makeFieldName(DefinitionModule.CAPTION_FIELD_PREFIX + dataElemName);
        for(Iterator it = dataElemValueCaptions.iterator(); it.hasNext(); i++) {
            val = (String) it.next();
	        field = new Field(fieldName, val, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        fields[i] = field;
	        allText += " " + getNltTagInAllLanguages(val);
        }
        
        //Build the allText field
        Field allTextField = BasicIndexUtils.allTextField(allText);
        fields[0] = allTextField;
        
        return fields;
    }

}
