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
import org.dom4j.Element;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.util.search.FieldFactory;

public class FieldBuilderCheckbox extends AbstractFieldBuilder {

    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
        
        Element entryElement = (Element)args.get(DefinitionModule.DEFINITION_ELEMENT);
        String caption = getEntryElementCaption(entity, dataElemName, entryElement);
        args.put(DefinitionModule.INDEX_CAPTION, caption);
        
        if(dataElemValue != null)
            return build(dataElemName, dataElemValue, args);
        else
            return null;
    }

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
    	String caption = (String) args.get(DefinitionModule.INDEX_CAPTION);
        Boolean val = (Boolean) getFirstElement(dataElemValue);
        if (val == null) {
            return new Field[0];
        }
        Field field = FieldFactory.createField(getSearchFieldName(dataElemName), val.toString(), getFieldStore(), getFieldIndex());
        if (!isFieldsOnly(args)) {
            Field generalTextField = BasicIndexUtils.generalTextField(caption);
        	return new Field[] {generalTextField, field};
        } else {
        	return new Field[] {field};
        }
    }

	@Override
	public String getSearchFieldName(String dataElemName) {
		return dataElemName;
	}

	@Override
	public String getSortFieldName(String dataElemName) {
		return getSearchFieldName(dataElemName);
	}

	@Override
	public Field.Index getFieldIndex() {
		return Field.Index.NOT_ANALYZED_NO_NORMS;
	}

	@Override
	public Field.Store getFieldStore() {
		return Field.Store.YES;
	}

}
