package com.sitescape.ef.search.docbuilder.mapped.property;

/**
 * @author Jong Kim
 *
 */
public class DefaultToStringConverter implements ToStringConverter {

    public String toString(Object propertyValue) {
        if(propertyValue == null)
            return null;
        else
            return propertyValue.toString();
    }
}
