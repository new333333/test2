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
package org.kablink.teaming.module.definition;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.Element;
import org.dom4j.Document;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.util.DefaultMergeableXmlClassPathConfigFiles;


public class DefinitionConfigurationBuilder extends
		DefaultMergeableXmlClassPathConfigFiles {
	
	private Map<String, Map> jspCache = new HashMap<String, Map>();
	private Map<String, Map> gwtFolderViewCache = new HashMap<String, Map>();
	private Map<String, Element> itemCache = new HashMap<String, Element>();
	
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        //TODO: add any caching we want
        // (rsordillo) adding '_cache' to store jsp page references in Definitions. This should help speed up
        // performance when rendering a Definition
        if (jspCache.isEmpty()) {
        	loadItems();
        }

    }

    private void loadItems()
    	throws Exception
    {
    	Iterator itItems = getAsMergedDom4jDocument().getRootElement().selectNodes("//item").listIterator();
		
    	while (itItems.hasNext()) {
			Element nextItem = (Element) itItems.next();
			
			String nameValue = nextItem.attributeValue("name");
			if (nameValue == null)
				continue;
			String licenseValue = nextItem.attributeValue("feature");
			if (licenseValue != null && !LicenseChecker.isAuthorizedByLicense(licenseValue)) continue;
			itemCache.put(nameValue, nextItem);
			Iterator itJsps = nextItem.selectNodes("jsps/jsp").listIterator();
			
			Map jspsObj = new HashMap();
			while (itJsps.hasNext()) {
				Element nextJsp = (Element) itJsps.next();
				jspsObj.put(nextJsp.attributeValue("name"), nextJsp.attributeValue("value"));
			}
			jspCache.put(nameValue, jspsObj);

			Iterator itFolderViews = nextItem.selectNodes("folderViews/folderView").listIterator();

			Map folderViewsObj = new HashMap();
			while (itFolderViews.hasNext()) {
				Element nextFolderView = (Element) itFolderViews.next();
				folderViewsObj.put(nextFolderView.attributeValue("name"), nextFolderView.attributeValue("value"));
			}
			gwtFolderViewCache.put(nameValue, folderViewsObj);
		}
    }
    
    public String getItemJspByStyle(Element item, String name, String style)
    {
    	//should probably check some version
      	    	
       		Map jspsObj = jspCache.get(name);
       		if (jspsObj != null) {
       			String jsp = (String)jspsObj.get(style);
       			if (jsp != null) return jsp;
       			return (String)jspsObj.get(Definition.JSP_STYLE_DEFAULT);
       		}
    	
       		return null;
       	
    }

    public String getItemGwtFolderViewByStyle(Element item, String name, String style)
    {
    	//should probably check some version

       		Map folderViewsObj = gwtFolderViewCache.get(name);
       		if (folderViewsObj != null) {
       			String folderView = (String)folderViewsObj.get(style);
       			if (folderView != null) return folderView;
       			return (String)folderViewsObj.get(Definition.JSP_STYLE_DEFAULT);
       		}

       		return null;

    }

    public Element getItem(Document config, String item) {
       	//should probably check some version
    	return itemCache.get(item);
    	//return (Element)config.getRootElement().selectSingleNode("item[@name='"+item+"']");
    }
    

}
