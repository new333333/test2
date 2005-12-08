package com.sitescape.ef.search.docbuilder.mapped.property;

/**
 * @author Jong Kim
 *
 */
public class YesNoBooleanConverter implements ToStringConverter {

    public String toString(Object propertyValue) {
        Boolean b = (Boolean) propertyValue;
        if (b.equals(Boolean.TRUE))
            return "yes";
        else
            return "no";
    }
}
