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
package com.sitescape.team.module.definition.notify;

import java.util.Collection;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.definition.DefinitionUtils;

/**
* Handle unnamed attachments in mail notification.  This implememtation will
* send the file name only in a notification for both summary and full types. 
* See <code>NotifyBuilderAttachmentsSend</code>to send the actual file.
* @author Janet McCann
*/
public class NotifyBuilderAttachments extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, DefinableEntity entity, String dataElemName, Map args) {
	    	Collection<FileAttachment> atts = entity.getFileAttachments();
	    	for (FileAttachment att : atts) {
		    	Element value = element.addElement("file");		    		
		    	if (att != null && att.getFileItem() != null) {
		    		value.setText(att.getFileItem().getName());
		    		if (notifyDef.isAttachmentsIncluded())	
		    			notifyDef.addAttachment(att);
		    		else if (entity instanceof FolderEntry) {
		    			FolderEntry fEntry = (FolderEntry)entity;
		    			String webUrl = DefinitionUtils.getViewPermalinkURL(fEntry, att); 
		    			value.addAttribute("href", webUrl);
		    		}
		    	}
   		}
	    	return true;
	   }
}
