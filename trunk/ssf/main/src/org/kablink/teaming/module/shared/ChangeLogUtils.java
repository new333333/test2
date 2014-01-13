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
package org.kablink.teaming.module.shared;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.TimeZone;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.DefinitionHelper;


public class ChangeLogUtils {

	protected static SharingModule getSharingModule() {
		return (SharingModule)SpringContextUtil.getBean("sharingModule");
	}

	protected static CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	}

	protected static AdminModule getAdminModule() {
		return (AdminModule)SpringContextUtil.getBean("adminModule");
	}

	public static Element buildLog(ChangeLog changes, DefinableEntity entry) {
		Element element = changes.getEntityRoot();
		if (entry instanceof Binder)
			EntityIndexUtils.addReadAccess(element, (Binder)entry, true);
		else
			EntityIndexUtils.addReadAccess(element, entry.getParentBinder(), entry, true);
		
		//See if this is shared
		ShareItemSelectSpec spec = new ShareItemSelectSpec();
		spec.setSharedEntityIdentifier(entry.getEntityIdentifier());
		List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
		if (!shareItems.isEmpty()) {
			//Add the sharing actions to this report
 			Element shares = element.addElement(ObjectKeys.XTAG_ENTITY_SHARES);
 			for (ShareItem shareItem: shareItems) {
 				if (shareItem.isLatest()) {
	 				Date shareStartDate = shareItem.getStartDate();
	 				Date expirationDate = shareItem.getEndDate();
	 			    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 			    sd.setTimeZone(TimeZone.getTimeZone("GMT"));
	 	 			Element share = shares.addElement(ObjectKeys.XTAG_ENTITY_SHARE);
	 	 			share.addAttribute(ObjectKeys.XTAG_ENTITY_SHARE_SHARER_ID, String.valueOf(shareItem.getSharerId()));
	 	 			share.addAttribute(ObjectKeys.XTAG_ENTITY_SHARE_RECIPIENT_TYPE, shareItem.getRecipientType().toString());
	 	 			share.addAttribute(ObjectKeys.XTAG_ENTITY_SHARE_RECIPIENT_ID, String.valueOf(shareItem.getRecipientId()));
	 	 			if (shareStartDate != null) share.addAttribute(ObjectKeys.XTAG_ENTITY_SHARE_START_DATE, sd.format(shareStartDate));
	 	 			if (expirationDate != null) share.addAttribute(ObjectKeys.XTAG_ENTITY_SHARE_EXPIRATION, sd.format(expirationDate));
	 	 			share.addAttribute(ObjectKeys.XTAG_ENTITY_SHARE_ROLE, shareItem.getRole().toString());
 				}
 			}
		}
		
		if (entry.getCreation() != null)
			entry.getCreation().addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (entry.getModification() != null)
			entry.getModification().addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		if (entry.getParentBinder() != null) {
			XmlUtils.addProperty(element, ObjectKeys.XTAG_ENTITY_PARENTBINDER, entry.getParentBinder().getId().toString());
		}
		if (entry.getEntryDefId() != null) {
			XmlUtils.addProperty(element, ObjectKeys.XTAG_ENTITY_DEFINITION, entry.getEntryDefId());
			String defName = DefinitionHelper.getItemProperty(entry.getEntryDefDoc().getRootElement(), "name");
			XmlUtils.addProperty(element, ObjectKeys.XTAG_ENTITY_DEFINITION_NAME, defName);
		}
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_ENTITY_ICONNAME, ObjectKeys.XTAG_TYPE_STRING, entry.getIconName());			
		//process all form items
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_ENTITY_TITLE, ObjectKeys.XTAG_TYPE_STRING, entry.getTitle());
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_ENTITY_DESCRIPTION, ObjectKeys.XTAG_TYPE_DESCRIPTION, entry.getDescription());
		Set<Map.Entry> mes = entry.getCustomAttributes().entrySet();
		for (Map.Entry me: mes) {
			CustomAttribute attr = (CustomAttribute)me.getValue();
			attr.addChangeLog(element);
		}
		if (entry instanceof WorkflowSupport) {
			WorkflowSupport wEntry = (WorkflowSupport)entry;
			Set<WorkflowState> states = wEntry.getWorkflowStates();
			for (WorkflowState s: states) s.addChangeLog(element);
			Set<WorkflowResponse> responses = wEntry.getWorkflowResponses();
			for (WorkflowResponse r: responses) r.addChangeLog(element);
			if (wEntry.getWorkflowChange() != null) wEntry.getWorkflowChange().addChangeLog(element, ObjectKeys.XTAG_WF_CHANGE);
		}
		
		//Just log top level attachments
 		Set<Attachment> atts = entry.getAttachments();
 		if (!atts.isEmpty()) {
 			Element attachments = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE_SET);
 			attachments.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, ObjectKeys.XTAG_ENTITY_ATTACHMENTS);
 			for (Attachment att: atts) {
 				if (att instanceof FileAttachment)
 					((FileAttachment)att).addChangeLog(attachments, false);
 				else
 					att.addChangeLog(attachments);
 			}
		}
		return element;
	}
	public static Element buildLog(ChangeLog changes, FileAttachment attachment) {
		Element element = buildLog(changes, attachment.getOwner().getEntity());
		attachment.addChangeLog(element);
		return element;
	}
	public static Element buildLog(ChangeLog changes, VersionAttachment attachment) {
		Element element = buildLog(changes, attachment.getOwner().getEntity());
		attachment.addChangeLog(element);
		return element;
	}

	public static ChangeLog create(DefinableEntity entity, String operation) {
		ChangeLog changes = new ChangeLog(entity, operation);
		changes.getEntityRoot(); // This causes some base data to be populated in the object
		return changes;
	}
	
	public static ChangeLog createAndBuild(DefinableEntity entity, String operation) {
		ChangeLog changes = create(entity, operation);
		buildLog(changes, entity);
		return changes;
	}
	
	public static ChangeLog createAndBuild(DefinableEntity entity, String operation, FileAttachment fa) {
		ChangeLog changes = create(entity, operation);
		buildLog(changes, fa);
		return changes;
	}
	
	public static ChangeLog createAndBuild(DefinableEntity entity, String operation, VersionAttachment va) {
		ChangeLog changes = create(entity, operation);
		buildLog(changes, va);
		return changes;
	}
	
	public static void save(ChangeLog changes) {
		if (getAdminModule().isChangeLogEnabled()) {
			getCoreDao().save(changes);
		}
	}
	
}
