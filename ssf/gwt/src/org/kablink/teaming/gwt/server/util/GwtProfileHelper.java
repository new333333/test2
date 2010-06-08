package org.kablink.teaming.gwt.server.util;

import static org.kablink.util.search.Constants.MODIFICATION_DATE_FIELD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.CustomAttributeListElement;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeAttachment;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.profile.ProfileCategory;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Criteria;

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
				profile.setUserId(u.getId().toString());
				
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
							buildAttributeInfo(u, cat, profile);
					}
				}
		}
		
		return profile;
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
	public static ProfileInfo buildQuickViewProfileInfo(AllModulesInjected bs, Long binderId) {
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
				
				Element item = (Element)configElement.selectSingleNode("//definition/item[@name='profileEntrySimpleView']");
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
							
							List<Element> aElements = catItem.selectNodes("properties/property[@name='_elements']");
							for(Element aElement: aElements) {
								ProfileAttribute attr = new ProfileAttribute();
								
								//Get the Elements name - which is the attribute name
								String attrName = aElement.attributeValue("value");
								attr.setDataName(attrName);
								
								//Now get the title for this attribute
								attr.setTitle(NLT.get("profile.abv.element."+attrName, attrName));
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
							buildAttributeInfo(u, cat, profile);
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
	private static void buildAttributeInfo(User u, ProfileCategory cat, ProfileInfo profile) {
		
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

				//Read the custom attribute
				CustomAttribute cAttr = u.getCustomAttribute(name);
				//Convert the Custom Attribute to a Profile Attribute for serialization purposes
				convertCustomAttrToProfileAttr(u, cAttr, pAttr, name, profile);
				
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
	private static void convertCustomAttrToProfileAttr(User u, CustomAttribute cAttr, ProfileAttribute pAttr, String name, ProfileInfo profile) {
		
		if(cAttr != null) {
		    switch(cAttr.getValueType()) {
    			case CustomAttribute.STRING:
    			case CustomAttribute.BOOLEAN:
    			case CustomAttribute.LONG:
    			case CustomAttribute.DATE:
    				pAttr.setValue(cAttr.getValue());
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
		    							String fileName;
		    							
		    							webPath = WebUrlUtil.getServletRootURL();
		    							fileName = attach.toString();
		    							path = WebUrlUtil.getFileUrl(webPath, "readScaledFile", attach.getOwner().getEntity(), fileName);
		    							
		    							//Check if null, this will guarantee we use the first picture we come across
		    							if(Validator.isNull(profile.getPictureUrl())){
		    								profile.addPictureUrl(path);
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
			    				convertCustomAttrToProfileAttr(u, cAtrLE, pAtrLE , name, profile);
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
	    		default:
	    			pAttr.setValue(cAttr.getValue());
	    			break;
		    }
		    
		    pAttr.setTitle(NLT.get("profile.element."+name, name));
		    
		    //continue to the next value
		    return;
 	    }
	}
	
	/**
	 * Need to find the Principal from the binderId that is passed in.
	 * 
	 * @param bs
	 * @param sbinderId
	 * @return
	 */
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
	
	/**
	 * Get a user's binderId to their MicroBlog,  given a binderId we can look up the 
	 * user that owns this binder and then determine the binderId to their microblog.
	 * 
	 * @param bs
	 * @param sbinderId
	 * @return Long - The microBlogId
	 */
	public static Long getMicroBlogId(AllModulesInjected bs, String sbinderId) {
		
		Long microBlogId = null;
		
		Principal p = getPrincipalByBinderId(bs, sbinderId);
		
		List <Long> userIds = new ArrayList<Long>();
		userIds.add(p.getId());

		//Get the User object for this principle
		SortedSet<User> users = bs.getProfileModule().getUsers(userIds);
		User u = null;
		if (!users.isEmpty()) u = users.iterator().next();
		
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

	/**
	 * Get the user's stats associated with this binderId. Find the user that belongs to this binderId and the user's stats.
	 * 
	 * @param bs
	 * @param binderId
	 * @return
	 */
	public static ProfileStats getStats(AllModulesInjected bs, String binderId) {
		
    	//Get the tracked persons by this user
		ProfileStats stats = new ProfileStats();
		GwtUser user = null;
		
		List<String> trackedIds = getTrackedPersonsIds(bs, binderId);
		for(String trackedId: trackedIds) {
			
			try {
				Principal principal = bs.getProfileModule().getEntry(Long.parseLong(trackedId));
				Binder binder = bs.getBinderModule().getBinder( principal.getWorkspaceId() );
				
				// Yes!  Construct a GwtUser object for it.
				user = new GwtUser();
				user.setUserId( principal.getId() );
				user.setWorkspaceId( binder.getId() );
				user.setName( principal.getName() );
				user.setTitle( principal.getTitle() );
				user.setWorkspaceTitle( binder.getTitle() );
				user.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( binder ) );
				
				stats.addTrackedUser(user);
			}
			catch (AccessControlException ex)
			{
				//No rights to view this user so don't add them to the list
			}
			catch ( Exception e )
			{
				//Log error
				logger.error("Error getting stats for user with binderId "+binderId, e);
			}
		}

		//Get the number of recent entries
		stats.setEntries( Integer.toString( getRecentEntries(bs, binderId) ) );
    	
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
    public static List<String> getTrackedPersonsIds(AllModulesInjected bs, String binderId) {

    	Long binderIdL = Long.parseLong(binderId);
		Binder binder = bs.getBinderModule().getBinder(binderIdL);
		List<String> trackedIds = SearchUtils.getTrackedPeopleIds(bs, binder);
		
		return trackedIds;
    }
    
    /**
     * Get the recent entries count for this user
     * 
     * @param bs
     * @param binderId
     * @return
     */
    @SuppressWarnings("unchecked")
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
