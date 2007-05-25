/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.shared;

import java.util.Map;
import java.util.Set;
import java.util.Date;

import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.domain.WorkflowResponse;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.util.Validator;

public class ChangeLogUtils {

	public static Element buildLog(ChangeLog changes, DefinableEntity entry) {
		Element element = changes.getEntityRoot();
		if (entry instanceof Binder)
			EntityIndexUtils.addReadAccess(element, (Binder)entry);
		else
			EntityIndexUtils.addReadAccess(element, entry.getParentBinder(), entry);
		
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
		XmlUtils.addAttribute(element, ObjectKeys.XTAG_ENTITY_ICONNAME, ObjectKeys.XTAG_TYPE_STRING, entry.getIconName());			
		//process all form items
		XmlUtils.addAttributeCData(element, ObjectKeys.XTAG_ENTITY_TITLE, ObjectKeys.XTAG_TYPE_STRING, entry.getTitle());
		XmlUtils.addAttributeCData(element, ObjectKeys.XTAG_ENTITY_DESCRIPTION, ObjectKeys.XTAG_TYPE_DESCRIPTION, entry.getDescription());
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
