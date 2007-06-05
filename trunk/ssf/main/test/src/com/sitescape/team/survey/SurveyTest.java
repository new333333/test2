package com.sitescape.team.survey;

import junit.framework.TestCase;

public class SurveyTest extends TestCase {

	public void testCreateInstance() {
		Survey survey = new Survey(
				"{definition : [{\"type\":\"multiple\",\"question\":\"What do you think?\",\"answers\":[\"yes\",\"no\",\"never\"]}]}");
		assertEquals(survey.getQuestions().length, 1);
	}

}
