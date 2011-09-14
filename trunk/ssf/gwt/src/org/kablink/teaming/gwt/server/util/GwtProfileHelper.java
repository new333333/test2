/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.server.util;

import static org.kablink.util.search.Constants.MODIFICATION_DATE_FIELD;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CommaSeparatedValue;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.CustomAttributeListElement;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeAttachment;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Criteria;

@SuppressWarnings("unchecked")
public class GwtProfileHelper {
	
	
	protected static Log logger = LogFactory.getLog(GwtServerHelper.class);
	
	/**
	 * This helper method is for the User Profile and reads the User definition and iterates through each of its elements.
	 * Retrieves the user attributes and custom attributes that the user definition defines and then creates helper classes
	 * that can be serialized and passed to the client.
	 * 
	 * @param bs
	 * @param binderId
	 * @return ProfileInfo  The main class that contains other helper classes
	 */
	public static ProfileInfo buildProfileInfo(HttpServletRequest request, AllModulesInjected bs, Long binderId) {

		ProfileInfo profile = new ProfileInfo();

		//get the binder
		Binder binder = bs.getBinderModule().getBinder(Long.valueOf(binderId));
		Principal owner = binder.getCreation().getPrincipal(); //creator is user
		owner = Utils.fixProxy(owner);
		
		if (owner != null) {
			//User u = user;
			User u;
			//if (!user.getId().equals(owner.getId())) {
				u = (User) bs.getProfileModule().getEntry(owner.getId());
				u = (User)Utils.fixProxy(u);
				profile.setUserId(u.getId().toString());
				
				Document doc = u.getEntryDefDoc();
				Element configElement = doc.getRootElement();
				
				Element item = (Element)configElement.selectSingleNode("//definition/item[@name='profileEntryStandardView']");
				if(item != null) {
					List<Element> itemList = item.selectNodes("item");
					
					//for each section header create a profile Info object to hold the information 
					for(Element catItem: itemList){
							ProfileCategory cat = new ProfileCategory();
							String caption = catItem.attributeValue("caption", "");
							Element captionEle = (Element)catItem.selectSingleNode("properties/property[@name='caption']");
							if (captionEle != null) {
								caption = captionEle.attributeValue("value", "");
							}
							String name = catItem.attributeValue("name", "");
							String title = NLT.getDef(caption);
							
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
									attr.setTitle(NLT.getDef(attrTitle));
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
							buildAttributeInfo(request, u, cat, profile);
					}
				}
		}
		
		return profile;
	}
	
	public static ProfileAttribute getProfileAvatars(HttpServletRequest request, AllModulesInjected bs, Long binderId) {
		
		ProfileAttribute attribute = new ProfileAttribute();
		
		//get the binder
		Binder binder = bs.getBinderModule().getBinder(Long.valueOf(binderId));
		Principal owner = binder.getCreation().getPrincipal(); //creator is user
		owner = Utils.fixProxy(owner);
		
		if (owner != null) {
			//User u = user;
			User u = (User) bs.getProfileModule().getEntry(owner.getId());
			u = (User)Utils.fixProxy(u);
			
			//Get the Elements name - which is the attribute name
			String attrName = "picture";
			attribute.setName(attrName);
			
			String dataName = "picture";
			attribute.setDataName(dataName);
			
			//Read the custom attribute
			Document defDoc = ((DefinableEntity)u).getEntryDefDoc();
			CustomAttribute cAttr = u.getCustomAttribute(attribute.getDataName());
			
			if(cAttr == null) {
				return attribute;
			}
			
			//Get the actual caption
			String caption = DefinitionHelper.findCaptionForAttribute(cAttr.getName(), defDoc);
			attribute.setTitle(NLT.getDef(caption));
			
			if(attribute.getTitle() == null){
				attribute.setTitle(NLT.get("profile.element."+dataName, dataName));
			}
			
			//Convert the Custom Attribute to a Profile Attribute for serialization purposes
			convertCustomAttrToProfileAttr(request, u, cAttr, attribute, attribute.getDataName(), null);
		}
		
		return attribute;
	}
	
