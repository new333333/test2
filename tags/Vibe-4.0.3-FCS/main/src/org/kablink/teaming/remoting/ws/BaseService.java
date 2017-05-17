/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.ws;

import static org.kablink.util.search.Constants.FAMILY_FIELD;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.AverageRating;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoFileByTheIdException;
import org.kablink.teaming.domain.NoFileVersionByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.ws.ElementBuilder;
import org.kablink.teaming.module.definition.ws.ElementBuilderUtil;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.remoting.ws.model.BinderBrief;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderEntryBrief;
import org.kablink.teaming.remoting.ws.model.PrincipalBrief;
import org.kablink.teaming.remoting.ws.model.Timestamp;
import org.kablink.teaming.remoting.ws.model.Workflow;
import org.kablink.teaming.remoting.ws.model.FileVersions.FileVersion;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.web.multipart.MultipartFile;

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
			return XmlUtil.parseText(xml);
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
		if(entry.getEntryDefId() != null) {
			entryElem.addAttribute("definitionId", entry.getEntryDefId());
		}
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
		addRating(entryElem, entry);
	}
	
	protected void addCommonAttributes(Element entryElem, Map entry)
	{
		entryElem.addAttribute("id", (String) entry.get(Constants.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) entry.get(Constants.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) entry.get(Constants.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) entry.get(Constants.TITLE_FIELD));
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		entryElem.addAttribute("creationDate", sdFormat.format((Date)entry.get(Constants.CREATION_DATE_FIELD)));
		entryElem.addAttribute("creatorName", (String)entry.get(Constants.CREATOR_NAME_FIELD));
		entryElem.addAttribute("creatorTitle", (String)entry.get(Constants.CREATOR_TITLE_FIELD));
		entryElem.addAttribute("modificationDate", sdFormat.format((Date)entry.get(Constants.MODIFICATION_DATE_FIELD)));
		entryElem.addAttribute("modificationName", (String)entry.get(Constants.MODIFICATION_NAME_FIELD));
		entryElem.addAttribute("modificationTitle", (String)entry.get(Constants.MODIFICATION_TITLE_FIELD));
		if (Validator.isNotNull((String) entry.get(Constants.ENTITY_FIELD))) {
			entryElem.addAttribute("permalink", PermaLinkUtil
					.getPermalink(Long.valueOf((String) entry
							.get(Constants.DOCID_FIELD)),
							EntityIdentifier.EntityType.valueOf((String) entry
									.get(Constants.ENTITY_FIELD))));
		}
	}
	
	protected void addBinderAttributes(Element entryElem, Map entry) {
		addCommonAttributes(entryElem, entry);
		entryElem.addAttribute("type", (String) entry.get(Constants.ENTITY_FIELD));
		entryElem.addAttribute("family", (String) entry.get(Constants.FAMILY_FIELD));
		entryElem.addAttribute("library", (String) entry.get(Constants.IS_LIBRARY_FIELD));
		entryElem.addAttribute("mirrored", (String) entry.get(Constants.IS_MIRRORED_FIELD));
		entryElem.addAttribute("path", (String) entry.get(Constants.ENTITY_PATH));
		entryElem.addAttribute("definitionType", (String) entry.get(Constants.DEFINITION_TYPE_FIELD));
		String parentBinderId = (String) entry.get(Constants.BINDERS_PARENT_ID_FIELD);
		if(Validator.isNotNull(parentBinderId))
			entryElem.addAttribute("parentBinderId", parentBinderId);
	}
	
	protected void addAttachmentAttributes(Element entryElem, Map entry) {
		addCommonAttributes(entryElem, entry);
		entryElem.addAttribute("fileID", (String)entry.get(Constants.FILE_ID_FIELD));
		entryElem.addAttribute("fileName", (String)entry.get(Constants.FILENAME_FIELD));
	}
	
	protected void addEntryAttributes(Element entryElem, Map entry, boolean isPrincipal)
	{
		addCommonAttributes(entryElem, entry);
		entryElem.addAttribute("type", (String) entry.get(Constants.ENTRY_TYPE_FIELD));
		if(isPrincipal) {
			entryElem.addAttribute("reserved", Boolean.toString(entry.get(Constants.RESERVEDID_FIELD)!=null));
		} else {
			entryElem.addAttribute("family", (String) entry.get(Constants.FAMILY_FIELD));
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
		entryElem.addAttribute("definitionId", entry.getEntryDefId());
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
			if(((User)entry).getStatusDate() != null) {
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss aa");
				entryElem.addAttribute("statusDate", sdFormat.format(((User)entry).getStatusDate()));
			}
			entryElem.addAttribute("skypeId", ((User)entry).getSkypeId());
			entryElem.addAttribute("twitterId", ((User)entry).getTwitterId());
			if(((User)entry).getMiniBlogId() != null)
				entryElem.addAttribute("miniBlogId", Long.toString(((User)entry).getMiniBlogId()));
			if(((User)entry).getDiskQuota() != null)
				entryElem.addAttribute("diskQuota", Long.toString(((User)entry).getDiskQuota()));
			if(((User)entry).getDiskSpaceUsed() != null)
				entryElem.addAttribute("diskSpaceUsed", Long.toString(((User)entry).getDiskSpaceUsed()));
		}
		
		return entryElem;
	}
	
	protected Element addPrincipalToDocument(Branch doc, Map user)
	{
		Element entryElem = doc.addElement("principal");
		
		entryElem.addAttribute("id", (String) user.get(Constants.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) user.get(Constants.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) user.get(Constants.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("type", (String) user.get(Constants.ENTRY_TYPE_FIELD));
		entryElem.addAttribute("reserved", Boolean.toString(user.get(Constants.RESERVEDID_FIELD)!=null));
		/*
		 * I don't know how to get this from the map
				entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
		*/
		String name = getPrincipalName(user);
		if(name != null) entryElem.addAttribute("name", name);
		
		// We return only statically defined data elements, and ignore all custom elements.
		for(Object key: user.keySet()) {
			if(key instanceof String) {
				String ks = (String) key;
				//if(!ks.startsWith("_")) {
				if(isStaticallyDefinedElement(ks)) {
					Object val = user.get(ks);
					String strVal = getValueAsString(val);
					if(strVal != null)
						entryElem.addAttribute(ks, strVal); 
				}
			}
		}

		return entryElem;
	}

	private boolean isStaticallyDefinedElement(String key) {
		return (key.equals("title") || 
				key.equals("emailAddress") || 
				key.equals("mobileEmailAddress") || 
				key.equals("txtEmailAddress") || 
				key.equals("firstName") || 
				key.equals("middleName") ||
				key.equals("lastName") ||
				key.equals("zonName") ||
				key.equals("skypeId") ||
				key.equals("twitterId") ||
				key.equals("miniBlogId"));
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
	
	protected void addCustomElements(final Element entryElem, final org.kablink.teaming.domain.DefinableEntity entry) {
		addCustomAttributes(entryElem, null, entry);
	}
	
	protected void fillFolderEntryModel(org.kablink.teaming.remoting.ws.model.FolderEntry entryModel, FolderEntry entry) {
		// Entry common
		fillEntryModel(entryModel, entry);
		
		// FolderEntry specific
		entryModel.setDocNumber(entry.getDocNumber());
		entryModel.setDocLevel(entry.getDocLevel());
		entryModel.setHref(WebUrlUtil.getEntryViewURL(entry));
		List<Workflow> wfs = new ArrayList();
		for (WorkflowState state:entry.getWorkflowStates()) {
			Workflow wfModel = new Workflow();
			wfs.add(wfModel);
			wfModel.setTokenId(state.getTokenId());
			wfModel.setDefinitionId(state.getDefinition().getId());
			wfModel.setState(state.getState());
			wfModel.setThreadName(state.getThreadName());
			if (state.getWorkflowChange() != null) {
				wfModel.setModification(toTimestampModel(state.getWorkflowChange()));
			}
			List<org.kablink.teaming.remoting.ws.model.WorkflowResponse> wfr = new ArrayList();
			for (WorkflowResponse response: entry.getWorkflowResponses()) {
				if (response.getDefinitionId().equals(wfModel.getDefinitionId())) {
					org.kablink.teaming.remoting.ws.model.WorkflowResponse wrModel = new org.kablink.teaming.remoting.ws.model.WorkflowResponse();
					wfr.add(wrModel);
					wrModel.setQuestion(response.getName());
					wrModel.setResponse(response.getResponse());
					if (response.getResponseDate() != null) {
						if (response.getResponderId() != null) {
							try {
								wrModel.setResponder(new Timestamp(getProfileModule().getEntry(response.getResponderId()).getName(), response.getResponderId(), response.getResponseDate()));								
							} catch (Exception ex) {
								wrModel.setResponder(new Timestamp("", response.getResponderId(), response.getResponseDate()));								
							}
						} else {
							wrModel.setResponder(new Timestamp("", null, response.getResponseDate()));
						}
					}
				}
			}
			wfModel.setResponses(wfr.toArray(new org.kablink.teaming.remoting.ws.model.WorkflowResponse[wfr.size()]));
		}
		entryModel.setWorkflows(wfs.toArray(new Workflow[wfs.size()]));
		HistoryStamp h = entry.getReservation();
		if(h != null) {
			if(h.getPrincipal() != null)
				entryModel.setReservedBy(h.getPrincipal().getId());
		}
	}
	protected void fillBinderModel(org.kablink.teaming.remoting.ws.model.Binder binderModel, Binder binder) {
		// Binder common
		fillDefinableEntityModel(binderModel, binder);
		binderModel.setPath(binder.getPathName());
    	binderModel.setLibrary(binder.isLibrary());
    	binderModel.setMirrored(binder.isMirrored());
	}
	
	protected void fillBinderBriefModel(BinderBrief brief, Binder binder) {
		brief.setId(binder.getId());
		brief.setTitle(binder.getTitle());
		brief.setEntityType(binder.getEntityType().toString());
    	org.dom4j.Document def = binder.getEntryDefDoc();
    	if(def != null) {
	    	brief.setFamily(DefinitionUtils.getFamily(def));
    	}
		brief.setLibrary(binder.isLibrary());
		brief.setMirrored(binder.isMirrored());
		brief.setDefinitionType(binder.getDefinitionType());
		brief.setPath(binder.getPathName());
		if(binder.getCreation() != null) {
			brief.setCreation(toTimestampModel(binder.getCreation()));
		}
		if(binder.getModification() != null) {
			brief.setModification(toTimestampModel(binder.getModification()));
		}
		brief.setPermaLink(PermaLinkUtil.getPermalink(binder));
	}
	
	protected void fillUserModel(org.kablink.teaming.remoting.ws.model.User userModel, User user) {
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
		userModel.setMiniBlogId(user.getMiniBlogId());
		userModel.setDiskQuota(user.getDiskQuota());
		userModel.setFileSizeLimit(user.getFileSizeLimit());
		userModel.setDiskSpaceUsed(user.getDiskSpaceUsed());
		userModel.setMaxGroupsQuota(user.getMaxGroupsQuota());
		userModel.setMaxGroupsFileSizeLimit(user.getMaxGroupsFileSizeLimit());
		userModel.setWorkspaceId(user.getWorkspaceId());
	}
	
	protected void fillZoneConfigModel(org.kablink.teaming.remoting.ws.model.ZoneConfig zoneConfigModel, ZoneConfig zoneConfig) {
		zoneConfigModel.setFsaEnabled(zoneConfig.getFsaEnabled());
		zoneConfigModel.setFsaSynchInterval(zoneConfig.getFsaSynchInterval());
		zoneConfigModel.setFsaAutoUpdateUrl(zoneConfig.getFsaAutoUpdateUrl());
		zoneConfigModel.setFsaMaxFileSize(zoneConfig.getFsaMaxFileSize() * 1024 * 1024);
		zoneConfigModel.setMobileAccessEnabled(zoneConfig.isMobileAccessEnabled());
		zoneConfigModel.setDiskQuotasEnabled(zoneConfig.isDiskQuotaEnabled());
		zoneConfigModel.setDiskQuotaUserDefault(zoneConfig.getDiskQuotaUserDefault());
		zoneConfigModel.setDiskQuotasHighwaterPercentage(zoneConfig.getDiskQuotasHighwaterPercentage());
		zoneConfigModel.setFileSizeLimitUserDefault(zoneConfig.getFileSizeLimitUserDefault());
		zoneConfigModel.setBinderQuotasInitialized(zoneConfig.isBinderQuotaInitialized());
		zoneConfigModel.setBinderQuotasEnabled(zoneConfig.isBinderQuotaEnabled());
		zoneConfigModel.setBinderQuotasAllowOwner(zoneConfig.isBinderQuotaAllowBinderOwnerEnabled());
		zoneConfigModel.setFileVersionsMaxAge(zoneConfig.getFileVersionsMaxAge());
	}
	
	protected void fillGroupModel(org.kablink.teaming.remoting.ws.model.Group groupModel, Group group) {
		fillPrincipalModel(groupModel, group);
		groupModel.setDiskQuota(group.getDiskQuota());
		groupModel.setFileSizeLimit(group.getFileSizeLimit());
	}
	
	protected void fillPrincipalModel(org.kablink.teaming.remoting.ws.model.Principal principalModel, Principal entry) {
		// Entry common
		fillEntryModel(principalModel, entry);

		// Principal specific
		principalModel.setEmailAddress(entry.getEmailAddress());
		principalModel.setDisabled(entry.isDeleted());
		principalModel.setReserved(entry.isReserved());
		principalModel.setName(entry.getName());
	}
	
	protected void fillEntryModel(org.kablink.teaming.remoting.ws.model.Entry entryModel, Entry entry) {
		// DefinableEntity common
		fillDefinableEntityModel(entryModel, entry);
		
		// Nothing specific to Entry
	}
	
	protected void fillDefinableEntityModel(org.kablink.teaming.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		// Handle static fields
		fillDefinableEntityModelStatic(entityModel, entity);
		
		// Handle definable fields
		fillDefinableEntityModelDefinable(entityModel, entity);
		
		// Set permalink
		entityModel.setPermaLink(PermaLinkUtil.getPermalink(entity));
	}
	
	protected void fillDefinableEntityModelStatic(org.kablink.teaming.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		entityModel.setId(entity.getId());
		
		if (entity.getParentBinder() != null) 
			entityModel.setParentBinderId(entity.getParentBinder().getId());
		
		if(entity.getEntryDefId() != null)
			entityModel.setDefinitionId(entity.getEntryDefId());
		
		entityModel.setTitle(entity.getTitle());
		
		Description desc = entity.getDescription();
		if(desc != null) {
			entityModel.setDescription(toDescriptionModel(entity, desc));
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
		if(entity.getEntityType() != null) {
			entityModel.setEntityType(entity.getEntityType().name());
		}
    	org.dom4j.Document def = entity.getEntryDefDoc();
    	if(def != null) {
    		entityModel.setFamily(DefinitionUtils.getFamily(def));
    	}
	}
	
	protected org.kablink.teaming.remoting.ws.model.Timestamp toTimestampModel(HistoryStamp hs) {
		return new Timestamp(Utils.redactUserPrincipalIfNecessary(hs.getPrincipal()).getName(), hs.getPrincipal().getId(), hs.getDate());
	}
	
	protected void fillDefinableEntityModelDefinable(final org.kablink.teaming.remoting.ws.model.DefinableEntity entityModel, final DefinableEntity entity) {
		addCustomAttributes(null, entityModel, entity);
	}
	
	protected void addCustomAttributes(final Element entryElem, final org.kablink.teaming.remoting.ws.model.DefinableEntity entityModel, final DefinableEntity entity) {
		final ElementBuilder.BuilderContext context = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
				
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
                	String typeValue = entryElement.attributeValue("name");
  					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = typeValue;}
                	ElementBuilderUtil.buildElement(entryElem, entityModel, entity, typeValue, nameValue, fieldBuilder, context);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entity, visitor, null);
		//see if attachments have been handled
		Element root = entity.getEntryDefDoc().getRootElement();
		if (root == null) return;
		Element attachments = (Element)root.selectSingleNode("//item[@name='attachFiles']");
		if (attachments != null) return; // already processed
      	//Force processing of attachments.  Not all forms will have an attachment element,
    	//but this is the only code that actually sends the files, even if they
    	//are part of a graphic or file element.  So force attachment processing to pick up all files
		attachments = (Element)getDefinitionModule().getDefinitionConfig().getRootElement().selectSingleNode("//item[@name='attachFiles']");
		Element flagElem = (Element) attachments.selectSingleNode("webService");
		ElementBuilderUtil.buildElement(entryElem, entityModel, entity, "attachFiles", DefinitionUtils.getPropertyValue(attachments, "name"),
				flagElem.attributeValue("elementBuilder"), context);
        	 		 

	}

	protected org.kablink.teaming.remoting.ws.model.AverageRating toAverageRatingModel(AverageRating ar) {
		return new org.kablink.teaming.remoting.ws.model.AverageRating(ar.getAverage(), ar.getCount());
	}

	protected org.kablink.teaming.remoting.ws.model.Description toDescriptionModel(DefinableEntity entity, Description desc) {
		return new org.kablink.teaming.remoting.ws.model.Description(desc.getText(), desc.getFormat());
	}
	protected org.kablink.teaming.remoting.ws.model.Subscription toSubscriptionModel(Subscription subscription) {
		org.kablink.teaming.remoting.ws.model.Subscription model = new org.kablink.teaming.remoting.ws.model.Subscription();
		Map<Integer, String[]> styles = subscription.getStyles();
		for (Map.Entry<Integer, String[]>me: styles.entrySet()) {
			model.addStyle(me.getKey(), me.getValue());
		}
		
		model.setEntityId(subscription.getId().getEntityId());
		return model;
	}
	protected org.kablink.teaming.remoting.ws.model.Tag toTagModel(Tag tag) {
		org.kablink.teaming.remoting.ws.model.Tag model = new org.kablink.teaming.remoting.ws.model.Tag();
		model.setId(tag.getId());
		model.setName(tag.getName());
		model.setPublic(tag.isPublic());
		model.setEntityId(tag.getEntityIdentifier().getEntityId());
		return model;
	}
	protected FolderEntryBrief toFolderEntryBrief(FolderEntry entry) {
		FolderEntryBrief entryBrief = new FolderEntryBrief();
		entryBrief.setId(entry.getId());
		entryBrief.setBinderId(entry.getParentBinder().getId());
		if(entry.getEntryDefId() != null)
			entryBrief.setDefinitionId(entry.getEntryDefId());
		entryBrief.setTitle(entry.getTitle());
		entryBrief.setDocNumber(entry.getDocNumber());
		entryBrief.setDocLevel(entry.getDocLevel());
		entryBrief.setHref(WebUrlUtil.getEntryViewURL(entry));
		entryBrief.setPermaLink(PermaLinkUtil.getPermalink(entry));
		Set<FileAttachment> fileAttachments = entry.getFileAttachments();
		if(fileAttachments.size() > 0) {
			String[] fileNames = new String[fileAttachments.size()];
			int i = 0;
			for(FileAttachment fa:fileAttachments)
				fileNames[i++] = fa.getFileItem().getName();
			entryBrief.setFileNames(fileNames);
		}
		if(entry.getCreation() != null) {
			entryBrief.setCreation(toTimestampModel(entry.getCreation()));
		}
		if(entry.getModification() != null) {
			entryBrief.setModification(toTimestampModel(entry.getModification()));
		}
		if(entry.getReservation() != null && entry.getReservation().getPrincipal() != null)
			entryBrief.setReservedBy(entry.getReservation().getPrincipal().getId());
    	org.dom4j.Document def = entry.getEntryDefDoc();
    	if(def != null) {
    		entryBrief.setFamily(DefinitionUtils.getFamily(def));
    	}
		return entryBrief;
	}	

	protected FolderEntryBrief toFolderEntryBrief(Map entry) {
		FolderEntryBrief entryBrief = new FolderEntryBrief();
		entryBrief.setId(Long.valueOf((String) entry.get(Constants.DOCID_FIELD)));
		entryBrief.setBinderId(Long.valueOf((String) entry.get(Constants.BINDER_ID_FIELD)));
		entryBrief.setDefinitionId((String) entry.get(Constants.COMMAND_DEFINITION_FIELD));
		entryBrief.setTitle((String) entry.get(Constants.TITLE_FIELD));
		entryBrief.setDocNumber((String) entry.get(Constants.DOCNUMBER_FIELD));
		entryBrief.setDocLevel((new HKey((String) entry.get(Constants.SORTNUMBER_FIELD))).getLevel());
		entryBrief.setHref(WebUrlUtil.getEntryViewURL((String) entry.get(Constants.BINDER_ID_FIELD),
					(String) entry.get(Constants.DOCID_FIELD)));
		entryBrief.setPermaLink(PermaLinkUtil.getPermalink
				(Long.valueOf((String) entry.get(Constants.DOCID_FIELD)), 
						EntityIdentifier.EntityType.valueOf((String) entry.get(Constants.ENTITY_FIELD))));
		entryBrief.setFileNames(getValueAsStringArray(entry.get(Constants.FILENAME_FIELD)));
		
		UserPrincipal creator = Utils.redactUserPrincipalIfNecessary(Long.valueOf((String) entry.get(Constants.CREATORID_FIELD)));
		UserPrincipal modifier = Utils.redactUserPrincipalIfNecessary(Long.valueOf((String) entry.get(Constants.MODIFICATIONID_FIELD)));
		
		entryBrief.setCreation(new Timestamp(((creator != null)? creator.getName() : (String) entry.get(Constants.CREATOR_NAME_FIELD)),
				Long.valueOf((String)entry.get(Constants.CREATORID_FIELD)),
				(Date) entry.get(Constants.CREATION_DATE_FIELD)));
		entryBrief.setModification(new Timestamp(((modifier != null)? modifier.getName() : (String) entry.get(Constants.MODIFICATION_NAME_FIELD)),
				Long.valueOf((String)entry.get(Constants.MODIFICATIONID_FIELD)),
				(Date) entry.get(Constants.MODIFICATION_DATE_FIELD)));
		String reservedByStr = (String) entry.get(Constants.RESERVEDBY_ID_FIELD);
		if(Validator.isNotNull(reservedByStr))
			entryBrief.setReservedBy(Long.valueOf(reservedByStr));
		entryBrief.setFamily((String) entry.get(Constants.FAMILY_FIELD));
		return entryBrief;
	}
	
	protected PrincipalBrief toPrincipalBrief(Map principal) {
		PrincipalBrief principalBrief = new PrincipalBrief();
		setPrincipalBrief(principal, principalBrief);
		return principalBrief;
	}
	
	private String getValueAsString(Object value) {
		if(value == null) {
			return null;
		}
		else if(value instanceof String) {
			return (String) value;
		}
		else if(value instanceof SearchFieldResult) {
			return ((SearchFieldResult) value).getValueString();
		}
		else {
			return null;
		}
	}
	
	private String[] getValueAsStringArray(Object value) {
		if(value == null) {
			return null;
		}
		else if(value instanceof String) {
			if(!value.equals(""))
				return new String[] {(String) value};
			else
				return null;
		}
		else if(value instanceof SearchFieldResult) {
			return ((SearchFieldResult) value).getValueSet().toArray(new String[0]);
		}
		else {
			return null;
		}
	}
	
	protected void setPrincipalBrief(Map principal, PrincipalBrief principalBrief) {
		principalBrief.setId(Long.valueOf((String) principal.get(Constants.DOCID_FIELD)));
		principalBrief.setBinderId(Long.valueOf((String) principal.get(Constants.BINDER_ID_FIELD)));
		principalBrief.setDefinitionId((String) principal.get(Constants.COMMAND_DEFINITION_FIELD));
		principalBrief.setTitle((String) principal.get(Constants.TITLE_FIELD));
		principalBrief.setEmailAddress(getValueAsString(principal.get(Constants.EMAIL_FIELD)));
		principalBrief.setType((String) principal.get(Constants.ENTRY_TYPE_FIELD));
		principalBrief.setReserved(principal.get(Constants.RESERVEDID_FIELD)!=null);
		String name = getPrincipalName(principal);
		if(name != null)
			principalBrief.setName(name);
		
		// The "disabled" field value is not stored in index. Also, getting the value from
		// database is expensive. So, in this use case, we will not return the value. 
	}
	
	protected PrincipalBrief toPrincipalBrief(Principal principal) {
		PrincipalBrief principalBrief = new PrincipalBrief(
				principal.getId(),
				principal.getParentBinder().getId(),
				principal.getEntryDefId(),
				principal.getTitle(),
				principal.getEmailAddress(),
				principal.getEntityType().toString(),
				Boolean.valueOf(principal.isDisabled()),
				principal.isReserved(),
				principal.getName()
				);
		
		return principalBrief;
	}
	
	protected FileVersions toFileVersions(FileAttachment fa) {
		Set<VersionAttachment> vatts = fa.getFileVersions();
		FileVersion[] versions = new FileVersion[vatts.size()];
		int i = 0;
		for(VersionAttachment vatt : vatts) {
			versions[i++] = new FileVersion(vatt.getId(),
					vatt.getVersionNumber(),
					vatt.getMajorVersion(),
					vatt.getMinorVersion(),
					new Timestamp(Utils.redactUserPrincipalIfNecessary(vatt.getCreation().getPrincipal()).getName(), 
							vatt.getCreation().getPrincipal().getId(),
							vatt.getCreation().getDate()),
					new Timestamp(Utils.redactUserPrincipalIfNecessary(vatt.getModification().getPrincipal()).getName(), 
							vatt.getModification().getPrincipal().getId(),
							vatt.getModification().getDate()),
					vatt.getFileItem().getLength(),
					WebUrlUtil.getFileUrl((String)null, WebKeys.ACTION_READ_FILE, vatt),
					vatt.getFileItem().getDescription().getText(), 
					vatt.getFileStatus().intValue());
		}
		return new FileVersions(fa.getFileItem().getName(), versions);
	}
	protected String definitionToId(Definition def) {
		if(def != null)
			return def.getId();
		else
			return null;
	}
	protected String[] definitionsToIds(List<Definition> defs) {
		String[] ids = null;
		if(defs != null) {
			ids = new String[defs.size()];
			for(int i = 0; i < defs.size(); i++)
				ids[i] = defs.get(i).getId(); 
		}
		return null;
	}
	protected Map wrapFileItemInMap(String elementName, String fileName, InputStream fileContent) {
		MultipartFile mf = new SimpleMultipartFile(fileName, fileContent); 
		Map fileItems = new HashMap();	
		fileItems.put(elementName, mf); // single file item
		return fileItems;
	}
	protected byte[] getFileAttachmentAsByteArray(Binder binder, DefinableEntity entity, String attachmentId) {
		FileAttachment fatt = getFileAttachment(entity, attachmentId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		getFileModule().readFile(binder, entity, fatt, baos);
		return baos.toByteArray();
	}
	
	protected FileAttachment getFileAttachment(DefinableEntity entity, String attachmentId) {
		Attachment att = entity.getAttachment(attachmentId);
		if(att == null || !(att instanceof FileAttachment))
			throw new NoFileByTheIdException(attachmentId);
		return (FileAttachment) att;
	}
	
	protected byte[] getFileVersionAsByteArray(Binder binder, DefinableEntity entity, String fileVersionId) {
		VersionAttachment va = getVersionAttachment(entity, fileVersionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		getFileModule().readFile(binder, entity, va, baos);
		return baos.toByteArray();
	}
	
	protected VersionAttachment getVersionAttachment(DefinableEntity entry, String fileVersionId) 
	throws NoFileVersionByTheIdException {
		VersionAttachment fileVer;
		for (Attachment attachment : entry.getAttachments()) {
			if (attachment instanceof FileAttachment) {
				fileVer = ((FileAttachment)attachment).findFileVersionById(fileVersionId);
				if(fileVer != null)
					return fileVer;
			}
		}
		throw new NoFileVersionByTheIdException(fileVersionId);
	}
	
	/*
	protected VersionAttachment getVersionAttachment(DefinableEntity entity, String attachmentId, String fileVersionId) {
		Attachment att = entity.getAttachment(attachmentId);
		if(att == null || !(att instanceof FileAttachment))
			throw new IllegalArgumentException("No such file attachment [" + attachmentId + "]");
		FileAttachment fatt = (FileAttachment) att;
		VersionAttachment va = fatt.findFileVersionById(fileVersionId);
		if(va == null)
			throw new IllegalArgumentException("No such file version [" + fileVersionId + "]");
		return va;
	}
	*/
	
	protected void getTimestamps(Map options, org.kablink.teaming.remoting.ws.model.DefinableEntity entry)
	{
		Timestamp creation = entry.getCreation();
		if(creation != null) {
			if(creation.getPrincipal() != null)
				options.put(ObjectKeys.INPUT_OPTION_CREATION_NAME, creation.getPrincipal());
			if(creation.getPrincipalId() != null)
				options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, creation.getPrincipalId());
			if(creation.getDate() != null)
				options.put(ObjectKeys.INPUT_OPTION_CREATION_DATE, creation.getDate());
		}
		Timestamp modification = entry.getModification();
		if(modification != null) {
			if(modification.getPrincipal() != null)
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME, modification.getPrincipal());
			if(modification.getPrincipalId() != null)
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_ID, modification.getPrincipalId());
			if(modification.getDate() != null)
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, modification.getDate());
		}
	}

}
