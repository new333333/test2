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

import java.text.SimpleDateFormat;
import java.util.Map;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HKey;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.ws.ElementBuilder;
import com.sitescape.team.module.definition.ws.ElementBuilderUtil;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
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
/*
 * I don't know how to get this from the map
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
*/

		return entryElem;
	}

	protected void addCustomElements(final Element entryElem, final com.sitescape.team.domain.Entry entry) {
		final ElementBuilder.BuilderContext context = this;
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
                if (flagElement.attributeValue("apply").equals("true")) {
                	String fieldBuilder = flagElement.attributeValue("elementBuilder");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
                	ElementBuilderUtil.buildElement(entryElem, entry, nameValue, fieldBuilder, context);
                }
			}
			public String getFlagElementName() { return "webService"; }
		};
		
		getDefinitionModule().walkDefinition(entry, visitor, null);
		
	}
	
}
