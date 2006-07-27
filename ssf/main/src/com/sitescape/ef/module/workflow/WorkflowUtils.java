package com.sitescape.ef.module.workflow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.WfAcl;
import com.sitescape.ef.domain.WfNotify;
import com.sitescape.ef.module.definition.DefinitionUtils;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;


/**
 * @author hurley
 *
 */
public class WorkflowUtils {
    // Defines variable names
    public final static String ENTRY_TYPE = "__entryType";
    public final static String ENTRY_ID = "__entryId";
    public final static String BINDER_ID = "__binderId";
       
    public static Map getManualTransitions(Definition wfDef, String stateName) {
		Map transitionData = new LinkedHashMap();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all manual transitions for this state
			List transitions = stateEle.selectNodes("./item[@name='transitions']/item[@name='transitionManual']");
			if (transitions != null) {
				for (int j = 0; j < transitions.size(); j++) {
					String toStateValue = DefinitionUtils.getPropertyValue((Element)transitions.get(j), "toState");
					String toStateCaption = "";
					if (!Validator.isNull(toStateValue)) {
						//We have a transition. get the caption;
						Element toStateEle = (Element) wfRoot.selectSingleNode(
									"//item[@name='workflowProcess']/item[@name='state']"+
									"/properties/property[@name='name' and @value='"+toStateValue+"']");
						if (toStateEle != null) {
							Element toStateCaptionEle = 
								(Element) toStateEle.selectSingleNode("../property[@name='caption']");
							if (toStateCaptionEle != null) {
								toStateCaption = toStateCaptionEle.attributeValue("value", "");
							}
						}
						if (toStateCaption.equals("")) toStateCaption = toStateValue;
						//TODO Check that the user has the right to execute this transition
							
						//Ok, add this transition to the map
						transitionData.put(toStateValue, toStateCaption);
					}
				}
			}
		}
		return transitionData;
    }
    public static Map getQuestions(Definition wfDef, String stateName) {
		Map questionsData = new LinkedHashMap();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all questions for this state
			List questions = stateEle.selectNodes("./item[@name='workflowQuestion']");
			if (questions != null) {
				for (int j = 0; j < questions.size(); j++) {
					Map questionData = new LinkedHashMap();
					String questionName = DefinitionUtils.getPropertyValue((Element)questions.get(j), "name");
					String questionText = DefinitionUtils.getPropertyValue((Element)questions.get(j), "question");
					Map responseData = new LinkedHashMap();
					questionData.put(WebKeys.WORKFLOW_QUESTION_TEXT, questionText);
					questionData.put(WebKeys.WORKFLOW_QUESTION_RESPONSES, responseData);
					List responses = ((Element)questions.get(j)).selectNodes("./item[@name='workflowResponse']");
					if (responses != null) {
						for (int k = 0; k < responses.size(); k++) {
							String responseName = DefinitionUtils.getPropertyValue((Element)responses.get(j), "name");
							String responseText = DefinitionUtils.getPropertyValue((Element)responses.get(j), "response");
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
    /**
     * Return the set of states that this state can transition to
     * @param wfDef
     * @param stateName
     * @return transition to states
     */
    public static Set getAllTransitions(Definition wfDef, String stateName) {
		Set transitionData = new HashSet();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all manual transitions for this state
			List transitions = stateEle.selectNodes("./item[@name='transitions']/item[@type='transition']");
			if (transitions != null) {
				for (int j = 0; j < transitions.size(); j++) {
					Element toStateEle = (Element) ((Element) 
							transitions.get(j)).selectSingleNode("./properties/property[@name='toState']");
					if (toStateEle != null) {
						String toStateValue = toStateEle.attributeValue("value", "");
						if (!toStateValue.equals("")) {
							transitionData.add(toStateValue);
						}
					}
				}
			}
		}
		return transitionData;
    }    
    /**
     * Get transitions triggered by a condition, ie) not manual
     * 
     * @param wfDef
     * @param stateName
     * @return Return the Dom elements
     */
    public static List getConditionElements(Definition wfDef, String stateName) {
    	List conditions=null;
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all conditional transitions for this state
			conditions = stateEle.selectNodes("./item[@name='transitions']/item[@name!='transitionManual']");
		}
		if (conditions == null) conditions = new ArrayList();
		return conditions;
    }
    /**
     * Get transitions triggered by a manual request
     * 
     * @param wfDef
     * @param stateName
     * @return Return the Dom elements
     */
    public static List getManualElements(Definition wfDef, String stateName) {
    	List conditions=null;
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all conditional transitions for this state
			conditions = stateEle.selectNodes("./item[@name='transitions']/item[@name='transitionManual']");
		}
		if (conditions == null) conditions = new ArrayList();
		return conditions;
    }
   

    public static String getInitialState(Definition wfDef) {
		Document workflowDoc = wfDef.getDefinition();
		String initialState="";
		if (workflowDoc != null) {
			Element workflowRoot = workflowDoc.getRootElement();
			Element initialStateProperty = (Element) workflowRoot.selectSingleNode("./item[@name='workflowProcess']/properties/property[@name='initialState']");
			if (initialStateProperty != null) {
				initialState = initialStateProperty.attributeValue("value", "");
				//Validate that this is an existing state
				if (!Validator.isNull(initialState)) {
					Element state = (Element)getState(workflowRoot, initialState);
					if (state == null) initialState = "";
				}
			}
			//See if the workflow definition actually defined an initial state
			if (Validator.isNull(initialState)) {
				//There is no defined initial state, so use the first state in the list
				initialStateProperty = (Element) workflowRoot.selectSingleNode("./item[@name='workflowProcess']/item[@name='state']/properties/property[@name='name']");
				initialState = initialStateProperty.attributeValue("value", "");
			}
		}
		return initialState;
    }
    public static boolean isThreadEndState(Definition wfDef, String stateName, String threadName) {
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		if (Validator.isNull(threadName)) {
			List ends = (List)wfRoot.selectNodes("./item[@name='workflowProcess']/properties/property[@name='endState' and @value='"+stateName+"']");
    		if ((ends == null) || ends.isEmpty()) return false;
    		return true;
    	} else {
    		Element threadEle = (Element) wfRoot.selectSingleNode("//item[@name='parallelThread']/properties/property[@name='name' and @value='"+threadName+"']");
    		if (threadEle != null) {
    			Element properties = threadEle.getParent();
    			List ends = properties.selectNodes("./property[@name='endState' and @value='"+stateName+"']");
        		if ((ends == null) || ends.isEmpty()) return false;
        		return true;
			}
		}
		return false;
    } 
    public static WfAcl getStateAcl(Definition wfDef, String stateName, AccessType type) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {
			Element accessControls = (Element)stateEle.selectSingleNode("./item[@name='accessControls']");
			if (accessControls != null) {
				if (AccessType.READ.equals(type))
						return getAcl((Element)accessControls.selectSingleNode("./item[@name='readAccess']"));
				else if (AccessType.WRITE.equals(type))  
						return getAcl((Element)accessControls.selectSingleNode("./item[@name='modifyAccess']"));
				else if (AccessType.DELETE.equals(type)) 
						return getAcl((Element)accessControls.selectSingleNode("./item[@name='deleteAccess']"));
			}
			
		}
		return getAcl(null);
    }
    public static WfAcl getStateTransitionOutAcl(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {
			Element accessControls = (Element)stateEle.selectSingleNode("./item[@name='accessControls']");
			if (accessControls != null) {
				return getAcl((Element)accessControls.selectSingleNode("./item[@name='transitionOutAccess']"));
			}			
		}
		return getAcl(null);
    }
    public static WfAcl getStateTransitionInAcl(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {
			Element accessControls = (Element)stateEle.selectSingleNode("./item[@name='accessControls']");
			if (accessControls != null) {
				return getAcl((Element)accessControls.selectSingleNode("./item[@name='transitionInAccess']"));
			}			
		}
		return getAcl(null);
    }    
    public static Map getAcls(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		Map results = new HashMap();
		if (stateEle != null) {
			Element accessControls = (Element)stateEle.selectSingleNode("./item[@name='accessControls']");
			if (accessControls != null) {
				results.put(AccessType.READ, 
						getAcl((Element)accessControls.selectSingleNode("./item[@name='readAccess']")));
				results.put(AccessType.WRITE, 
						getAcl((Element)accessControls.selectSingleNode("./item[@name='modifyAccess']")));
				results.put(AccessType.DELETE, 
						getAcl((Element)accessControls.selectSingleNode("./item[@name='deleteAccess']")));
			}
			
		}
		return results;
    }
    private static WfAcl getAcl(Element aclElement) {
    	WfAcl result = new WfAcl();
    	if (aclElement == null) return result;
    	Element props = (Element)aclElement.selectSingleNode("./properties/property[@name='folderDefault']");
    	if (props != null)
    		result.setUseDefault(GetterUtil.getBoolean(props.attributeValue("value"), true));
    	props = (Element)aclElement.selectSingleNode("./properties/property[@name='entryCreator']");
    	if (props != null)
    		result.setCreator(GetterUtil.getBoolean(props.attributeValue("value"), false));
    	props = (Element)aclElement.selectSingleNode("./properties/property[@name='userGroupAccess']");
    	if (props != null)
    		result.setPrincipals(props.attributeValue("value"));
    	return result;
    }
    public static List getEnterNotifications(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {  	
			List notifications = (List)stateEle.selectNodes("./item[@name='onEntry']/item[@name='notifications']");
			return getNotifications(notifications);
		}
		return new ArrayList();

    }
    public static List getExitNotifications(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {  	
			List notifications = (List)stateEle.selectNodes("./item[@name='onExit']/item[@name='notifications']");
			return getNotifications(notifications);
		}
		return new ArrayList();

    }
    private static List getNotifications(List notifications) {
    	List result = new ArrayList();
    	if ((notifications == null) || notifications.isEmpty()) return result;
    	Element prop, notify;
    	List props;
    	String name, value;
    	for (int i=0; i<notifications.size(); ++i) {
    		WfNotify n = new WfNotify();
    		notify = (Element)notifications.get(i);
    		props = notify.selectNodes("./properties/property");
    		if ((props == null) || props.isEmpty()) continue;
    		for (int j=0; j<props.size(); ++j) {
    			prop = (Element)props.get(j);
    			name = prop.attributeValue("name","");
    			value = prop.attributeValue("value","");
    			if ("entryCreator".equals(name)) {
    				n.setCreatorEnabled(GetterUtil.getBoolean(value, false));
    			} else if ("subjText".equals(name)) {
    				n.setSubject(value);
    			} else if ("appendTitle".equals(name)) {
    				n.setAppendTitle(GetterUtil.getBoolean(value, false));
    			} else if ("bodyText".equals(name)) {
    				n.setBody(value);
    			} else if ("appendBody".equals(name)) {
    				n.setAppendBody(GetterUtil.getBoolean(value, false));
    			}
    		}
    		result.add(n);
    	}
    	return result;
    	
    }
    public static List getOnEntry(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {  	
			List items = (List)stateEle.selectNodes("./item[@name='onEntry']/item");
			return items;
		}
		return new ArrayList();

    }
    public static List getOnExit(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {  	
			List items = (List)stateEle.selectNodes("./item[@name='onExit']/item");
			return items;
		}
		return new ArrayList();

    } 
    private static Element getState(Element wfRoot, String stateName) {
		//Find the current state in the definition
		Element statePropertyEle = (Element) wfRoot.selectSingleNode(
				"//item[@name='workflowProcess']/item[@name='state']"+
				"/properties/property[@name='name' and @value='"+stateName+"']");
		if (statePropertyEle != null) {
			return statePropertyEle.getParent().getParent();
		}
		return null;

    }
    public static List getItems(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
    	Element stateEle = getState(wfDoc.getRootElement(), stateName);
		List items = stateEle.selectNodes("./item");
		if (items == null) return new ArrayList();
		return items;
    }
 
}
