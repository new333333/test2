/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.definition.index;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.util.cal.Duration;
import org.kablink.util.search.Constants;
import org.kablink.util.search.FieldFactory;

public class FieldBuilderEvent extends AbstractFieldBuilder {
	
	protected String makeFieldName(String dataElemName, String fieldName) {
		// Just use the data name concatenated with the field name. It is
		// guaranteed to be unique within its definition
		return dataElemName + BasicIndexUtils.DELIMITER + fieldName;
	}

	@SuppressWarnings("unchecked")
	protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
		// This default text implementation ignores args.

		Event event = (Event) getFirstElement(dataElemValue);

		if (event == null)
			return new Field[0];


		List fields = new ArrayList();
		
		buildEventDateIndex(fields, dataElemName, Constants.EVENT_FIELD_START_DATE,         event.getDtStart());
		buildEventDateIndex(fields, dataElemName, Constants.EVENT_FIELD_CALC_START_DATE,    event.getDtCalcStart());
		buildEventDateIndex(fields, dataElemName, Constants.EVENT_FIELD_LOGICAL_START_DATE, event.getLogicalStart());
		buildEventDateIndex(fields, dataElemName, Constants.EVENT_FIELD_END_DATE,           event.getDtEnd());
		buildEventDateIndex(fields, dataElemName, Constants.EVENT_FIELD_CALC_END_DATE,      event.getDtCalcEnd());
		buildEventDateIndex(fields, dataElemName, Constants.EVENT_FIELD_LOGICAL_END_DATE,   event.getLogicalEnd());
		if (!event.isAllDayEvent()) {
			fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(dataElemName, Constants.EVENT_FIELD_TIME_ZONE_ID), event.getTimeZone().getID()));
		}
		Duration dur = event.getDuration();
		if (null != dur) {
			String durField = makeFieldName(dataElemName, Constants.EVENT_FIELD_DURATION);
			fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(durField, Constants.DURATION_FIELD_SECONDS), String.valueOf(dur.getSeconds())));
			fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(durField, Constants.DURATION_FIELD_MINUTES), String.valueOf(dur.getMinutes())));
			fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(durField, Constants.DURATION_FIELD_HOURS),   String.valueOf(dur.getHours())));
			fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(durField, Constants.DURATION_FIELD_DAYS),    String.valueOf(dur.getDays())));
			fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(durField, Constants.DURATION_FIELD_WEEKS),   String.valueOf(dur.getWeeks())));
		}
		fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(dataElemName, Constants.EVENT_FIELD_TIME_ZONE_SENSITIVE), Boolean.toString(event.isTimeZoneSensitive())));
		fields.add(FieldFactory.createFieldStoredNotAnalyzed(makeFieldName(dataElemName, Constants.EVENT_FIELD_FREE_BUSY), event.getFreeBusy().name()));
		Field[] realFields = new Field[fields.size()];
		realFields = (Field[]) fields.toArray(realFields);
		return realFields;

	}
	
	@SuppressWarnings("unchecked")
	private void buildEventDateIndex(List fields, String dataElemName, String fieldName, Calendar date) {
		if (null != date) {
			fields.add(
					FieldFactory.createFieldStoredNotAnalyzed(
					makeFieldName(dataElemName, fieldName),
					DateTools.dateToString(date.getTime(), DateTools.Resolution.SECOND)));
		}
	}

	@Override
	public String getSearchFieldName(String dataElemName) {
		// This data element maps to multiple fields.
		return null;
	}

	@Override
	public String getSortFieldName(String dataElemName) {
		// This data element does not support sorting.
		return null;
	}

	@Override
	public Field.Index getFieldIndex() {
		return Field.Index.NOT_ANALYZED_NO_NORMS;
	}

	@Override
	public Field.Store getFieldStore() {
		return Field.Store.YES;
	}

}
