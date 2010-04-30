package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;

public class GwtProfileHelper {
	
	public static ProfileInfo buildProfileInfo(AllModulesInjected bs, Long binderId) {

		ProfileInfo profile = new ProfileInfo();

		//get the binder
		Binder binder = bs.getBinderModule().getBinder(Long.valueOf(binderId));
		Principal owner = binder.getCreation().getPrincipal(); //creator is user
		
		if (owner != null) {
			//User u = user;
			User u;
			//if (!user.getId().equals(owner.getId())) {
				u = (User) bs.getProfileModule().getEntry(owner.getId());
				Document doc = u.getEntryDef().getDefinition();
				Element configElement = doc.getRootElement();
				
				Element item = (Element)configElement.selectSingleNode("//definition/item[@name='profileEntryStandardView']");
				if(item != null) {
					List<Element> itemList = item.selectNodes("item[@name]");
					
					//for each section header create a profile Info object to hold the information 
					for(Element catItem: itemList){
							ProfileCategory cat = new ProfileCategory();
							String caption = catItem.attributeValue("caption", "");
							String name = catItem.attributeValue("name", "");
							String title = NLT.get(caption);
							
							cat.setTitle(title);
							cat.setName(name);
							profile.add(cat);
							
							List<Element> attrElements = catItem.selectNodes("item[@name]");
							for(Element attrElement: attrElements) {
								ProfileAttribute attr = new ProfileAttribute();
								
								//Get the Elements name - which is the attribute name
								String attrName = attrElement.attributeValue("name");
								attr.setName(attrName);
								
								Element captionElement = (Element) attrElement.selectSingleNode("properties/property[@name='caption']");
								if(captionElement != null) {

									String attrTitle = captionElement.attributeValue("value");

									//Now get the title for this attribute
									attr.setTitle(NLT.get(attrTitle));
								}

								Element nameElement = (Element) attrElement.selectSingleNode("properties/property[@name='name']");
								if(nameElement != null) {
									String dataName = nameElement.attributeValue("value");
									attr.setDataName(dataName);
									
									if(attr.getTitle() == null){
										attr.setTitle(NLT.get("profile.element."+dataName, dataName));
									}
								}
								
								cat.add(attr);
							}
							
							//Get the value for this attribute
							buildAttributeInfo(u, cat);
					}
				}
		}
		
		return profile;
	}
	
	private static void buildAttributeInfo(User u, ProfileCategory cat) {
		
		List<ProfileAttribute> attrs = cat.getAttributes();
		
		for(ProfileAttribute pAttr: attrs) {
			
			String value = null;
			String type = "";
			String name = pAttr.getDataName();
		
			if(Validator.isNull(name)){
				continue;
			}

			if(name.equals("name")) {
				value = u.getName();
			} else if(name.equals("title")) {
				value = u.getTitle();
			} else if(name.equals("phone")) {
				value = u.getPhone();
			} else if(name.equals("emailAddress")){
			    value = u.getEmailAddress();
			    type = "email";
			} else if(name.equals("mobileEmailAddress")){
			    value = u.getMobileEmailAddress();
			    type = "email";
			} else if(name.equals("txtEmailAddress")){
				value = u.getTxtEmailAddress();
				type = "email";
			} else if(name.equals("twitterId")){
				value = u.getSkypeId();
			} else if(name.equals("skypeId")){
				value = u.getSkypeId();
			} else if(name.equals("organization")){
				value = u.getOrganization();
			} else if(name.equals("zonName")){
				value = u.getZonName();
			} else {
				CustomAttribute cAttr = u.getCustomAttribute(name);
				if(cAttr != null) {
				    switch(cAttr.getValueType()) {
		    			case CustomAttribute.STRING:
			    		case CustomAttribute.BOOLEAN:
			    		case CustomAttribute.LONG:
			    		case CustomAttribute.DATE:
			    			pAttr.setValue(cAttr.getValue(), true);
			    			break;
			    		default:
			    			pAttr.setValue(cAttr.getValue(), true);
			    			break;
				    }
				    
				    pAttr.setTitle(NLT.get("profile.element."+name, name));
				    
				    //continue to the next value
				    continue;
		 	    }
			}

			if(Validator.isNull(value)){
				value = "";
			}
				
			pAttr.setValue(value, true);
			pAttr.setDisplayType(type);

		}
	}
	
	public static Principal getPrincipalByBinderId(AllModulesInjected bs, String sbinderId){
		//Convert binderID to Long
		Long binderId = Long.valueOf(sbinderId);
		Binder binder = bs.getBinderModule().getBinder(binderId);

		//Get the Owner of the binder
		Principal p = binder.getOwner();
		Long workspaceId = p.getWorkspaceId();

		//We need to match the binder to the correct user, so we can read the miniblog from the correct user
		if(!binderId.equals(workspaceId)) {
			//then we need to find the correct owner by name
			String owner = binder.getName();
			if(owner!=null && !owner.equals("")){
				List<String> names = new ArrayList<String>();
				names.add(owner);

				Collection<Principal> principals;
				principals = bs.getProfileModule().getPrincipalsByName(names);
				if (!principals.isEmpty()) p = (Principal)principals.iterator().next();
			}
		} 
		
		return p;
	}
	
	public static UserStatus getUserStatus(AllModulesInjected bs, String sbinderId) {
		//This is the object that is streamed back to the client
		UserStatus userStatus = new UserStatus();
		
		Principal p = getPrincipalByBinderId(bs, sbinderId);
		
		List <Long> userIds = new ArrayList<Long>();
		userIds.add(p.getId());

		//Get the User object for this principle
		SortedSet<User> users = bs.getProfileModule().getUsers(userIds);
		User u = null;
		if (!users.isEmpty()) u = users.iterator().next();
		
		//Check this user object to see if they cleared their status, don't display a status if cleared.
		if(u != null) {
			String sStatus = u.getStatus();
			if(sStatus == null || sStatus.equals("")) {
				return userStatus;
			}
		}
		
		Long[] userIdsArray = new Long[]{p.getId()};
		
		String page = "0";
		int pageStart = Integer.valueOf(page) * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Calling into api to read the user status because it checks access controls
		List<Map<String,Object>> statuses = bs.getReportModule().getUsersStatuses(userIdsArray, null, null, 
				pageStart + Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox")));
		if (statuses != null && statuses.size() > pageStart) {
			Map<String,Object> statusMap = statuses.get(0);
			
			User statusUser = (User) statusMap.get(ReportModule.USER);
			statusUser.getStatus();
			
			String description = (String)statusMap.get(ReportModule.DESCRIPTION);
			Date modifyDate = (Date)statusMap.get(ReportModule.DATE);
			
			userStatus.setMiniBlogId(statusUser.getMiniBlogId());
			userStatus.setStatus(description);
			userStatus.setModifyDate(modifyDate);
		}
		
		return userStatus;
	}
}
