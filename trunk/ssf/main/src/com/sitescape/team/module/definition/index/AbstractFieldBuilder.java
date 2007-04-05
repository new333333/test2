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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.ObjectPropertyNotFoundException;

/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFieldBuilder implements FieldBuilder {

    
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
        
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
