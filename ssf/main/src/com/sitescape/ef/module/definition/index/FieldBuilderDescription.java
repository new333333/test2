package com.sitescape.ef.module.definition.index;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.Description;
import com.sitescape.ef.module.folder.index.IndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderDescription extends AbstractFieldBuilder {
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default text implementation ignores args.
        
        Description val = (Description) getFirstElement(dataElemValue);
        
        if(val == null)
            return new Field[0];

        String text = val.getText();
        
        if(text == null || text.length() == 0)
            return new Field[0];
            
        Field allTextField = IndexUtils.allTextField(text);
        
        Field descField = new Field(IndexUtils.DESC_FIELD, text, true, true, true); 

        
        return new Field[] {allTextField, descField};
    }

}
