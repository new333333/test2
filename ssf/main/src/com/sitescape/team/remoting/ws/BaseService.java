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
package com.sitescape.team.remoting.ws;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.AverageRating;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.ws.ElementBuilder;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.remoting.ws.model.FolderEntryBrief;
import com.sitescape.team.remoting.ws.model.PrincipalBrief;
import com.sitescape.team.remoting.ws.model.Timestamp;
import com.sitescape.team.remoting.ws.model.Workflow;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;

public class BaseService extends AbstractAllModulesInjected implements ElementBuilder.BuilderContext {

	protected AttachmentHandler attachmentHandler = new AttachmentHandler();

	public void handleAttachment(FileAttachment att, String webUrl)
	{
		attachmentHandler.handleAttachment(att, webUrl);
	}

	public static class AttachmentHandler {
		public void handleAttachment(FileAttachment att, String webUrl) {}
	}

	protected Document getDocument(String xml) {
		// Parse XML string into a document tree.
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	protected void addRating(Element element, DefinableEntity entity)
	{
		if(entity.getAverageRating() != null) {
			element.addAttribute("averageRating", entity.getAverageRating().getAverage().toString());
			element.addAttribute("ratingCount", entity.getAverageRating().getCount().toString());
		}
	}

	protected void addEntryAttributes(Element entryElem, FolderEntry entry)
	{
        entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		if(entry.getEntryDef() != null) {
			entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		}
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
		addRating(entryElem, entry);
	}
	
	protected void addEntryAttributes(Element entryElem, Map entry, boolean isPrincipal)
	{
		entryElem.addAttribute("id", (String) entry.get(Constants.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) entry.get(Constants.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) entry.get(Constants.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) entry.get(Constants.TITLE_FIELD));
		if(isPrincipal) {
			entryElem.addAttribute("type", (String) entry.get(Constants.ENTRY_TYPE_FIELD));
			entryElem.addAttribute("reserved", Boolean.toString(entry.get(Constants.RESERVEDID_FIELD)!=null));
		} else {
			entryElem.addAttribute("docNumber", (String) entry.get(Constants.DOCNUMBER_FIELD));
			entryElem.addAttribute("docLevel", "" + (new HKey((String) entry.get(Constants.SORTNUMBER_FIELD))).getLevel());

			String entryUrl = WebUrlUtil.getEntryViewURL((String) entry.get(Constants.BINDER_ID_FIELD),
					(String) entry.get(Constants.DOCID_FIELD));
			entryElem.addAttribute("href", entryUrl);
		}
	}

	protected Element addPrincipalToDocument(Branch doc, Principal entry)
	{
		Element entryElem = doc.addElement("principal");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("emailAddress", entry.getEmailAddress());
		entryElem.addAttribute("type", entry.getEntityType().toString());
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
		entryElem.addAttribute("reserved", Boolean.toString(entry.isReserved()));
		entryElem.addAttribute("name", entry.getName());
		if (entry instanceof User) {
			entryElem.addAttribute("firstName", ((User)entry).getFirstName());
			entryElem.addAttribute("middleName", ((User)entry).getMiddleName());
			entryElem.addAttribute("lastName", ((User)entry).getLastName());
			entryElem.addAttribute("zonName", ((User)entry).getZonName());
			entryElem.addAttribute("status", ((User)entry).getStatus());
			entryElem.addAttribute("skypeId", ((User)entry).getSkypeId());
			entryElem.addAttribute("twitterId", ((User)entry).getTwitterId());
		}
		
		return entryElem;
	}
	
	protected Element addPrincipalToDocument(Branch doc, Map user)
	{
		Element entryElem = doc.addElement("principal");
		
		entryElem.addAttribute("id", (String) user.get(Constants.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) user.get(Constants.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) user.get(Constants.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) user.get(Constants.TITLE_FIELD));
		entryElem.addAttribute("emailAddress", (String) user.get(Constants.EMAIL_FIELD));
		entryElem.addAttribute("type", (String) user.get(Constants.ENTRY_TYPE_FIELD));
		entryElem.addAttribute("reserved", Boolean.toString(user.get(Constants.RESERVEDID_FIELD)!=null));
		String name = getPrincipalName(user);
		if(name != null) entryElem.addAttribute("name", name);
		
		if (user.containsKey(Constants.FIRSTNAME_FIELD)) entryElem.addAttribute("firstName", (String) user.get(Constants.FIRSTNAME_FIELD));
		if (user.containsKey(Constants.MIDDLENAME_FIELD)) entryElem.addAttribute("middleName", (String) user.get(Constants.MIDDLENAME_FIELD));
		if (user.containsKey(Constants.LASTNAME_FIELD)) entryElem.addAttribute("lastName", (String) user.get(Constants.LASTNAME_FIELD));
		if (user.containsKey(Constants.ZONNAME_FIELD)) entryElem.addAttribute("zonName", (String) user.get(Constants.ZONNAME_FIELD));
		if (user.containsKey(Constants.STATUS_FIELD)) entryElem.addAttribute("status", (String) user.get(Constants.STATUS_FIELD));
		if (user.containsKey(Constants.SKYPEID_FIELD)) entryElem.addAttribute("skypeId", (String) user.get(Constants.SKYPEID_FIELD));
		if (user.containsKey(Constants.TWITTERID_FIELD)) entryElem.addAttribute("twitterId", (String) user.get(Constants.TWITTERID_FIELD));
/*
 * I don't know how to get this from the map
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
*/

		return entryElem;
	}

	String getPrincipalName(Map user) {
		if (Constants.ENTRY_TYPE_USER.equals(user.get(Constants.ENTRY_TYPE_FIELD))) {
			return (String)user.get(Constants.LOGINNAME_FIELD);			
		} else if (Constants.ENTRY_TYPE_GROUP.equals(user.get(Constants.ENTRY_TYPE_FIELD))) {
			return (String)user.get(Constants.GROUPNAME_FIELD);						
		} else if (Constants.ENTRY_TYPE_APPLICATION.equals(user.get(Constants.ENTRY_TYPE_FIELD))) {
			return (String)user.get(Constants.APPLICATION_NAME_FIELD);						
		} else if (Constants.ENTRY_TYPE_APPLICATION_GROUP.equals(user.get(Constants.ENTRY_TYPE_FIELD))) {
			return (String)user.get(Constants.GROUPNAME_FIELD);						
		} else {
			return null;
		}
	}
	
	protected void addCustomElements(final Element entryElem, final com.sitescape.team.domain.DefinableEntity entry) {
		addCustomAttributes(entryElem, null, entry);
	}
	
	protected void fillFolderEntryModel(com.sitescape.team.remoting.ws.model.FolderEntry entryModel, FolderEntry entry) {
		// Entry common
		fillEntryModel(entryModel, entry);
		
		// FolderEntry specific
		entryModel.setDocNumber(entry.getDocNumber());
		entryModel.setDocLevel(entry.getDocLevel());
		entryModel.setHref(WebUrlUtil.getEntryViewURL(entry));
		for (WorkflowState state:entry.getWorkflowStates()) {
			Workflow wfModel = new Workflow();
			wfModel.setTokenId(state.getTokenId());
			wfModel.setDefinitionId(state.getDefinition().getId());
			wfModel.setState(state.getState());
			wfModel.setThreadName(state.getThreadName());
			if (state.getWorkflowChange() != null) {
				wfModel.setModification(toTimestampModel(state.getWorkflowChange()));
			}
			entryModel.addWorkflow(wfModel);
		}
	}
	protected void fillBinderModel(com.sitescape.team.remoting.ws.model.Binder binderModel, Binder binder) {
		// Binder common
		fillDefinableEntityModel(binderModel, binder);
		
	}
	protected void fillUserModel(com.sitescape.team.remoting.ws.model.User userModel, User user) {
		// Principal common
		fillPrincipalModel(userModel, user);
		
		// Note: With this implementation, most of the statically-known attributes (eg. first name)
		// are also duplicated as definable attributes, due to the way our system works. 
		// At the moment, we'll not make an effort to eliminate this duplicate from the model
		// object returned from this method, since they are harmless.  
		
		// User specific
		userModel.setFirstName(user.getFirstName());
		userModel.setMiddleName(user.getMiddleName());
		userModel.setLastName(user.getLastName());
		userModel.setOrganization(user.getOrganization());
		userModel.setPhone(user.getPhone());
		userModel.setZonName(user.getZonName());
		Locale locale = user.getLocale();
		if(locale != null) {
			userModel.setLocaleLanguage(locale.getLanguage());
			userModel.setLocaleCountry(locale.getCountry());
		}
		if(user.getTimeZone() != null) {
			userModel.setTimeZone(user.getTimeZone().getID());
		}
		userModel.setSkypeId(user.getSkypeId());
		userModel.setTwitterId(user.getTwitterId());
	}
	
	protected void fillGroupModel(com.sitescape.team.remoting.ws.model.Group groupModel, Group group) {
		fillPrincipalModel(groupModel, group);
	}
	
	protected void fillPrincipalModel(com.sitescape.team.remoting.ws.model.Principal principalModel, Principal entry) {
		// Entry common
		fillEntryModel(principalModel, entry);

		// Principal specific
		principalModel.setEmailAddress(entry.getEmailAddress());
		principalModel.setDisabled(entry.isDeleted());
		principalModel.setReserved(entry.isReserved());
		principalModel.setName(entry.getName());
	}
	
	protected void fillEntryModel(com.sitescape.team.remoting.ws.model.Entry entryModel, Entry entry) {
		// DefinableEntity common
		fillDefinableEntityModel(entryModel, entry);
		
		// Nothing specific to Entry
	}
	
	protected void fillDefinableEntityModel(com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		// Handle static fields
		fillDefinableEntityModelStatic(entityModel, entity);
		
		// Handle definable fields
		fillDefinableEntityModelDefinable(entityModel, entity);
	}
	
	protected void fillDefinableEntityModelStatic(com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		entityModel.setId(entity.getId());
		
		if (entity.getParentBinder() != null) 
			entityModel.setParentBinderId(entity.getParentBinder().getId());
		
		if(entity.getEntryDef() != null)
			entityModel.setDefinitionId(entity.getEntryDef().getId());
		
		entityModel.setTitle(entity.getTitle());
		
		Description desc = entity.getDescription();
		if(desc != null) {
			entityModel.setDescription(toDescriptionModel(desc));
		}
		
		if(entity.getCreation() != null) {
			entityModel.setCreation(toTimestampModel(entity.getCreation()));
		}
		if(entity.getModification() != null) {
			entityModel.setModification(toTimestampModel(entity.getModification()));
		}
		
		if(entity.getAverageRating() != null) {
			entityModel.setAverageRating(toAverageRatingModel(entity.getAverageRating()));
		}
	}
	
	protected com.sitescape.team.remoting.ws.model.Timestamp toTimestampModel(HistoryStamp hs) {
		return new Timestamp(hs.getPrincipal().getName(), hs.getDate());
	}
	
	protected void fillDefinableEntityModelDefinable(final com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, final DefinableEntity entity) {
		addCustomAttributes(null, entityModel, entity);
	}
	
	protected void addCustomAttributes(final Element entryElem, final com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, final DefinableEntity entity) {
		final ElementBuilder.BuilderContext context = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	ElementBuilderUtil.buildElement(entryElem, entityModel, entity, entryElement.attributeValue("name"), nameValue, fieldBuilder, context);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entity, visitor, null);
	}

