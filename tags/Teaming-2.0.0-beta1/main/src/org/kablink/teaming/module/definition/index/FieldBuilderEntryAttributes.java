/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.module.definition.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.search.BasicIndexUtils;


/**
 *
 * @author Jong Kim
 */
public class FieldBuilderEntryAttributes extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        //Just use the data name. It is guaranteed to be unique within its definition
    	return dataElemName;
    }
    
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
        if (dataElemValue == null) return null;
        
        //Build a map of all of the attribute sets
        Map dataElemMap = new HashMap();
        dataElemMap.put(dataElemName, dataElemValue);
        Set dataElemSet = new HashSet();
        dataElemSet.add(dataElemMap);
        
        //The value set of the element is the list of attribute sets
        Iterator itNames = dataElemValue.iterator();
        while (itNames.hasNext()) {
        	//Get the attribute settings for each attribute set
        	String attributeSetName = (String)itNames.next();
            Set dataElemAttributeSetValue = getEntryElementValue(entity, dataElemName+DefinitionModule.ENTRY_ATTRIBUTES_SET+attributeSetName);
            dataElemMap.put(dataElemName + DefinitionModule.ENTRY_ATTRIBUTES_SET + attributeSetName, dataElemAttributeSetValue);
        }
        
       	fieldsOnly = (Boolean)args.get(DefinitionModule.INDEX_FIELDS_ONLY);
        if (fieldsOnly == null) fieldsOnly = Boolean.FALSE;
        
        return build(dataElemName, dataElemSet, args);
    }

    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
    	if (dataElemValue.size() != 1) return null;
    	
    	Map dataElemMap = (Map) dataElemValue.iterator().next();
        Iterator itNames = ((Set)dataElemMap.get(dataElemName)).iterator();
        int size = 0;
        //Calculate the size of the field array to be returned
        while (itNames.hasNext()) {
        	String attributeSetName = (String)itNames.next();
        	Set attributeSet = (Set)dataElemMap.get(dataElemName + DefinitionModule.ENTRY_ATTRIBUTES_SET + attributeSetName);
        	if (attributeSet != null) size = size + attributeSet.size();
        }
    	Field[] fields = new Field[size];

        //Now build the fields for each attribute set
    	itNames = ((Set)dataElemMap.get(dataElemName)).iterator();
        int i = 0;
        while (itNames.hasNext()) {
        	String attributeSetName = (String)itNames.next();
        	Set attributeSet = (Set)dataElemMap.get(dataElemName + DefinitionModule.ENTRY_ATTRIBUTES_SET + attributeSetName);
            if (attributeSet != null) {
            	String fieldName = makeFieldName(dataElemName + DefinitionModule.ENTRY_ATTRIBUTES_SET + attributeSetName);
                String val;
                Field field;
                for (Iterator it = attributeSet.iterator(); it.hasNext(); i++) {
                    val = (String) it.next();
        	        field = new Field(fieldName, val, Field.Store.YES, Field.Index.UN_TOKENIZED);
        	        fields[i] = field;
                }
            }
        }
        return fields;
    }
}
