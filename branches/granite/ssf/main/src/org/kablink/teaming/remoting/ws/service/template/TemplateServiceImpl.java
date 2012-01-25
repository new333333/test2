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
package org.kablink.teaming.remoting.ws.service.template;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.remoting.ws.model.TemplateBrief;
import org.kablink.teaming.remoting.ws.model.TemplateCollection;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;


public class TemplateServiceImpl extends BaseService implements TemplateService, TemplateServiceInternal {

	public long template_addBinder(String accessToken, long parentBinderId, long binderConfigId, String title)
	{
		title = StringCheckUtil.check(title);
		try {
			return getTemplateModule().addBinder(binderConfigId, parentBinderId, title, null).getId().longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	public String template_getTemplatesAsXML(String accessToken) {
		List<TemplateBinder> defs = getTemplateModule().getTemplates();
		Document doc = DocumentHelper.createDocument();
		doc.addElement("templates");
		Element root = doc.getRootElement();
		for (TemplateBinder def:defs) {
			Element defElement = root.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, def.getName());
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, def.getInternalId());
			defElement.addAttribute("id", def.getId().toString());
		    Integer defType = def.getDefinitionType();
		    if (defType == null) defType = 0;
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, defType.toString());
			XmlUtils.addCustomAttribute(defElement, ObjectKeys.XTAG_TEMPLATE_TITLE, ObjectKeys.XTAG_TYPE_STRING, def.getTemplateTitle());
			
		}
		return root.asXML();
	}
	
	public TemplateCollection template_getTemplates(String accessToken) {
		List<TemplateBinder> defs = getTemplateModule().getTemplates();

		List<TemplateBrief> list = new ArrayList<TemplateBrief>();
		for (TemplateBinder def:defs) {
			String family = DefinitionUtils.getFamily(def.getEntryDefDoc());
		    Integer defType = def.getDefinitionType();
		    if (defType == null) defType = 0;
			list.add(new TemplateBrief(def.getId(), def.getInternalId(), family, defType, def.getName(), def.getTemplateTitle()));	
		}

		TemplateBrief[] array = new TemplateBrief[list.size()];
		return new TemplateCollection(list.toArray(array));
	}

}