	protected com.sitescape.team.remoting.ws.model.AverageRating toAverageRatingModel(AverageRating ar) {
		return new com.sitescape.team.remoting.ws.model.AverageRating(ar.getAverage(), ar.getCount());
	}

	protected com.sitescape.team.remoting.ws.model.Description toDescriptionModel(Description desc) {
		return new com.sitescape.team.remoting.ws.model.Description(desc.getText(), desc.getFormat());
	}
	protected com.sitescape.team.remoting.ws.model.Subscription toSubscriptionModel(Subscription subscription) {
		com.sitescape.team.remoting.ws.model.Subscription model = new com.sitescape.team.remoting.ws.model.Subscription();
		Map<Integer, String[]> styles = subscription.getStyles();
		for (Map.Entry<Integer, String[]>me: styles.entrySet()) {
			model.addStyle(me.getKey(), me.getValue());
		}
		
		model.setEntityId(subscription.getId().getEntityId());
		return model;
	}
	protected FolderEntryBrief toFolderEntryBrief(FolderEntry entry) {
		FolderEntryBrief entryBrief = new FolderEntryBrief();
		entryBrief.setId(entry.getId());
		entryBrief.setBinderId(entry.getParentBinder().getId());
		if(entry.getEntryDef() != null)
			entryBrief.setDefinitionId(entry.getEntryDef().getId());
		entryBrief.setTitle(entry.getTitle());
		entryBrief.setDocNumber(entry.getDocNumber());
		entryBrief.setDocLevel(entry.getDocLevel());
		entryBrief.setHref(WebUrlUtil.getEntryViewURL(entry));
		if(entry.getCreation() != null) {
			entryBrief.setCreation(toTimestampModel(entry.getCreation()));
		}
		if(entry.getModification() != null) {
			entryBrief.setModification(toTimestampModel(entry.getModification()));
		}
		if(entry.getAverageRating() != null) {
			entryBrief.setAverageRating(toAverageRatingModel(entry.getAverageRating()));
		}	
		return entryBrief;
	}	

