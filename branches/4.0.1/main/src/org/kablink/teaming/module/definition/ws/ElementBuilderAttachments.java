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
package org.kablink.teaming.module.definition.ws;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileAttachment.FileLock;
import org.kablink.teaming.remoting.ws.model.AttachmentsField;
import org.kablink.teaming.remoting.ws.model.Timestamp;
import org.kablink.teaming.remoting.ws.model.AttachmentsField.Attachment;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;


public class ElementBuilderAttachments extends AbstractElementBuilder {
	protected boolean build(Element element, org.kablink.teaming.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity, String dataElemType, String dataElemName) {
		List<Attachment> attachments = null;
		if(entityModel != null)
			attachments = new ArrayList<Attachment>();
		for (FileAttachment att:entity.getFileAttachments()) {
			if (att != null && att.getFileItem() != null) {
				String webUrl = WebUrlUtil.getFileUrl((String)null, WebKeys.ACTION_READ_FILE, att); 
				if(element != null) {
					Element value = element.addElement("file");
					value.setText(att.getFileItem().getName());
					value.addAttribute("href", webUrl);
				}
				if(attachments != null) {
					FileLock fl = att.getFileLock();
					attachments.add(new Attachment(att.getId(), att.getFileItem().getName(),
							new Timestamp(Utils.redactUserPrincipalIfNecessary(att.getCreation().getPrincipal()).getName(), 
									att.getCreation().getPrincipal().getId(),
									att.getCreation().getDate()),
							new Timestamp(Utils.redactUserPrincipalIfNecessary(att.getModification().getPrincipal()).getName(), 
									att.getModification().getPrincipal().getId(),
									att.getModification().getDate()),
							att.getFileItem().getLength(), webUrl, att.getHighestVersionNumber(), att.getMajorVersion().intValue(), att.getMinorVersion().intValue(), 
							att.getFileItem().getDescription().getText(), att.getFileStatus().intValue(),
							(fl != null && fl.getOwner() != null)? fl.getOwner().getId():null, (fl!= null)? fl.getExpirationDate():null));
				}
				context.handleAttachment(att, webUrl);
			}
		}
		if(attachments != null)
			entityModel.setAttachmentsField(new AttachmentsField(dataElemName, dataElemType, attachments.toArray(new Attachment[]{})));
		return true;
	}
}
