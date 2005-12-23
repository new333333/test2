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

/**
 * @author hurley
 *
 */
/**
 * @author hurley
 *
 */
public class WorkflowUtils {
	
    // Defines variable names
    public final static String ENTRY_TYPE = "__entryType";
    public final static String ENTRY_ID = "__entryId";
    
    public static Map getManualTransitions(Definition wfDef, String stateName) {
		Map transitionData = new LinkedHashMap();
		Document wfDoc = wfDef.getDefinition();
		Element wfRoot = wfDoc.getRootElement();
		//Find the current state in the definition
		Element statePropertyEle = (Element) wfRoot.selectSingleNode(
				"//item[@name='workflowProcess']/item[@name='state']"+
				"/properties/property[@name='name' and @value='"+stateName+"']");
		if (statePropertyEle != null) {
			Element stateEle = statePropertyEle.getParent().getParent();
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
		Element statePropertyEle = (Element) wfRoot.selectSingleNode(
				"//item[@name='workflowProcess']/item[@name='state']"+
				"/properties/property[@name='name' and @value='"+stateName+"']");
		if (statePropertyEle != null) {
			Element stateEle = statePropertyEle.getParent().getParent();
			//Build a list of all parallel executions for this state
			List startParallelExecutions = stateEle.selectNodes(
					"./item[@name='startParallelThread']");
			if (startParallelExecutions != null) {
				for (int j = 0; j < startParallelExecutions.size(); j++) {
					//Get the "startState" property
					Element parallelThreadEle = (Element) ((Element) 
							startParallelExecutions.get(j)).selectSingleNode(
							"./properties/property[@name='name']");
					Element startStateEle = (Element) ((Element) 
							startParallelExecutions.get(j)).selectSingleNode(
							"./properties/property[@name='startState']");
					if (startStateEle != null && parallelThreadEle != null) {
						String parallelThreadName = parallelThreadEle.attributeValue("value", "");
						String startStateValue = startStateEle.attributeValue("value", "");
						if (!startStateValue.equals("") && !parallelThreadName.equals("")) {
							//We have a start state. 
							//TODO Check that the user has the right to execute this transition
							
							//Ok, add this transition to the map
							Map parallelThread = new HashMap();
							parallelThread.put(ObjectKeys.WORKFLOW_PARALLEL_THREAD_NAME, parallelThreadName);
							parallelThread.put(ObjectKeys.WORKFLOW_PARALLEL_THREAD_START_STATE, startStateValue);
						}
					}
				}
			}
		}
		return parallelExecutions;
    }
    
}