	/**
	 * This helper method is for the User Profile Quick View and reads the User definition and iterates through each of its elements.
	 * Retrieves the user attributes and custom attributes that the user definition defines and then creates helper classes
	 * that can be serialized and passed to the client.
	 * 
	 * @param bs
	 * @param binderId
	 * @return ProfileInfo  The main class that contains other helper classes
	 */
	public static ProfileInfo buildQuickViewProfileInfo(HttpServletRequest request, AllModulesInjected bs, Long binderId) throws Exception {
		ProfileInfo profile = new ProfileInfo();

		//get the binder
		Binder binder = bs.getBinderModule().getBinder(Long.valueOf(binderId));
		Principal owner = binder.getCreation().getPrincipal(); //creator is user
		owner = Utils.fixProxy(owner);
		
		if (owner != null) {
			//User u = user;
			User u;
			//if (!user.getId().equals(owner.getId())) {
				u = (User) bs.getProfileModule().getEntry(owner.getId());
				u = (User)Utils.fixProxy(u);

				CustomAttribute ca = GwtServerHelper.getCurrentUser().getCustomAttribute("conferencingID");
				if (ca != null && ((String)ca.getValue()).length() > 0) {
					profile.setConferencingEnabled(bs.getConferencingModule().isEnabled());
				}
				
				PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
				if (presenceService != null) {
					profile.setPresenceEnabled(presenceService.isEnabled());
				}

				Document doc = u.getEntryDefDoc();
				Element configElement = doc.getRootElement();
				
				Element item = (Element)configElement.selectSingleNode("//definition/item[@name='profileEntrySimpleView']");
				if(item != null) {
					List<Element> itemList = item.selectNodes("item[@name]");
					
					//for each section header create a profile Info object to hold the information 
					for(Element catItem: itemList){
							ProfileCategory cat = new ProfileCategory();
							String caption = catItem.attributeValue("caption", "");
							String name = catItem.attributeValue("name", "");
							String title = NLT.getDef(caption);
							
							cat.setTitle(title);
							cat.setName(name);
							profile.add(cat);
							
							List<Element> aElements = catItem.selectNodes("properties/property[@name='_elements']");
							for(Element aElement: aElements) {
								ProfileAttribute attr = new ProfileAttribute();
								
								//Get the Elements name - which is the attribute name
								String attrName = aElement.attributeValue("value");
								attr.setDataName(attrName);
								
								//Now get the title for this attribute
								attr.setTitle(NLT.get("profile.abv.element."+attrName, attrName, false));
								cat.add(attr);
							}
							
							List<Element> attrElements = catItem.selectNodes("item[@name]");
							for(Element attrElement: attrElements) {
								ProfileAttribute attr = new ProfileAttribute();
								
								//Get the Elements name - which is the attribute name
								String attrName = attrElement.attributeValue("name");
								attr.setName(attrName);
								
								Element captionElement = (Element) attrElement.selectSingleNode("properties/property[@name='caption']");
								if(captionElement != null) {
									final String withHelpMarker = "WithHelp";
									String attrTitle = captionElement.attributeValue("value");
									String attrValue = null;
									if (attrTitle.startsWith("__") && attrTitle.endsWith(withHelpMarker)) {
										attrValue = NLT.get(attrTitle.substring(0, (attrTitle.length() - withHelpMarker.length())), "", true);
										if ((null != attrValue) && (0 == attrValue.length())) {
											attrValue = null;
										}
									}
									if (null == attrValue) {
										attrValue = NLT.getDef(attrTitle);
									}

									//Now get the title for this attribute
									attr.setTitle(attrValue);
								}

								Element nameElement = (Element) attrElement.selectSingleNode("properties/property[@name='name']");
								if(nameElement != null) {
									String dataName = nameElement.attributeValue("value");
									attr.setDataName(dataName);
									
									//if the element exists then show the picture
									if(dataName != null && dataName.equals("picture")){
										profile.setPictureEnabled(true);
									}
									
									if(attr.getTitle() == null){
										attr.setTitle(NLT.get("profile.element."+dataName, dataName));
									}
								}
								
								cat.add(attr);
							}
							
							//Get the value for this attribute
							buildAttributeInfo(request, u, cat, profile);
					}
				}
		}
		
		return profile;
	}
	
