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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.dom4j.Element;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;


/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFieldBuilder implements FieldBuilder {
	protected Boolean fieldsOnly;
    
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
       	fieldsOnly = (Boolean)args.get(DefinitionModule.INDEX_FIELDS_ONLY);
        if (fieldsOnly == null) fieldsOnly = Boolean.FALSE;
        
        if(dataElemValue != null)
            return build(dataElemName, dataElemValue, args);
        else
            return null;
    }

    protected Set getEntryElementValue(DefinableEntity entity, String dataElemName) {
	    Object dataElemValue = null;
	    Set result = null;
	    try {
	        dataElemValue = InvokeUtil.invokeGetter(entity, dataElemName);
	    }
	    catch (ObjectPropertyNotFoundException pe) {
	        CustomAttribute cAttr = entity.getCustomAttribute(dataElemName);
	        if(cAttr != null)
	        	//let customAttribute do set conversion to handle comman separated values
	            result = cAttr.getValueSet();
		}
	    
	    
	    if ((result == null) && (dataElemValue != null)) {
	        if(dataElemValue instanceof Set) {
	            result = (Set) dataElemValue;
	        }
	        else {
		        result = new HashSet();
		        result.add(dataElemValue);	            
	        }
	    }
	    
	    return result;
    }
    
    protected Set getEntryElementValueCaptions(DefinableEntity entity, String dataElemName, Element entryElement) {
	    Object dataElemValue = null;
	    Set elemValues = null;
	    Set result = new HashSet();
	    try {
	        dataElemValue = InvokeUtil.invokeGetter(entity, dataElemName);
	    }
	    catch (ObjectPropertyNotFoundException pe) {
	        CustomAttribute cAttr = entity.getCustomAttribute(dataElemName);
	        if(cAttr != null)
	        	//let customAttribute do set conversion to handle comman separated values
	        	elemValues = cAttr.getValueSet();
		}
	    
	    
	    if ((elemValues == null) && (dataElemValue != null)) {
	        if(dataElemValue instanceof Set) {
	        	elemValues = (Set) dataElemValue;
	        }
	        else {
	        	elemValues = new HashSet();
	        	elemValues.add(dataElemValue);	            
	        }
	    }
	    //Get the caption for each value
	    if (elemValues != null) {
	    	Iterator itElemValues = elemValues.iterator();
		    while (itElemValues.hasNext()) {
		    	String valueName = (String)itElemValues.next();
		    	Element nameEle = (Element)entryElement.selectSingleNode("./item/properties/property[@name='name' and @value='"+valueName+"']");
		    	if (nameEle != null) {
		    		Element captionEle = (Element)nameEle.getParent().selectSingleNode("./property[@name='caption']");
			    	if (captionEle != null) result.add(captionEle.attributeValue("value", ""));
		    	}
		    }
	    }
	    
	    return result;
    }
    
    /*
    protected String makeFieldName(String dataElemName, String indexingType) {
        return indexingType + dataElemName;
    }*/
    
    protected abstract Field[] build(String dataElemName, Set dataElemValue, Map args);
    
    Object getFirstElement(Set set) {
        if(set.size() == 0)
            return null;
    
        return set.iterator().next();
    }
}
