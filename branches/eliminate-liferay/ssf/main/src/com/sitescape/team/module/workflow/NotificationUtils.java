package com.sitescape.team.module.workflow;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.WfNotify;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.util.GetterUtil;

public class NotificationUtils {
	   public static boolean hasEnterNotifications(Definition wfDef, Element stateEle ) {
			if (stateEle != null) {  	
				List notifications = (List)stateEle.selectNodes("./item[@name='onEntry']/item[@name='notifications']");
				if (notifications == null || notifications.isEmpty()) return false;
				return true;
			}
			return false;

	    }
	    public static boolean hasExitNotifications(Definition wfDef, Element stateEle ) {
			if (stateEle != null) {  	
				List notifications = (List)stateEle.selectNodes("./item[@name='onExit']/item[@name='notifications']");
				if (notifications == null || notifications.isEmpty()) return false;
				return true;
			}
			return false;

	    }
	   public static List getEnterNotifications(Definition wfDef, DefinableEntity entity, String stateName) {
	    	Document wfDoc = wfDef.getDefinition();
			//Find the current state in the definition
			Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
			if (stateEle != null) {  	
				List notifications = (List)stateEle.selectNodes("./item[@name='onEntry']/item[@name='notifications']");
				return getNotifications(entity, notifications);
			}
			return new ArrayList();

	    }
	    public static List getExitNotifications(Definition wfDef, DefinableEntity entity, String stateName) {
	    	Document wfDoc = wfDef.getDefinition();
			//Find the current state in the definition
			Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
			if (stateEle != null) {  	
				List notifications = (List)stateEle.selectNodes("./item[@name='onExit']/item[@name='notifications']");
				return getNotifications(entity, notifications);
			}
			return new ArrayList();

	    }
	    private static List getNotifications(DefinableEntity entity, List<Element> notifications) {
	    	List result = new ArrayList();
	    	if ((notifications == null) || notifications.isEmpty()) return result;
	    	List<Element> props;
	    	String name, value;
	    	for (Element notify:notifications) {
	    		WfNotify n = new WfNotify();
	    		props = notify.selectNodes("./properties/property");
	    		if ((props == null) || props.isEmpty()) continue;
	    		for (Element prop:props) {
	    			name = prop.attributeValue("name","");
	    			value = prop.attributeValue("value","");
	    			if ("entryCreator".equals(name) &&  GetterUtil.getBoolean(value, false)) {
	    				n.addPrincipalId(ObjectKeys.OWNER_USER_ID);
	    			} else if ("team".equals(name) &&  GetterUtil.getBoolean(value, false)) {
	        			n.addPrincipalId(ObjectKeys.TEAM_MEMBER_ID);
	    			} else if ("subjText".equals(name)) {
	    				n.setSubject(value);
	    			} else if ("appendTitle".equals(name)) {
	    				n.setAppendTitle(GetterUtil.getBoolean(value, false));
	    			} else if ("bodyText".equals(name)) {
	    				n.setBody(value);
	    			} else if ("appendBody".equals(name)) {
	    				n.setAppendBody(GetterUtil.getBoolean(value, false));
	    			} else if ("userGroupNotification".equals(name)) {
	    				n.addPrincipalIds(LongIdUtil.getIdsAsLongSet(value));
	    			} else if ("condition".equals(name)) {
	    		    	if (entity.getEntryDef() != null) {
	    		    		Element element = (Element)prop.selectSingleNode("./workflowEntryDataUserList[@definitionId='" +
	    		    			entity.getEntryDef().getId() + "']");
	    		    		if (element != null) {
	    		    			String userListName = element.attributeValue("elementName"); //custom attribute name
	    		   				CustomAttribute attr = entity.getCustomAttribute(userListName); 
	    		   				if (attr != null) {
	    		   					//comma separated
	    		   					n.addPrincipalIds(LongIdUtil.getIdsAsLongSet(attr.getValue().toString(), ","));
	    		   				}
	    		    		}
	    		    	}
	    				
	    			}
	    		}
	    		result.add(n);
	    	}
	    	return result;
	    	
	    }

}
