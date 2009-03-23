/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.util.search.Constants;



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


		List fields = new ArrayList();
		
		fields.add(new Field(makeFieldName(dataElemName, Constants.EVENT_FIELD_START_DATE), DateTools.dateToString(event.getDtStart().getTime(),	DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED));
		fields.add(new Field(makeFieldName(dataElemName, Constants.EVENT_FIELD_END_DATE), DateTools.dateToString(event.getDtEnd().getTime(), DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED));
		if (!event.isAllDayEvent()) {
			fields.add(new Field(makeFieldName(dataElemName, Constants.EVENT_FIELD_TIME_ZONE_ID), event.getTimeZone().getID(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		}
		fields.add(new Field(makeFieldName(dataElemName, Constants.EVENT_FIELD_TIME_ZONE_SENSITIVE), Boolean.toString(event.isTimeZoneSensitive()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		fields.add(new Field(makeFieldName(dataElemName, Constants.EVENT_FIELD_FREE_BUSY), event.getFreeBusy().name(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		Field[] realFields = new Field[fields.size()];
		realFields = (Field[]) fields.toArray(realFields);
		return realFields;

	}


}
