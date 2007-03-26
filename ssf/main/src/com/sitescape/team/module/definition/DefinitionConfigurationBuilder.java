package com.sitescape.team.module.definition;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.Element;
import org.dom4j.Document;

import com.sitescape.team.util.DefaultMergeableXmlClassPathConfigFiles;

public class DefinitionConfigurationBuilder extends
		DefaultMergeableXmlClassPathConfigFiles {
	
	private Map<String, Map> jspCache = new HashMap<String, Map>();
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
			itemCache.put(nameValue, nextItem);
			Iterator itJsps = nextItem.selectNodes("jsps/jsp").listIterator();
			
			Map jspsObj = new HashMap();
			while (itJsps.hasNext()) {
				Element nextJsp = (Element) itJsps.next();
				jspsObj.put(nextJsp.attributeValue("name"), nextJsp.attributeValue("value"));
			}
			jspCache.put(nameValue, jspsObj);
		}
    }
    
    public String getItemJspByStyle(Element item, String name, String style)
    {
    	//should probably check some version
      	    	
       		Map jspsObj = jspCache.get(name);
       		if (jspsObj != null) {
       			return (String)jspsObj.get(style);
       		}
    	
       		return null;

//       	Element jsp = (Element)item.selectSingleNode("jsps/jsp[@name='" + style + "']");
//       	if (jsp == null) return null;
//       	return jsp.attributeValue("value");
       	
    }
    public Element getItem(Document config, String item) {
       	//should probably check some version
    	return itemCache.get(item);
    	//return (Element)config.getRootElement().selectSingleNode("item[@name='"+item+"']");
    }
    

}
