package com.sitescape.ef.module.definition.index;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.domain.DefinableEntity;

/**
 *
 * @author Jong Kim
 */
public interface FieldBuilder {
    
    public static final String INDEXING_TYPE_TITLE			= "title";
    public static final String INDEXING_TYPE_ELEMENT		= "element";
    public static final String INDEXING_TYPE_DESCRIPTION 	= "desc";
    public static final String INDEXING_TYPE_TEXT			= "text";
    public static final String INDEXING_TYPE_SELECT			= "select";
    public static final String INDEXING_TYPE_CHECK			= "check";
    public static final String INDEXING_TYPE_DATE			= "date";
    public static final String INDEXING_TYPE_USERLIST		= "userlist";
    public static final String INDEXING_TYPE_NUMBER	 		= "number";
    public Field[] buildField(DefinableEntity entity, String dataElemName, Map args);
}