	/**
	 * This is a help method to create the ProfileAttribute objects that are related to the User Attributes
	 * and CustomAttributes defined on the user object.
	 * 
	 * @param u     User 
	 * @param cat - ProfileCategory or section headings that are defined in the user definiton
	 * @param profile
	 */
	private static void buildAttributeInfo(HttpServletRequest request, User u, ProfileCategory cat, ProfileInfo profile) {
		
		List<ProfileAttribute> attrs = cat.getAttributes();
		
		for(ProfileAttribute pAttr: attrs) {
			
			String value = null;
			String type = "";
			String name = pAttr.getDataName();
		
			if(Validator.isNull(name)){
				continue;
			}

			//get attributes store with the User 
			if(name.equals("name")) {
				value = u.getName();
			} else if(name.equals("title")) {
				value = Utils.getUserTitle(u);
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
			} else if(name.equals("bccEmailAddress")){
				value = u.getBccEmailAddress();
				type = "email";
			} else if(name.equals("twitterId")){
				value = u.getSkypeId();
			} else if(name.equals("skypeId")){
				value = u.getSkypeId();
			} else if(name.equals("organization")){
				value = u.getOrganization();
			} else if(name.equals("zonName")){
				value = u.getZonName();
			} else if(name.equals("firstName")){
				value = u.getFirstName();
			} else if(name.equals("middleName")){
				value = u.getMiddleName();
			} else if(name.equals("lastName")){
				value = u.getLastName();
			} else if(name.equals("locale")){
				value = u.getLocale().getDisplayName();
			} else if(name.equals("timeZone")){
				value = TimeZoneHelper.getUserTimeZoneDisplayString(u);
			} else {

				//Read the custom attribute
				CustomAttribute cAttr = u.getCustomAttribute(name);
				//Convert the Custom Attribute to a Profile Attribute for serialization purposes
				if (cAttr != null) {
					convertCustomAttrToProfileAttr(request, u, cAttr, pAttr, name, profile);
				}
				
				//done here, continue to the next attribute
				continue;
			}

			if(Validator.isNull(value)){
				value = "";
			}
				
			pAttr.setValue(value);
			pAttr.setDisplayType(type);

		}
	}
	
	/**
	 * Convert the CustomAttribute and its domain object's to the ProfileAttribute class 
	 *  
	 * @param u
	 * @param cAttr
	 * @param pAttr
	 * @param name
	 */
	private static void convertCustomAttrToProfileAttr(HttpServletRequest request, User u, CustomAttribute cAttr, ProfileAttribute pAttr, String name, ProfileInfo profile) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Document defDoc = ((DefinableEntity)u).getEntryDefDoc();
		String attrType = DefinitionHelper.findAttributeType(name, defDoc);
		boolean processed = true;
		
