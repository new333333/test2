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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.AverageRating;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.ws.ElementBuilder;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.remoting.ws.model.Field;
import com.sitescape.team.remoting.ws.model.Timestamp;
import com.sitescape.team.util.AbstractAllModulesInjected;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;

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
		entryElem.addAttribute("id", (String) entry.get(EntityIndexUtils.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) entry.get(EntityIndexUtils.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) entry.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) entry.get(EntityIndexUtils.TITLE_FIELD));
		if(isPrincipal) {
			entryElem.addAttribute("type", (String) entry.get(EntityIndexUtils.ENTRY_TYPE_FIELD));
			entryElem.addAttribute("reserved", Boolean.toString(entry.get(ProfileIndexUtils.RESERVEDID_FIELD)!=null));
		} else {
			entryElem.addAttribute("docNumber", (String) entry.get(IndexUtils.DOCNUMBER_FIELD));
			entryElem.addAttribute("docLevel", "" + (new HKey((String) entry.get(IndexUtils.SORTNUMBER_FIELD))).getLevel());

			String entryUrl = WebUrlUtil.getEntryViewURL((String) entry.get(EntityIndexUtils.BINDER_ID_FIELD),
					(String) entry.get(EntityIndexUtils.DOCID_FIELD));
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
		
		return entryElem;
	}
	
	protected Element addPrincipalToDocument(Branch doc, Map user)
	{
		Element entryElem = doc.addElement("principal");
		
		entryElem.addAttribute("id", (String) user.get(EntityIndexUtils.DOCID_FIELD));
		entryElem.addAttribute("binderId", (String) user.get(EntityIndexUtils.BINDER_ID_FIELD));
		entryElem.addAttribute("definitionId", (String) user.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD));
		entryElem.addAttribute("title", (String) user.get(EntityIndexUtils.TITLE_FIELD));
		entryElem.addAttribute("emailAddress", (String) user.get(ProfileIndexUtils.EMAIL_FIELD));
		entryElem.addAttribute("type", (String) user.get(EntityIndexUtils.ENTRY_TYPE_FIELD));
		entryElem.addAttribute("reserved", Boolean.toString(user.get(ProfileIndexUtils.RESERVEDID_FIELD)!=null));
		if (EntityIndexUtils.ENTRY_TYPE_USER.equals(user.get(EntityIndexUtils.ENTRY_TYPE_FIELD))) {
			entryElem.addAttribute("name", (String)user.get(ProfileIndexUtils.LOGINNAME_FIELD));			
		} else {
			entryElem.addAttribute("name", (String)user.get(ProfileIndexUtils.GROUPNAME_FIELD));			
		}
/*
 * I don't know how to get this from the map
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
*/

		return entryElem;
	}

	protected void addCustomElements(final Element entryElem, final com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, final com.sitescape.team.domain.DefinableEntity entry) {
		final ElementBuilder.BuilderContext context = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	ElementBuilderUtil.buildElement(entryElem, entityModel, entry, entryElement.attributeValue("name"), nameValue, fieldBuilder, context);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entry, visitor, null);
		
	}
	
	protected void fillFolderEntryModel(com.sitescape.team.remoting.ws.model.FolderEntry entryModel, FolderEntry entry) {
		fillEntryModel(entryModel, entry);
		entryModel.setDocNumber(entry.getDocNumber());
		entryModel.setDocLevel(entry.getDocLevel());
		entryModel.setHref(WebUrlUtil.getEntryViewURL(entry));	
	}
	
	protected void fillEntryModel(com.sitescape.team.remoting.ws.model.Entry entryModel, Entry entry) {
		fillDefinableEntityModel(entryModel, entry);
	}
	
	protected void fillDefinableEntityModel(com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		// Handle static fields first
		fillDefinableEntityModelStatic(entityModel, entity);
		
		// Handle definable fields
		fillDefinableEntityModelDefinable(entityModel, entity);
	}
	
	protected void fillDefinableEntityModelStatic(com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		entityModel.setId(entity.getId());
		
		entityModel.setBinderId(entity.getParentBinder().getId());
		
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
		Calendar cal = Calendar.getInstance();
		cal.setTime(hs.getDate());
		return new Timestamp(hs.getPrincipal().getName(), cal);
	}
	
	protected void fillDefinableEntityModelDefinable(com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity) {
		addCustomElements(null, entityModel, entity);
	}
	
	protected com.sitescape.team.remoting.ws.model.AverageRating toAverageRatingModel(AverageRating ar) {
		return new com.sitescape.team.remoting.ws.model.AverageRating(ar.getAverage(), ar.getCount());
	}

	protected com.sitescape.team.remoting.ws.model.Description toDescriptionModel(Description desc) {
		return new com.sitescape.team.remoting.ws.model.Description(desc.getText(), desc.getFormat());
	}
}
