package com.sitescape.ef.search.docbuilder.mapped.property;

import java.util.Date;

/**
 * @author Jong Kim
 *
 */
public class DefaultToDateConverter implements ToDateConverter {

    public Date toDate(Object propertyValue) {
        if(propertyValue == null)
            return null;
        
        if(propertyValue instanceof Date)
            return (Date) propertyValue;
        
        throw new ConverterException("Do not know how to convert " + propertyValue.getClass().getName() + " to java.util.Date");
    }
}
