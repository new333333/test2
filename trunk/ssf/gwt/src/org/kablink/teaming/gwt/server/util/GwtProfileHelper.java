package org.kablink.teaming.gwt.server.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CommaSeparatedValue;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.PackedValue;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
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
				
				Element item = (Element)configElement.selectSingleNode("//definition/item[@name='profileEntryBusinessCard']");
				if(item != null) {
					List<Element> itemList = item.selectNodes("//item[@category='userViewLayout']");
					
					//for each section header create a profile Info object to hold the information 
					for(Element catItem: itemList){
							ProfileCategory cat = new ProfileCategory();
							String caption = catItem.attributeValue("caption", "");
							String title = NLT.get(caption);
							
							cat.setTitle(title);
							profile.add(cat);
							
							List<Element> attrElements = catItem.selectNodes("properties/property[@name='_elements']");
							for(Element attrElement: attrElements) {
								ProfileAttribute attr = new ProfileAttribute();
								
								//Get the Elements name - which is the attribute name
								String name = attrElement.attributeValue("value");
								attr.setName(name);
								
								//Now get the title for this attribute
								attr.setTitle(NLT.get("profile.element."+name, name));
								cat.add(attr);
							}
							
							List<Element> customElements = catItem.selectNodes("item/properties/property[@name='name']");
							for(Element attrElement: customElements) {
								ProfileAttribute attr = new ProfileAttribute();
								
								//Get the Elements name - which is the attribute name
								String name = attrElement.attributeValue("value");
								attr.setName(name);
								
								//Now get the title for this attribute
								attr.setTitle(NLT.get("profile.element."+name, name));
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
			
			String name = pAttr.getName();
			if(Validator.isNull(name)){
				continue;
			}

			String value = null;
			if(name.equals("name")) {
				value = u.getName();
			} else if(name.equals("title")) {
					value = u.getTitle();
			} else if(name.equals("phone")) {
				value = u.getPhone();
			} else if(name.equals("emailAddress")){
			    value = u.getEmailAddress();
			} else if(name.equals("mobileAddress")){
			    value = u.getMobileEmailAddress();
			} else if(name.equals("txtEmailAddress")){
				    value = u.getTxtEmailAddress();
			} else if(name.equals("twitterId")){
				value = u.getSkypeId();
			} else if(name.equals("skypeId")){
				value = u.getSkypeId();
			} else if(name.equals("organization")){
				value = u.getOrganization();
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
		 	    }
			}

			if(Validator.isNull(value)){
				value = "";
			}
				
			pAttr.setValue(value, true);

		}
	}
}
