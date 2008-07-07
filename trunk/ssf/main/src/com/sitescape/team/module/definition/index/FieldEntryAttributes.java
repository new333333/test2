package com.sitescape.team.module.definition.index;

import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.module.definition.DefinitionModule;

public class FieldEntryAttributes extends AbstractFieldBuilder {

    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args) {
        Set dataElemValue = getEntryElementValue(entity, dataElemName);
       	fieldsOnly = (Boolean)args.get(DefinitionModule.INDEX_FIELDS_ONLY);
        if (fieldsOnly == null) fieldsOnly = Boolean.FALSE;
        
        if(dataElemValue != null)
            return build(dataElemName, dataElemValue, args);
        else
            return null;
    }
	@Override
	protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
		// TODO Auto-generated method stub
		return null;
	}

}
