package com.sitescape.ef.search.docbuilder.mapped.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.util.ReflectHelper;

/**
 * @author Jong Kim
 *
 */
public class Getter {
	private static final Log logger = LogFactory.getLog(Getter.class);

	private Class classes[];
    private String propertyName;
    private String[] propertyNameElements;
    private Method methods[];
    
    public Getter(Class clazz, String propertyName, boolean includeInherited) {
        this.propertyName = propertyName;
        if(propertyName.indexOf(".") == -1)
            propertyNameElements = new String[] {propertyName};
        else
            propertyNameElements = propertyName.split("\\.");
        this.classes = new Class[propertyNameElements.length];
        this.methods = new Method[propertyNameElements.length];
        for(int i = 0; i < propertyNameElements.length; i++) {
            this.methods[i] = ReflectHelper.getterMethod(clazz, propertyNameElements[i], (i==0 && !includeInherited)? false:true);
            if(this.methods[i] == null)
                throw new PropertyNotFoundException("Could not find a getter for " + propertyNameElements[i] + " in class " + clazz.getName());
            classes[i] = clazz;
            clazz = methods[i].getReturnType();
        }
    }
    
    public Object get(Object target) throws PropertyAccessException {
        Object returnObj = null;
        for(int i = 0; target != null && i < methods.length; i++) {
            try {
                returnObj = methods[i].invoke(target, null);
            }
    		catch (InvocationTargetException e) {
    			throw new PropertyAccessException("Exception occurred inside", classes[i], propertyNameElements[i], e);
    		}
    		catch (IllegalAccessException e) {
    			throw new PropertyAccessException("IllegalAccessException occurred while calling", classes[i], propertyNameElements[i], e);
    		}
    		catch (IllegalArgumentException e) {
    			logger.error(
    				"IllegalArgumentException in class: " + classes[i].getName() + 
    				", getter method of property: " + propertyNameElements[i]
    			);
    			throw new PropertyAccessException("IllegalArgumentException occurred calling", classes[i], propertyNameElements[i], e);
    		}
            target = returnObj;
        }
        return returnObj;
    }
}
