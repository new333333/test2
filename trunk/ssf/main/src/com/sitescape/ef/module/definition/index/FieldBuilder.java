package com.sitescape.ef.module.definition.index;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.DefinableEntity;

/**
 *
 * @author Jong Kim
 */
public interface FieldBuilder {
    
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args);
}
