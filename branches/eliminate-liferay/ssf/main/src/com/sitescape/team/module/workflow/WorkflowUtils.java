/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.workflow;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.CommaSeparatedValue;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.WfAcl;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;


/**
 * @author hurley
 *
 */
public class WorkflowUtils {
        
    public static Map<String, String> getManualTransitions(Definition wfDef, String stateName) {
		Map transitionData = new LinkedHashMap();
		Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all manual transitions for this state
			List transitions = stateEle.selectNodes("./item[@name='transitions']/item[@name='transitionManual']");
			if (transitions != null) {
				for (int j = 0; j < transitions.size(); j++) {
					String toStateValue = DefinitionUtils.getPropertyValue((Element)transitions.get(j), "toState");
					String toStateCaption = "";
					if (!Validator.isNull(toStateValue)) {
						//We have a transition. get the caption;
						Element toStateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", toStateValue);
						if (toStateEle != null) {
							toStateCaption = DefinitionUtils.getPropertyValue(toStateEle, "caption");
						}
						if (Validator.isNull(toStateCaption)) toStateCaption = toStateValue;
						//Ok, add this transition to the map
						transitionData.put(toStateValue, toStateCaption);
					}
				}
			}
		}
		return transitionData;
    }

    public static Map<String, Map> getQuestions(Definition wfDef, String stateName) {
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


    public static WfAcl getStateAcl(Definition wfDef, DefinableEntity entity, String stateName, WfAcl.AccessType type) {
    	Document wfDoc = wfDef.getDefinition();
    	WfAcl acl = null;
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			String nodeString=null;
			if (WfAcl.AccessType.read.equals(type)) {
				nodeString = "item[@name='readAccess']";
			} else if (WfAcl.AccessType.write.equals(type)) {  
				nodeString = "item[@name='modifyAccess']";
			} else if (WfAcl.AccessType.delete.equals(type)) {
				nodeString = "item[@name='deleteAccess']";
			} else if (WfAcl.AccessType.transitionOut.equals(type)) {
				nodeString = "item[@name='transitionOutAccess']";
			} else if (WfAcl.AccessType.transitionIn.equals(type)) {
				nodeString = "item[@name='transitionInAccess']";
			} 
			if (nodeString != null) {
				acl = getAcl((Element)stateEle.selectSingleNode("./item[@name='accessControls']/" + nodeString), entity, type);
			}
			if (acl == null) {
				//check global settings
				acl = getAcl((Element)wfDoc.getRootElement().selectSingleNode("./item[@name='workflowProcess']/item[@name='accessControls']/" + nodeString), entity, type);
			}
			if (acl != null) return acl;
		} 
		acl = new WfAcl(type);
		acl.setUseDefault(true);
		return acl;
    }

    private static WfAcl getAcl(Element aclElement, DefinableEntity entity, WfAcl.AccessType type) {
     	if (aclElement == null) return null;
		List<Element>props = aclElement.selectNodes("./properties/property");
    	String name, value;
		if ((props == null) || props.isEmpty()) return null;
	   	WfAcl result = new WfAcl(type);
		for (Element prop:props) {
			name = prop.attributeValue("name","");
			value = prop.attributeValue("value","");
			if ("folderDefault".equals(name)) {
				result.setUseDefault(GetterUtil.getBoolean(value, false));
			} else if ("userGroupAccess".equals(name)) {
				result.addPrincipalIds(LongIdUtil.getIdsAsLongSet(value));
			} else if ("team".equals(name) &&  GetterUtil.getBoolean(value, false)) {
    			result.addPrincipalId(ObjectKeys.TEAM_MEMBER_ID);
			} else if ("condition".equals(name)) {
		    	if (entity.getEntryDef() != null) {
		    		Element element = (Element)prop.selectSingleNode("./workflowEntryDataUserList[@definitionId='" +
		    			entity.getEntryDef().getId() + "']");
		    		if (element != null) {
		    			String userListName = element.attributeValue("elementName"); //custom attribute name
		   				CustomAttribute attr = entity.getCustomAttribute(userListName); 
		   				if (attr != null) {
		   					//comma separated value
		   					result.addPrincipalIds(LongIdUtil.getIdsAsLongSet(attr.getValue().toString(), ","));
		   				}
		    		}
		    	}
    		} else if ("entryCreator".equals(name) && GetterUtil.getBoolean(value, false)) {
    			//	add special owner to allow list
    			result.addPrincipalId(ObjectKeys.OWNER_USER_ID);
    		}
		}
		//see if nothing was actually set
		if (result.getPrincipalIds().isEmpty() && !result.isUseDefault()) return null;
    	return result;
    }
 
 
}
