package com.sitescape.team.survey;

import java.util.Iterator;

import junit.framework.TestCase;

public class SurveyTest extends TestCase {

	public void testCreateInstance() {
		Survey survey = new Survey(
				"{questions : [{'type':'multiple','question':'What do you think?','answers':[{'answer' : 'yes'}, {'answer' : 'no'}, {'answer' : 'never'}]}]}");
		System.out.println(survey.getSurveyModel());
//		System.out.println(survey.getQuestions().get(0));
//		assertEquals(survey.getQuestions().length, 1);
	}

}
