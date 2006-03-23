package com.sitescape.ef.module.shared;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Definition;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.WfWaits;
import com.sitescape.ef.domain.WfNotify;
import com.sitescape.ef.domain.WfAcl;
import com.sitescape.ef.domain.Entry;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.security.acl.AccessType;


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
					Element toStateEle = (Element) ((Element) 
							transitions.get(j)).selectSingleNode("./properties/property[@name='toState']");
					if (toStateEle != null) {
						String toStateValue = toStateEle.attributeValue("value", "");
						String toStateCaption = "";
						if (!toStateValue.equals("")) {
							//We have a transition. get the caption;
							Element toStateEle2 = (Element) wfRoot.selectSingleNode(
									"//item[@name='workflowProcess']/item[@name='state']"+
									"/properties/property[@name='name' and @value='"+toStateValue+"']");
							if (toStateEle2 != null) {
								Element toStateCaptionEle = 
									(Element) toStateEle2.selectSingleNode("../property[@name='caption']");
								if (toStateCaptionEle != null) {
									toStateCaption = toStateCaptionEle.attributeValue("value", "");
								}
							}
						}
						if (toStateCaption.equals("")) toStateCaption = toStateValue;
						if (!toStateValue.equals("") && !toStateCaption.equals("")) {
							//TODO Check that the user has the right to execute this transition
							
							//Ok, add this transition to the map
							transitionData.put(toStateValue, toStateCaption);
						}
					}
				}
			}
		}
		return transitionData;
    }
    
    public static List getParallelThreadStarts(Definition wfDef, String stateName) {
		List parallelExecutions = new ArrayList();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all parallel executions for this state
			List startParallelExecutions = stateEle.selectNodes(
					"./item[@name='startParallelThread']");
			if (startParallelExecutions != null) {
				for (int j = 0; j < startParallelExecutions.size(); j++) {
					//Get the "startState" property
					Element startParallelThreadEle = (Element) ((Element) 
							startParallelExecutions.get(j)).selectSingleNode(
							"./properties/property[@name='name']");
					String parallelThreadName = startParallelThreadEle.attributeValue("value", "");
            		if (!parallelThreadName.equals("")) {
            			Element parallelThreadEle = (Element) wfRoot.selectSingleNode("//item[@name='parallelThread']/properties/property[@name='name' and @value='"+parallelThreadName+"']");
            			if (parallelThreadEle != null) {
            				parallelThreadEle = parallelThreadEle.getParent().getParent();
            				Element startStateEle = (Element) parallelThreadEle.selectSingleNode("./properties/property[@name='startState']");
        					if (startStateEle != null && parallelThreadEle != null) {
        						String startStateValue = startStateEle.attributeValue("value", "");
        						if (!startStateValue.equals("") && !parallelThreadName.equals("")) {
        							//We have a start state. 
        							//TODO Check that the user has the right to execute this transition
        							
        							//Ok, add this transition to the map
        							Map parallelThread = new HashMap();
        							parallelThread.put(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME, parallelThreadName);
        							parallelThread.put(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE, startStateValue);
        							parallelExecutions.add(parallelThread);
        						}
            				}
            			}
            		}
				}
			}
		}
		return parallelExecutions;
    }
    public static List getParallelThreadStops(Definition wfDef, String stateName) {
		List parallelExecutions = new ArrayList();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all parallel executions for this state
			List stopParallelExecutions = stateEle.selectNodes(
					"./item[@name='stopParallelThread']");
			if (stopParallelExecutions != null) {
				for (int j = 0; j < stopParallelExecutions.size(); j++) {
					Element startParallelThreadEle = (Element) ((Element) 
							stopParallelExecutions.get(j)).selectSingleNode(
							"./properties/property[@name='name']");
					String parallelThreadName = startParallelThreadEle.attributeValue("value", "");
            		if (!parallelThreadName.equals("")) {
            			parallelExecutions.add(parallelThreadName);
            		}
				}
			}
		}
		return parallelExecutions;
    }    
    /**
     * Return a lists of WfWaits objects. Represents all of the thread waits
     * for the current state
     * @param wfDef
     * @param stateName
     * @return
     */
    public static List getParallelThreadWaits(Definition wfDef, String stateName) {
		List parallelExecutions = new ArrayList();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element stateEle = getState(wfRoot, stateName);
		if (stateEle != null) {
			//Build a list of all parallel executions for this state
			List waitParallelExecutions = stateEle.selectNodes(
					"./item[@name='transitions']/item[@name='waitForParallelThread']");
			if (waitParallelExecutions != null) {
				for (int j = 0; j < waitParallelExecutions.size(); j++) {
					//Get the "startState" property
					List parallelThreads = (List) ((Element) 
							waitParallelExecutions.get(j)).selectNodes(
							"./properties/property[@name='name']");
					Element transitionEle = (Element) ((Element) 
							waitParallelExecutions.get(j)).selectSingleNode(
							"./properties/property[@name='toState']");
					List result = new ArrayList();
					for (int k=0; k<parallelThreads.size(); ++k) {
						Element thread = (Element)parallelThreads.get(k);
						result.add(thread.attributeValue("value",""));
					}
					parallelExecutions.add(new WfWaits((String)transitionEle.attributeValue("value", ""),result));
					
				}
			}
		}
		return parallelExecutions;
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
    public static List getEndState(Definition wfDef) {
		Document workflowDoc = wfDef.getDefinition();
		List endStates= new ArrayList();
		if (workflowDoc != null) {
			Element workflowRoot = workflowDoc.getRootElement();
			List ends = (List)workflowRoot.selectNodes("./item[@name='workflowProcess']/properties/property[@name='endState']");
			if (ends != null) {
				for (int i=0; i<ends.size(); ++i) {
					endStates.add(((Element)ends.get(i)).attributeValue("value", ""));
				}
			}
		}
		return endStates;
    }
    public static List getThreadEndState(Definition wfDef, String threadName) {
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the thread definition
		List endStates= new ArrayList();
		Element threadEle = (Element) wfRoot.selectSingleNode("//item[@name='parallelThread']/properties/property[@name='name' and @value='"+threadName+"']");
		if (threadEle != null) {
			Element properties = threadEle.getParent();
			List ends = properties.selectNodes("./property[@name='endState']");
			if (ends != null) {
				for (int i=0; i<ends.size(); ++i) {
					endStates.add(((Element)ends.get(i)).attributeValue("value", ""));
				}
			}
		}
		return endStates;
    } 
    public static WfAcl getStateAcl(Definition wfDef, Entry entry, String stateName, AccessType type) {
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
    public static Map getAcls(Definition wfDef, Entry entry, String stateName) {
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
			List notifications = (List)stateEle.selectNodes("./item[@name='notifications']/item[@name='entryNotification']");
			return getNotifications(notifications);
		}
		return new ArrayList();

    }
    public static List getExitNotifications(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = getState(wfDoc.getRootElement(), stateName);
		if (stateEle != null) {  	
			List notifications = (List)stateEle.selectNodes("./item[@name='notifications']/item[@name='exitNotification']");
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
}
