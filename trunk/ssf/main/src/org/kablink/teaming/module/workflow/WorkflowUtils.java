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
package org.kablink.teaming.module.workflow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.util.NLT;
import org.kablink.util.Validator;



/**
 * Helper to parse workflow definitions
 *
 */
public class WorkflowUtils {
        
    public static Map<String, Map> getManualTransitions(Definition wfDef, String stateName) {
		Map transitionData = new LinkedHashMap();
		Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all manual transitions for this state
			List transitions = stateEle.selectNodes("./item[@name='transitions']/item[@name='transitionManual']");
			if (transitions != null) {
				for (int j = 0; j < transitions.size(); j++) {
					Element transitionEle = (Element)transitions.get(j);
					Element transitionAccessEle = (Element) transitionEle.selectSingleNode("./item[@name='transitionManualAccess']");
					String toStateValue = DefinitionUtils.getPropertyValue(transitionEle, "toState");
					String toStateCaption = "";
					if (!Validator.isNull(toStateValue)) {
						//We have a transition. get the caption;
						Element toStateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", toStateValue);
						if (toStateEle != null) {
							toStateCaption = DefinitionUtils.getPropertyValue(toStateEle, "caption");
						}
						if (Validator.isNull(toStateCaption)) toStateCaption = toStateValue;
						//Ok, add this transition to the map
						Map data = new HashMap();
						data.put("toStateCaption", toStateCaption);
						data.put("transitionAccessElement", transitionAccessEle);
						transitionData.put(toStateValue, data);
					}
				}
			}
		}
		return transitionData;
    }

    /**
     * Return the set of states that this state can transition to
     * @param wfDef
     * @param stateName
     * @return transition to states
     */
    public static Set<String> getAllTransitions(Definition wfDef, String stateName) {
		Set transitionData = new HashSet();
		Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all transitions for this state
			List<Element> transitions = stateEle.selectNodes("./item[@name='transitions']/item[@type='transition']");
			if (transitions != null) {
				for (Element transitionEle: transitions) {
					String toStateValue = DefinitionUtils.getPropertyValue(transitionEle, "toState");
					if (Validator.isNotNull(toStateValue)) {
							transitionData.add(toStateValue);
					}
				}
			}
		}
		return transitionData;
    } 
	public static void setTransition(Definition wfDef, String stateName, String toStateName, String newState) {
		Document wfDoc = wfDef.getDefinitionForModificationPurpose();
		if (newState == null) newState = "";
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all transitions for this state
			List<Element> transitions = stateEle.selectNodes("./item[@name='transitions']/item[@type='transition']/properties/property[@name='toState' and @value='"+toStateName+"']");
			if (transitions != null) {
				for (Element transitionEle: transitions) {
					transitionEle.addAttribute("value", newState);
				}
			}
		}
		wfDef.setDefinition(wfDoc);
	}
    
   public static Map<String, Map> getQuestions(Definition wfDef, WorkflowState ws) {
	    String stateName = ws.getState();
	    DefinableEntity entry = ws.getOwner().getEntity();
		//now see if responses to this question exist
		Set<WorkflowResponse> respondersResponses = new HashSet<WorkflowResponse>();
		if (entry instanceof WorkflowSupport) respondersResponses = ((WorkflowSupport) entry).getWorkflowResponses();

		Map questionsData = new LinkedHashMap();
		Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all questions for this state
			List questions = stateEle.selectNodes("./item[@name='workflowQuestion']");
			if (questions != null) {
				for (int j = 0; j < questions.size(); j++) {
					Map questionData = new LinkedHashMap();
					String questionName = DefinitionUtils.getPropertyValue((Element)questions.get(j), "name");
					String questionText = DefinitionUtils.getPropertyValue((Element)questions.get(j), "question");
					Map responseData = new LinkedHashMap();
					questionData.put(ObjectKeys.WORKFLOW_QUESTION_TEXT, questionText);
					questionData.put(ObjectKeys.WORKFLOW_QUESTION_RESPONSES, responseData);
					List responses = ((Element)questions.get(j)).selectNodes("./item[@name='workflowResponse']");
					if (responses != null) {
						for (int k = 0; k < responses.size(); k++) {
							String responseName = DefinitionUtils.getPropertyValue((Element)responses.get(k), "name");
							String responseText = DefinitionUtils.getPropertyValue((Element)responses.get(k), "response");
							responseData.put(responseName, responseText);
						}
					}
					//Build a list of the responders to each response and count the number of responses
					Integer totalResponses = 0;
					List<Long> totalResponders = new ArrayList<Long>();
					Map<String,List> responseResponders = new HashMap<String,List>();
					questionData.put(ObjectKeys.WORKFLOW_QUESTION_RESPONSE_RESPONDERS, responseResponders);
					for (WorkflowResponse wr : respondersResponses) {
						if (wfDef.getId().equals(wr.getDefinitionId()) && 
								questionName.equals(wr.getName())) {
							if (!responseResponders.containsKey(wr.getResponse())) {
								responseResponders.put(wr.getResponse(), new ArrayList<Long>());
							}
							List responders = (List)responseResponders.get(wr.getResponse());
							if (!responders.contains(wr.getResponderId())) {
								responders.add(wr.getResponderId());
							}
							responseResponders.put(wr.getResponse(), responders);
							totalResponders.add(wr.getResponderId());
						}
					}
					questionData.put(ObjectKeys.WORKFLOW_QUESTION_RESPONDERS, totalResponders);
					questionData.put(ObjectKeys.WORKFLOW_QUESTION_RESPONSE_COUNT, totalResponses);
					questionData.put(ObjectKeys.WORKFLOW_QUESTION_EVERYONE_MUST_RESPOND, 
							WorkflowProcessUtils.checkIfEveryoneMustRespondToQuestion((WorkflowSupport)entry, ws, questionName));
					
					//Ok, add this question to the map
					questionsData.put(questionName, questionData);
				}
			}
		}
		return questionsData;
    }
   
    public static String getStateCaption(Definition wfDef, String state) {
	   	String stateCaption = "";
	   	//Find the actual caption of the state
	   	if (wfDef != null) {
	   		Document wfDefDoc = wfDef.getDefinition();
	       	Element stateProperty = (Element) wfDefDoc.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+state+"']");
	       	if (stateProperty != null) {
	       		Element statePropertyCaption = (Element) stateProperty.getParent().selectSingleNode("./property[@name='caption']");
	       		if (statePropertyCaption != null) stateCaption = statePropertyCaption.attributeValue("value", "");
	       	}
	       	if (stateCaption.equals("")) {
	       		stateCaption = state;
	       	} else {
	       		stateCaption = NLT.getDef(stateCaption);
	       	}
	   	}
	   	return stateCaption;
    }
   
    public static String getStateDescription(Definition wfDef, String state) {
	   	String stateDescription = "";
	   	//Find the actual caption of the state
	   	if (wfDef != null) {
	   		Document wfDefDoc = wfDef.getDefinition();
	       	Element stateProperty = (Element) wfDefDoc.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+state+"']");
	       	if (stateProperty != null) {
	       		Element statePropertyDescription = (Element) stateProperty.getParent().selectSingleNode("./property[@name='description']");
	       		if (statePropertyDescription != null) stateDescription = statePropertyDescription.getText();
	       	}
	   	}
	   	return stateDescription;
   }

    public static String getQuestionCaption(Definition wfDef, String state, String questionName) {
	   	String questionCaption = "";
	   	//Find the actual caption of the question
	   	if (wfDef != null) {
	   		Document wfDefDoc = wfDef.getDefinition();
	       	Element stateProperty = (Element) wfDefDoc.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+state+"']");
	       	if (stateProperty != null) {
		       	Element questionProperty = (Element) stateProperty.selectSingleNode("//item[@name='workflowQuestion']/properties/property[@name='name' and @value='"+questionName+"']");
		       	if (questionProperty != null) {
		       		Element questionPropertyCaption = (Element) questionProperty.getParent().selectSingleNode("./property[@name='question']");
		       		if (questionPropertyCaption != null) {
		       			questionCaption = questionPropertyCaption.attributeValue("value", "");
		       		}
		       		
		       	}

	       	}
	       	if (!questionCaption.equals("")) {
	       		questionCaption = NLT.getDef(questionCaption);
	       	}
	   	}
	   	return questionCaption;
    }
    
    public static String getResponseCaption(Definition wfDef, String state, String questionName, String responseName) {
	   	String responseCaption = "";
	   	//Find the actual caption of the question
	   	if (wfDef != null) {
	   		Document wfDefDoc = wfDef.getDefinition();
	       	Element stateProperty = (Element) wfDefDoc.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+state+"']");
	       	if (stateProperty != null) {
		       	Element questionProperty = (Element) stateProperty.selectSingleNode("//item[@name='workflowQuestion']/properties/property[@name='name' and @value='"+questionName+"']");
		       	if (questionProperty != null) {
		       		Element responseProperty = (Element) questionProperty.getParent().getParent().selectSingleNode("//item[@name='workflowResponse']/properties/property[@name='name' and @value='"+responseName+"']");
			       	if (responseProperty != null) {
			       		Element responsePropertyCaption = (Element) responseProperty.getParent().selectSingleNode("./property[@name='response']");
			       		if (responsePropertyCaption != null) {
			       			responseCaption = responsePropertyCaption.attributeValue("value", "");
			       		}
			       	}
		       	}
	       	}
	       	if (!responseCaption.equals("")) {
	       		responseCaption = NLT.getDef(responseCaption);
	       	}
	   	}
	   	return responseCaption;
    }
    
    public static String getThreadCaption(Definition wfDef, String thread) {
    	String threadCaption = "";
    	//Find the actual caption of the thread
    	if (wfDef != null) {
    		Document wfDefDoc = wfDef.getDefinition();
        	Element threadProperty = (Element) wfDefDoc.getRootElement().selectSingleNode("//item[@name='parallelThread' or @name='parallelProcess']/properties/property[@name='name' and @value='"+thread+"']");
        	if (threadProperty != null) {
        		Element threadPropertyCaption = (Element) threadProperty.getParent().selectSingleNode("./property[@name='caption']");
        		if (threadPropertyCaption != null) threadCaption = threadPropertyCaption.attributeValue("value", "");
        	}
        	if (threadCaption.equals("")) {
        		threadCaption = thread;
        	} else {
        		threadCaption = NLT.getDef(threadCaption);
        	}
    	}
    	return threadCaption;
    }
}
