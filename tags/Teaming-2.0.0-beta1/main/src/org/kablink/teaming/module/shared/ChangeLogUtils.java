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
package org.kablink.teaming.module.shared;

import java.util.Map;
import java.util.Set;
import java.util.Date;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.util.Validator;


public class ChangeLogUtils {

	public static Element buildLog(ChangeLog changes, DefinableEntity entry) {
		Element element = changes.getEntityRoot();
		if (entry instanceof Binder)
			EntityIndexUtils.addReadAccess(element, (Binder)entry, true);
		else
			EntityIndexUtils.addReadAccess(element, entry.getParentBinder(), entry, true);
		
		if (entry.getCreation() != null)
			entry.getCreation().addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (entry.getModification() != null)
			entry.getModification().addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		if (entry.getParentBinder() != null) {
			XmlUtils.addProperty(element, ObjectKeys.XTAG_ENTITY_PARENTBINDER, entry.getParentBinder().getId().toString());
		}
		Definition def = entry.getEntryDef();
		if (def != null) {
			XmlUtils.addProperty(element, ObjectKeys.XTAG_ENTITY_DEFINITION, def.getId());
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
		Element element = changes.getEntityRoot();
		attachment.addChangeLog(element);
		return element;
	}
	public static Element buildLog(ChangeLog changes, VersionAttachment attachment) {
		Element element = changes.getEntityRoot();
		attachment.addChangeLog(element);
		return element;
	}

}
