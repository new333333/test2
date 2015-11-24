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
package org.kablink.teaming.web.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.util.StringUtil;

/**
 * A UserAppConfig object contains an unordered list of file extension
 * application pairs.
 * 
 * @author drfoster
 */
public class UserAppConfig {
	private Document m_appConfig = null;
	
	public UserAppConfig() {
	}
	
	public UserAppConfig(Document appConfigDoc) {
		this.m_appConfig = appConfigDoc;
	}
	
	public UserAppConfig(String xmlEncoding) {
		if (xmlEncoding == null) return;
		try {
			m_appConfig = XmlUtil.parseText(xmlEncoding);
		} catch (Exception ex) {};
	}

	public String toString() {
		return getUserAppConfig().asXML();
	}
	
	@SuppressWarnings("unchecked")
	public Document addAppConfig(String extension, String application) {
		getUserAppConfig();
		Element root = this.m_appConfig.getRootElement();
		List list = root.elements();
		if (!extension.startsWith(".")) {
			extension = ("." + extension);
		}
		for(int i = 0; i < list.size(); i++) {
			String	ext = ((Element)list.get(i)).attributeValue("extension");
			if ((ext != null) && ext.equalsIgnoreCase(extension)) {
				return this.m_appConfig;
			}
		}
		
		Element newAppConfig;
		newAppConfig = root.addElement("app-config");
		newAppConfig.addAttribute("extension", extension);
		newAppConfig.addAttribute("application", application);
		
		return this.m_appConfig;
	}

	public static UserAppConfig createFromBrowserData(String appConfigS) {
		Element			root;
		int				count;
		int				i;
		String[]		appConfigs;
		UserAppConfig	uacReply;
		
		
		appConfigs = StringUtil.split(appConfigS, "]-$-[");
		count      = ((null == appConfigs) ? 0 : appConfigs.length);

		uacReply = new UserAppConfig();
		uacReply.m_appConfig = createUserAppConfigRootDocument();
    	root = uacReply.m_appConfig.getRootElement();
    	for (i = 0; i < count; i += 2) {
    		Element	appConfigElement = root.addElement("app-config");
    		appConfigElement.addAttribute("extension",   appConfigs[i]);
    		appConfigElement.addAttribute("application", appConfigs[i + 1]);
    	}
		return uacReply;
	}
	
	public Document deleteAppConfig(String extension) {
		getUserAppConfig();
		Element root = this.m_appConfig.getRootElement();
		Element appConfig = (Element)root.selectSingleNode("//app-config[@extension='"+extension+"']");
		if (appConfig != null) {
			appConfig.getParent().remove(appConfig);
		}
		return this.m_appConfig;
	}

	public Document getUserAppConfig() {
		if (this.m_appConfig == null) {
			this.m_appConfig = createUserAppConfigRootDocument();
		}
		return this.m_appConfig; 	//this.favorites.asXML();
	}
	
	private static Document createUserAppConfigRootDocument() {
		Document doc = DocumentHelper.createDocument();
		doc.addElement("app-configs");
		return doc;
	}
	
	public JSONArray getUserAppConfigJson() {
		getUserAppConfig();
		JSONArray uacData = new JSONArray();
    	Element appConfigsRoot = this.m_appConfig.getRootElement();
    	buildUserAppConfigJson(appConfigsRoot, uacData);
    	return uacData;
	}
	
	@SuppressWarnings("unchecked")
	private void buildUserAppConfigJson(Element appConfigsElement, JSONArray uacData) {
       	Iterator itAppConfigs = appConfigsElement.selectNodes("app-configs|app-config").iterator();
       	int	count = 0;
       	while (itAppConfigs.hasNext()) {
       		Element e = (Element) itAppConfigs.next();

       		Map map = new HashMap();
       		map.put("extension", e.attributeValue("extension"));
       		map.put("application", e.attributeValue("application"));
       		uacData.put(map);
       		count += 1;
    	}
	}
}
