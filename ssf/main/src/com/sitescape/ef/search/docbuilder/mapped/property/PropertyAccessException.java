package com.sitescape.ef.search.docbuilder.mapped.property;

import com.sitescape.ef.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class PropertyAccessException extends UncheckedException {
    public PropertyAccessException(String msg, Class clazz, String propertyName, Throwable cause) {
        super(msg + " getter of " + clazz.getName() + "." + propertyName, cause);
    } 
}