	protected PrincipalBrief toPrincipalBrief(Map principal) {
		PrincipalBrief principalBrief = new PrincipalBrief();
		principalBrief.setId(Long.valueOf((String) principal.get(Constants.DOCID_FIELD)));
		principalBrief.setBinderId(Long.valueOf((String) principal.get(Constants.BINDER_ID_FIELD)));
		principalBrief.setDefinitionId((String) principal.get(Constants.COMMAND_DEFINITION_FIELD));
		principalBrief.setTitle((String) principal.get(Constants.TITLE_FIELD));
		principalBrief.setEmailAddress((String) principal.get(Constants.EMAIL_FIELD));
		principalBrief.setType((String) principal.get(Constants.ENTRY_TYPE_FIELD));
		principalBrief.setReserved(principal.get(Constants.RESERVEDID_FIELD)!=null);
		String name = getPrincipalName(principal);
		if(name != null)
			principalBrief.setName(name);
		
/*
 * I don't know how to get this from the map
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
*/

		return principalBrief;
	}
	
	protected PrincipalBrief toPrincipalBrief(Principal principal) {
		PrincipalBrief principalBrief = new PrincipalBrief(
				principal.getId(),
				principal.getParentBinder().getId(),
				principal.getEntryDef().getId(),
				principal.getTitle(),
				principal.getEmailAddress(),
				principal.getEntityType().toString(),
				Boolean.valueOf(principal.isDisabled()),
				principal.isReserved(),
				principal.getName()
				);
		
		return principalBrief;
	}
}
