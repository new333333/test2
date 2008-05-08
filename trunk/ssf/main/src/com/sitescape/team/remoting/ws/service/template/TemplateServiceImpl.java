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
package com.sitescape.team.remoting.ws.service.template;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.model.TemplateBrief;
import com.sitescape.team.remoting.ws.model.TemplateCollection;

public class TemplateServiceImpl extends BaseService implements TemplateService {

	public long template_addBinder(String accessToken, long parentBinderId, long binderConfigId, String title)
	{
		try {
			return getTemplateModule().addBinder(binderConfigId, parentBinderId, title, null);
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
			defElement.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, def.getDefinitionType().toString());
			XmlUtils.addCustomAttribute(defElement, ObjectKeys.XTAG_TEMPLATE_TITLE, ObjectKeys.XTAG_TYPE_STRING, def.getTemplateTitle());
			
		}
		return root.asXML();
	}
	
	public TemplateCollection template_getTemplates(String accessToken) {
		List<TemplateBinder> defs = getTemplateModule().getTemplates();

		List<TemplateBrief> list = new ArrayList<TemplateBrief>();
		for (TemplateBinder def:defs) {
			list.add(new TemplateBrief(def.getId(), def.getInternalId(), def.getDefinitionType(), def.getName(), def.getTemplateTitle()));	
		}

		TemplateBrief[] array = new TemplateBrief[list.size()];
		return new TemplateCollection(list.toArray(array));
	}

}
