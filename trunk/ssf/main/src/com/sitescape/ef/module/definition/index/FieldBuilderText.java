package com.sitescape.ef.module.definition.index;

import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.module.folder.index.IndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderText extends AbstractFieldBuilder {

    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default text implementation ignores args.
              
        /*
        boolean store = false;
        boolean index = true;
        boolean token = true;
        boolean storeTermVector = false;
        
        Boolean bool;
        if((bool = (Boolean) args.get("store")) != null)
            store = bool.booleanValue();
        if((bool = (Boolean) args.get("index")) != null)
            index = bool.booleanValue();
        if((bool = (Boolean) args.get("token")) != null)
            token = bool.booleanValue();
        if((bool = (Boolean) args.get("storeTermVector")) != null)
            storeTermVector = bool.booleanValue();
        */
        
        String val = (String) getFirstElement(dataElemValue);
        
        if(val == null) {
            return new Field[0];
        }
        else {
            Field field = IndexUtils.allTextField(val);
        
            return new Field[] {field};
        }
    }
}
