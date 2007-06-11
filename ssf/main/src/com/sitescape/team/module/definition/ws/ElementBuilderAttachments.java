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
package com.sitescape.team.module.definition.ws;

import java.util.Collection;

import org.dom4j.Element;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FolderEntry;

/**
* Handle unnamed attachments in mail notification.  This implememtation will
* send the file name only in a notification for both summary and full types. 
* See <code>NotifyBuilderAttachmentsSend</code>to send the actual file.
* @author Janet McCann
*/
public class ElementBuilderAttachments extends AbstractElementBuilderFile {
	protected boolean build(Element element, DefinableEntity entity, String dataElemName) {
		if (entity instanceof FolderEntry) {
			FolderEntry fEntry = (FolderEntry)entity;
			Collection atts = entity.getFileAttachments();
			generateValues(atts, element, fEntry, "file");
		}
		return true;
	}
}
