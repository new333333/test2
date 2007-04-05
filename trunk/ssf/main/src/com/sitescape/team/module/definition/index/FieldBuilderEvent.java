/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.definition.index;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;

import com.sitescape.team.domain.Event;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

public class FieldBuilderEvent extends AbstractFieldBuilder {
	
	public String makeFieldName(String dataElemName, String fieldName) {
		// Just use the data name concatenated with the field name. It is
		// guaranteed to be unique within its definition
		return dataElemName + BasicIndexUtils.DELIMITER + fieldName;
	}

	protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
		// This default text implementation ignores args.

		Event event = (Event) getFirstElement(dataElemValue);

		if (event == null)
			return new Field[0];

		// range check to see if this event is in range
		Field evDtStartField = new Field(makeFieldName(dataElemName, EntityIndexUtils.EVENT_FIELD_START_DATE), DateTools.dateToString(event.getDtStart().getTime(),	DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
		Field evDtEndField = new Field(makeFieldName(dataElemName, EntityIndexUtils.EVENT_FIELD_END_DATE), DateTools.dateToString(event.getDtEnd().getTime(), DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);

		return new Field[] { evDtStartField, evDtEndField};
	}


}
