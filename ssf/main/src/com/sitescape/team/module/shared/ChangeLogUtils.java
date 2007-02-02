package com.sitescape.team.module.shared;

import java.util.Map;
import java.util.Set;
import java.util.Date;

import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Attachment;
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
		if (entry.getCreation() != null)
			entry.getCreation().addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (entry.getModification() != null)
			entry.getModification().addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		if (entry.getParentBinder() != null) {
			addLogProperty(element, ObjectKeys.XTAG_ENTITY_PARENTBINDER, entry.getParentBinder().getId().toString());
		}
		Definition def = entry.getEntryDef();
		if (def != null) {
			addLogProperty(element, ObjectKeys.XTAG_ENTITY_DEFINITION, def.getId());
		}
		if (!Validator.isNull(entry.getIconName())) {
			addLogAttribute(element, ObjectKeys.XTAG_ENTITY_ICONNAME, ObjectKeys.XTAG_TYPE_STRING, entry.getIconName());			
		}
		//process all form items
		addLogAttribute(element, ObjectKeys.XTAG_ENTITY_TITLE, "string", entry.getTitle());
		addLogAttributeCData(element, ObjectKeys.XTAG_ENTITY_DESCRIPTION, "description", entry.getDescription());
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
 			Element attachments = element.addElement(ObjectKeys.XTAG_ATTRIBUTE_SET);
 			attachments.addAttribute(ObjectKeys.XTAG_NAME, ObjectKeys.XTAG_FIELD_ENTITY_ATTACHMENTS);
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
	public static Element addLogProperty(Element parent, String name, String value) {
		Element prop = parent.addElement("property");
		prop.addAttribute(ObjectKeys.XTAG_NAME, name);
		if (!Validator.isNull(value)) prop.addText(value);
		return prop;
	}
	//force comman date format.  This is a problem cause hibernate returns sql Timestamps which format
	//differently then java.util.date
	public static Element addLogProperty(Element parent, String name, Date value) {
		if (value != null) return addLogProperty(parent, name, value.toGMTString());
		return addLogProperty(parent, name, (Object)null);
	}
	public static Element addLogProperty(Element parent, String name, Object value) {
		Element prop = parent.addElement("property");
		prop.addAttribute(ObjectKeys.XTAG_NAME, name);
		if (value != null) prop.addText(value.toString());
		return prop;
	}
	//attributes are available through the definintion builder
	public static Element addLogAttribute(Element parent, String name, String type, String value) {
		if (Validator.isNull(value)) return null;
		Element prop = parent.addElement("attribute");
		prop.addAttribute(ObjectKeys.XTAG_NAME, name);
		prop.addAttribute(ObjectKeys.XTAG_TYPE, type);
		prop.addText(value);
		return prop;
	}
	//attributes are available through the definintion builder
	public static Element addLogAttributeCData(Element parent, String name, String type, String value) {
		if (Validator.isNull(value)) return null;
		Element prop = parent.addElement("attribute");
		prop.addAttribute("name", name);
		prop.addAttribute("type", type);
		prop.addCDATA(value);
		return prop;
	}
	public static Element addLogAttributeCData(Element parent, String name, String type, Object value) {
		if (value == null) return null;
		Element prop = parent.addElement("attribute");
		prop.addAttribute("name", name);
		prop.addAttribute("type", type);
		prop.addCDATA(value.toString());
		return prop;
	}
}