		//Process the known attribute types
		if ("description".equals(attrType) || "htmlEditorTextarea".equals(attrType)) {
			Description desc = null;
			Object value = cAttr.getValue();
			if (value instanceof String) {
				desc = new Description((String)value);
			} else if (value instanceof Description) {
				desc = (Description) cAttr.getValue();
			}
			if (desc != null) {
				String text = MarkupUtil.markupStringReplacement(null, null, request, null, u, desc.getText(), WebKeys.MARKUP_VIEW);
				if(text != null){
					//added a length of one to skip over a return characters that are in somehow in the value of the attribute
					if(text.length() > 1){
						pAttr.setValue(text);
					}
				}
			}
		} else if ("selectbox".equals(attrType)) {
			Map selectionMap = DefinitionHelper.findSelectboxSelectionsAsMap(name, defDoc);
			if (cAttr.getValueType() == CustomAttribute.SET) {
				Set<String> v = (Set) cAttr.getValue();
				String text = "";
				for (String value : v) {
				    String caption = "";
				    if (selectionMap.containsKey(value)) {
				    	caption = (String)selectionMap.get(value);
				    }
				    if (caption == null || caption.equals("")) caption = value;
				    if (!text.equals("") && !value.equals("")) {
				    	text = text + ", ";
				    }
					text = text + NLT.getDef(caption);
				}
				pAttr.setValue(text);
			} else {
				String value = (String)cAttr.getValue();
			    String caption = "";
			    if (selectionMap.containsKey(value)) {
			    	caption = (String)selectionMap.get(value);
			    }
			    if (caption == null || caption.equals("")) caption = value;
				pAttr.setValue(NLT.getDef(caption));
			}
		} else if ("radio".equals(attrType)) {
			String value = (String)cAttr.getValue();
			Map radioMap = DefinitionHelper.findRadioSelectionsAsMap(name, defDoc);
			String caption = "";
			if (radioMap.containsKey(value)) {
				caption = (String)radioMap.get(value);
			}
			if (caption == null || caption.equals("")) caption = value;
			pAttr.setValue(NLT.getDef(caption));
		} else if ("url".equals(attrType)) {
			Element attrEle = DefinitionHelper.findAttribute(name, defDoc);
			if (attrEle != null) {
				String value = (String) cAttr.getValue();
				String linkText = DefinitionHelper.getItemProperty(attrEle, "linkText");
				if ("".equals(linkText)) linkText = value;
				String target = DefinitionHelper.getItemProperty(attrEle, "target");
				String link = "<a href=\""+value+"\" ";
				if (!target.equals(target)) link = link + "target=\""+target+"\"";
				link = link + ">" + linkText + "</a>";
				pAttr.setValue(link);
			}
		} else if ("checkbox".equals(attrType)) {
			if (cAttr == null) {
				pAttr.setValue(NLT.get("general.No"));
			} else {
				pAttr.setValue(NLT.get("general.Yes"));
			}
		} else if ("user_list".equals(attrType) || "group_list".equals(attrType) ||
				"userListSelectbox".equals(attrType)) {
			if (cAttr.getValueType() == CustomAttribute.COMMASEPARATEDSTRING) {
				CommaSeparatedValue ids = (CommaSeparatedValue)cAttr.getValue();
				List<Principal> principals = ResolveIds.getPrincipals(ids.getValueSet(), false);
				String text = "";
				for (Principal p : principals) {
				    String userTitle = Utils.getUserTitle(p);
				    if (!text.equals("") && !userTitle.equals("")) {
				    	text = text + ", ";
				    }
					text = text + userTitle;
				}
				pAttr.setValue(text);
			}
		} else if ("team_list".equals(attrType)) {
			if (cAttr.getValueType() == CustomAttribute.COMMASEPARATEDSTRING) {
				CommaSeparatedValue ids = (CommaSeparatedValue)cAttr.getValue();
				Set<Binder> binders = ResolveIds.getBinders(ids.getValueSet());
				String text = "";
				for (Binder binder : binders) {
				    if (!text.equals("")) {
				    	text = text + ", ";
				    }
					text = text + binder.getTitle();
				}
				pAttr.setValue(text);
			}
		} else if ("date".equals(attrType)) {
			Date date = (Date)cAttr.getValue();
			if (date != null) {
				DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, user.getLocale());
				pAttr.setValue(df.format(date));
			}
		} else if ("date_time".equals(attrType)) {
			Date date = (Date)cAttr.getValue();
			if (date != null) {
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, user.getLocale());
				pAttr.setValue(df.format(date));
			}
		} else if ("file".equals(attrType)) {
			if (cAttr.getValueType() == CustomAttribute.SET || cAttr.getValueType() == CustomAttribute.ORDEREDSET) {
				Set v = (Set) cAttr.getValue();
				String html = "";
				if (v != null && !v.isEmpty()) {
		    		for (Iterator iter=v.iterator(); iter.hasNext();) {
	    				Attachment attach = (Attachment) iter.next();
		    			if (attach !=  null) {
							String webPath;
							String path;
							String fileName;
							
							webPath = WebUrlUtil.getServletRootURL(request);
							fileName = attach.toString();
							path = WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, attach.getOwner().getEntity(), fileName);
							if (SsfsUtil.supportsViewAsHtml(fileName)) {
								path = WebUrlUtil.getFileHtmlUrl(request, WebKeys.ACTION_VIEW_FILE, attach.getOwner().getEntity(), fileName);
							}
							if (!html.equals("")) html = html + "<br/>";
							html = html + "<a target=\"_blank\" href=\"" + path + "\">" + fileName + "</a>";
		    			}
	    			}
	    		}
	    		pAttr.setValue(html);
			}
		} else if ("graphic".equals(attrType)) {
			if (cAttr.getValueType() == CustomAttribute.SET || cAttr.getValueType() == CustomAttribute.ORDEREDSET) {
				String html = "";
				Set v = (Set) cAttr.getValue();
				if (v != null && !v.isEmpty()) {
		    		for (Iterator iter=v.iterator(); iter.hasNext();) {
	    				Attachment attach = (Attachment) iter.next();
		    			if (attach !=  null) {
							String webPath;
							String path;
							String fileName;
							
							webPath = WebUrlUtil.getServletRootURL(request);
							fileName = attach.toString();
							path = WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, attach.getOwner().getEntity(), fileName);
							html = html + "<img src=\"" + path + "\"/>";
		    			}
	    			}
	    		}
	    		pAttr.setValue(html);
			}
		} else {
			processed = false;
		}

		//If the attribute hasn't been processed yet, do it by value type
		if (!processed) {
			if (cAttr != null) {
			    switch(cAttr.getValueType()) {
	    			case CustomAttribute.STRING:
	    			case CustomAttribute.BOOLEAN:
	    			case CustomAttribute.LONG:
	    				pAttr.setValue(cAttr.getValue());
		    			break;
	    			case CustomAttribute.DATE:
	    				Date date = (Date)cAttr.getValue();
	    				DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, user.getLocale());
	    				pAttr.setValue(df.format(date));
	    				break;
		    		case CustomAttribute.ORDEREDSET:
		    		case CustomAttribute.SET:	
		    			Set v = (Set) cAttr.getValue();
		    			if(v != null && !v.isEmpty()){
		    				ArrayList  pvList = new ArrayList<ProfileAttributeListElement>();
		    	    		for (Iterator iter=v.iterator(); iter.hasNext();) {
		    	    			
		    	    			Object nextObj = iter.next();
		    	    			if(nextObj == null) {
		    	    				continue;
		    	    			}
		    	    			
		    	    			if(nextObj instanceof Attachment){
		    	    				Attachment attach = (Attachment) nextObj;
		    		    			if(attach !=  null){
		    		    				
		    		    				ProfileAttributeListElement pAtrLE;
		    		    				ProfileAttributeAttachment pAttach;
		    		    				
		    		    				if(name.equals("picture") && (attach instanceof FileAttachment)){
			    							String webPath;
			    							String path;
			    							String scaledPath;
			    							String fileName;
			    							
			    							webPath = WebUrlUtil.getServletRootURL(request);
			    							fileName = attach.toString();
			    							path = WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, attach.getOwner().getEntity(), fileName);
			    							scaledPath = WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_SCALED, attach.getOwner().getEntity(), fileName);
			    							
			    							//Check if null, this will guarantee we use the first picture we come across
			    							if(profile != null) {
			    								if (Validator.isNull(profile.getPictureUrl())){
			    									profile.addPictureUrl(path);
			    								}
			    								if (Validator.isNull(profile.getPictureScaledUrl())){
			    									profile.addPictureScaledUrl(scaledPath);
			    								}
			    							}
			    							
			    							//Create a new Profile Attribute to convert the data to
						    				pAtrLE = new ProfileAttributeListElement(name, pAttr);
			    		    				pAttach = new ProfileAttributeAttachment(fileName, attach.getId(), path);
			    		    				
		    		    				} else {
		    		    					//Create a new Profile Attribute to convert the data to
						    				pAtrLE = new ProfileAttributeListElement(name, pAttr);
			    		    				pAttach = new ProfileAttributeAttachment(attach.getName(), attach.getId(), attach.toString());
		    		    				}
		    		    				
						    			pAtrLE.setValue(pAttach);
		    		    				
		    		    				//then add them to the linked list
		    		    				pvList.add(pAtrLE);
		    		    			}
		    	    			} else if (nextObj instanceof CustomAttributeListElement){
				    				CustomAttributeListElement cAtrLE = (CustomAttributeListElement) v.iterator().next();
					    			//Create a new Profile Attribute to convert the data to
				    				ProfileAttributeListElement pAtrLE = new ProfileAttributeListElement(name, pAttr);
					    			//then get this attribute lists elements
				    				convertCustomAttrToProfileAttr(request, u, cAtrLE, pAtrLE , name, profile);
			    	    			//then add them to the linked list
				    				pvList.add(((ProfileAttributeListElement)iter.next()).getValue());
		    	    			}
		    	    		}
		    	    		
		    	    		pAttr.setValue(pvList);
		    			}
		    			break;
		    		case CustomAttribute.ATTACHMENT:
		    			Attachment attach = (Attachment) cAttr.getValue();
		    			if(attach !=  null){
		    				ProfileAttributeAttachment pAttach = new ProfileAttributeAttachment(attach.getName(), attach.getId(), attach.toString());
		    				pAttr.setValue(pAttach);
		    			}
		    			break;
		    		case CustomAttribute.DESCRIPTION:
		    			Description desc = (Description) cAttr.getValue();
		    			String text = MarkupUtil.markupStringReplacement(null, null, request, null, u, desc.getText(), WebKeys.MARKUP_VIEW);
		    			pAttr.setValue(text);
		    			break;
		    		default:
		    			pAttr.setValue(cAttr.getValue());
		    			break;
			    }
		    
	 	    } else {
		    	//cAttr is null. There is no value stored for this attribute. Output the default info for "no value"
		    }
		}
	    String caption = DefinitionHelper.findCaptionForAttribute(name, defDoc);
	    pAttr.setTitle(NLT.getDef(caption));
	    
	    //continue to the next value
	    return;
	}
	
	/**
	 * Need to find the Principal from the binderId that is passed in.
	 * 
	 * @param bs
	 * @param sbinderId
	 * @return
	 */
	public static Principal getPrincipalByBinderId(AllModulesInjected bs, String sbinderId) throws OperationAccessControlExceptionNoName {
		//Convert binderID to Long
		Long binderId = Long.valueOf(sbinderId);
		Binder binder = bs.getBinderModule().getBinder(binderId);

		//Get the Owner of the binder
		Principal p = binder.getOwner();
		p = Utils.fixProxy(p);
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
				if (!principals.isEmpty()) {
					p = (Principal)principals.iterator().next();
					p = Utils.fixProxy(p);
				}
			}
		} 
		
		return p;
	}
	
	/**
	 * Get a user's binderId to their MicroBlog,  given a binderId we can look up the 
	 * user that owns this binder and then determine the binderId to their microblog.
	 * 
	 * @param bs
	 * @param sbinderId
	 * @return Long - The microBlogId
	 */
	public static Long getMicroBlogId(AllModulesInjected bs, String sbinderId) throws OperationAccessControlExceptionNoName {
		
		Long microBlogId = null;
		
		Principal p = getPrincipalByBinderId(bs, sbinderId);
		
		List <Long> userIds = new ArrayList<Long>();
		userIds.add(p.getId());

		//Get the User object for this principle
		SortedSet<User> users = bs.getProfileModule().getUsers(userIds);
		User u = null;
		if (!users.isEmpty()) {
			u = users.iterator().next();
			u = (User)Utils.fixProxy(u);
		}
		
		//If we have a user then lets get the microBlogId.
		if(u != null) {
			Long blogId = u.getMiniBlogId();
			if(blogId != null) {
				return microBlogId;
			}
		}
		
		return microBlogId;
	} 
	
	/**
	 * Get the User's status based on the binderId that is passed in.  This will be the status of the user
	 * that the current logged in user is browsing.
	 *  
	 * @param bs
	 * @param sbinderId
	 * @return
	 */
	public static UserStatus getUserStatus(AllModulesInjected bs, String sbinderId) throws OperationAccessControlExceptionNoName {
		//This is the object that is streamed back to the client
		UserStatus userStatus = new UserStatus();
		
		Principal p = getPrincipalByBinderId(bs, sbinderId);
		
		List <Long> userIds = new ArrayList<Long>();
		userIds.add(p.getId());

		//Get the User object for this principle
		SortedSet<User> users = null;
		try{
			users = bs.getProfileModule().getUsers(userIds);
		} catch(AccessControlException ace) {}
		User u = null;
		if (users != null && !users.isEmpty()) {
			u = users.iterator().next();
			u = (User)Utils.fixProxy(u);
		}
		
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
			userStatus.setCurrentDate(new Date());
		}
		
		return userStatus;
	}

	/**
	 * Get the user's stats associated with this binderId. Find the user that belongs to this binderId and the user's stats.
	 * 
	 * @param bs
	 * @param binderId
	 * @return
	 */
	public static ProfileStats getStats(HttpServletRequest request, AllModulesInjected bs, String userId) throws Exception {
		
    	//Get the tracked persons by this user
		ProfileStats stats = new ProfileStats();
		GwtUser user = null;
		
		List<String> trackedIds = getTrackedPersonsIds(bs, userId);
		for(String trackedId: trackedIds) {
				Principal principal = bs.getProfileModule().getEntry(Long.parseLong(trackedId));
				if ((null != principal) && (!(principal.isDeleted()))) {
					principal = Utils.fixProxy(principal);
					Binder binder = bs.getBinderModule().getBinder( principal.getWorkspaceId() );
					
					// Yes!  Construct a GwtUser object for it.
					user = new GwtUser();
					user.setUserId( principal.getId() );
					user.setWorkspaceId( binder.getId() );
					user.setName( principal.getName() );
					user.setTitle( Utils.getUserTitle(principal) );
					user.setWorkspaceTitle( binder.getTitle() );
					user.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( request, binder ) );
					
					stats.addTrackedUser(user);
				}
		}

		//Get the number of recent entries
		//stats.setEntries( Integer.toString( getRecentEntries(bs, binderId) ) );
    	
    	return stats;
    }

    /**
     * This is the persons being tracked by a given user.  Look up user and who the user is following
     * using the binderId.
     * 
     * @param bs
     * @param binderId
     * @return
     */
    public static List<String> getTrackedPersonsIds(AllModulesInjected bs, String userId) {

    	Long userIdL = Long.parseLong(userId);
		List<String> trackedIds = SearchUtils.getTrackedPeopleIds(bs, userIdL);
		
		return trackedIds;
    }
    
    /**
     * Get the recent entries count for this user
     * 
     * @param bs
     * @param binderId
     * @return
     */
	public static int getRecentEntries(AllModulesInjected bs, String binderId){
    	
    	List<String> binderIds = new ArrayList<String>();
		binderIds.add(binderId);
	    
		//get entries created within last 30 days
		Date creationDate = new Date();
		creationDate.setTime(creationDate.getTime() - ObjectKeys.SEEN_TIMEOUT_DAYS*24*60*60*1000);
		
		String startDate = DateTools.dateToString(creationDate, DateTools.Resolution.SECOND);
		String now = DateTools.dateToString(new Date(), DateTools.Resolution.SECOND);
		Criteria crit = SearchUtils.newEntriesDescendants(binderIds);
		crit.add(org.kablink.util.search.Restrictions.between(
				MODIFICATION_DATE_FIELD, startDate, now));
		Map results = bs.getBinderModule().executeSearchQuery(crit, 0, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
    	List<Map> entries = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	
    	return ((entries != null) ? entries.size() : 0);
    }

	public static void getDiskUsageInfo(HttpServletRequest request,
			AllModulesInjected bs, String binderId,
			DiskUsageInfo diskUsageInfo) {
		
		boolean isMirrored = false;
		
		Long binderIdL = Long.valueOf(binderId);
		
		Binder binder = bs.getBinderModule().getBinder(binderIdL);
		if (binder instanceof Folder) {
			isMirrored = ((Folder)binder).isMirrored();
		}
		
		Principal p = getPrincipalByBinderId(bs, binderId);
		
		List <Long> userIds = new ArrayList<Long>();
		userIds.add(p.getId());

		//Get the User object for this principle
		SortedSet<User> users = bs.getProfileModule().getUsers(userIds);
		User u = null;
		if (!users.isEmpty()) {
			u = users.iterator().next();
			u = (User)Utils.fixProxy(u);
		}
		
		if(bs.getAdminModule().isQuotaEnabled()) {
			
			double quotaMax =  bs.getProfileModule().getMaxUserQuota();
			double used = u.getDiskSpaceUsed();
			double maxValue = 1048576;
		
	    	NumberFormat nf = NumberFormat.getInstance();
	    	nf.setMaximumFractionDigits(2);
	    	
		    String quotaMessage = null;
		    
		    if(bs.getProfileModule().isDiskQuotaHighWaterMarkExceeded()  && !bs.getProfileModule().isDiskQuotaExceeded() && !isMirrored){
		    	double value = (quotaMax - used) / maxValue;
		    	String number = nf.format(value);
		    	quotaMessage = NLT.get("quota.nearLimit", new Object[]{number});  
		    }

		    if(bs.getProfileModule().isDiskQuotaExceeded() && !isMirrored){
		    	quotaMessage = NLT.get("quota.diskQuotaExceeded");  
		    }
		    
		    diskUsageInfo.setEnabled(bs.getAdminModule().isQuotaEnabled());
			diskUsageInfo.setExceeded(bs.getProfileModule().isDiskQuotaExceeded());
			diskUsageInfo.setHighWaterMarkExceeded(bs.getProfileModule().isDiskQuotaHighWaterMarkExceeded());
			diskUsageInfo.setMaxQuota((nf.format(quotaMax/maxValue)));
			diskUsageInfo.setUsedQuota((nf.format(used/maxValue)));
			diskUsageInfo.setQuotaMessage(quotaMessage);
		}
	}


    
//    public static int getEntriesByAudit(AllModulesInjected bs, String binderId) {
//		int count = 0;
//    
//    	Long userId = null;
//		Principal p = null;
//		if(binderId != null) {
//			p = getPrincipalByBinderId(bs, binderId);
//		}
//		if(p != null){
//			userId = p.getId();
//		}
//		
//		Set<Long> memberIds = new HashSet();
//		memberIds.add(userId);
//		
//		Date endDate = Calendar.getInstance().getTime();
//		Calendar c = Calendar.getInstance();
//		c.set(1990, 0, 0);
//		
//		Date startDate = c.getTime();
//		
//		
//		List<Map<String,Object>> report = getReportModule().generateActivityReportByUser(memberIds, startDate, endDate, ReportModule.REPORT_TYPE_SUMMARY);
//		Map<String,Object> row = null;
//		if(!report.isEmpty()) row = report.get(0);
//		
//		if(row!=null){
//			Object obj = row.get(AuditTrail.AuditType.add.name());
//			count = obj.toString()
//		}
//    	return count;
//    }
}
