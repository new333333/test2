package com.sitescape.team.module.definition.index;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.team.domain.Description;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

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
            
        Field allTextField = BasicIndexUtils.allTextField(text);
        
        Field descField = new Field(EntityIndexUtils.DESC_FIELD, text, Field.Store.YES, Field.Index.TOKENIZED); 

        
        return new Field[] {allTextField, descField};
    }

}
