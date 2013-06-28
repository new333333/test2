/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;

/**
 * @author jong
 *
 */
public class DefaultQueryParser extends MultiFieldQueryParser {

	private String[] termQueryOnlyFields;
	
	public DefaultQueryParser(Version matchVersion, String[] fields,
			Analyzer analyzer) {
		this(matchVersion, fields, analyzer, null);
	}

	public DefaultQueryParser(Version matchVersion, String[] fields,
			Analyzer analyzer, String[] termQueryOnlyFields) {
		super(matchVersion, fields, analyzer);
		this.termQueryOnlyFields = termQueryOnlyFields;
	}

	@Override
	protected Query getFieldQuery(String field, String queryText, boolean quoted)  throws ParseException {
		if(field != null && termQueryOnlyFields != null && isTermQueryOnlyField(field)) {
			// The field is known to be a single term field with no text analysis applied to the value.
			// In this case, we short circuit the normal flow of control and instead create
			// a single term query from the entire query text value. Otherwise, the default
			// implementation in the super class will apply text analysis (e.g. tokenization,
			// stemming and stop word removal, etc.) to the query text  which can yield 
			// unexpected/undesirable search result.
			return new TermQuery(new org.apache.lucene.index.Term(field, queryText));
		}
		else {
			return super.getFieldQuery(field, queryText, quoted);
		}
	}
	
	private boolean isTermQueryOnlyField(String field) {
		for(String termQueryOnlyField : termQueryOnlyFields) {
			if(termQueryOnlyField.equals(field))
				return true;
		}
		return false;
	}
}
