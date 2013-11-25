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
package org.kablink.teaming.module.definition.export;

import org.dom4j.Element;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;


public class ElementBuilderAttachments extends AbstractElementBuilder {
	protected boolean build(Element element, DefinableEntity entity, String dataElemType, String dataElemName) {
		
		BinderModule binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
		
		for (FileAttachment att:entity.getFileAttachments()) {
			if (att != null && att.getFileItem() != null) { 
				if(element != null) {
					Element value = element.addElement("file");
					value.setText(att.getFileItem().getName());
					
					String fileName = binderModule.filename8BitSingleByteOnly(att, 
							SPropsUtil.getBoolean("export.filename.8bitsinglebyte.only", true));
					value.addAttribute("href", fileName);
					
					value.addAttribute("numVersions", String.valueOf(att.getFileVersions().size()));
				}
			}
		}
		return true;
	}
}
