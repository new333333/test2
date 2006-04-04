package com.sitescape.ef.module.definition.index;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.util.InvokeUtil;
import com.sitescape.ef.util.ObjectPropertyNotFoundException;

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
