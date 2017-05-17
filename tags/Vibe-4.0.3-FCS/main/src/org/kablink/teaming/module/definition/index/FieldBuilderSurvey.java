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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.survey.Answer;
import org.kablink.teaming.survey.Question;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.survey.SurveyModel;
import org.kablink.util.Html;
import org.kablink.util.search.FieldFactory;

public class FieldBuilderSurvey extends AbstractFieldBuilder {
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
    	Object obj = getFirstElement(dataElemValue);
    	if(obj instanceof Survey) {
    		String fieldName = getSearchFieldName(dataElemName);
        	List<String> textsToIndex = extractsTextsToIndex((Survey) obj);
        	List<Field> fields = new ArrayList<Field>();
    		for(String text : textsToIndex) {
    			fields.add(FieldFactory.createFullTextFieldIndexed(fieldName, text, false));
        		if(!isFieldsOnly(args))
        			fields.add(BasicIndexUtils.generalTextField(text));
    		}
    		Field[] fieldArray = new Field[fields.size()];
    		return (Field[]) fields.toArray(fieldArray);
    	}
    	else {
        	return new Field[0];    		
    	}
    }

	@Override
	public String getSearchFieldName(String dataElemName) {
		return dataElemName;
	}

	@Override
	public String getSortFieldName(String dataElemName) {
		return null; // This element does not support sorting.
	}
	
	@Override
	public Field.Index getFieldIndex() {
		return Field.Index.ANALYZED;
	}

	@Override
	public Field.Store getFieldStore() {
		return Field.Store.NO;
	}
	
	protected List<String> extractsTextsToIndex(Survey survey) {
		List<String> texts = new ArrayList<String>();
		SurveyModel sm = survey.getSurveyModel();
		List<Question> questions = sm.getQuestions();
		for(Question q : questions) {
			texts.add(Html.stripHtml(q.getQuestion()).trim());
			List<Answer> answers = q.getAnswers();
			for(Answer a : answers)
				texts.add(a.getText());
		}
		return texts;
	}

}
