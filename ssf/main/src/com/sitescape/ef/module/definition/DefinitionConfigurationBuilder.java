package com.sitescape.ef.module.definition;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.Element;

import com.sitescape.team.util.DefaultMergeableXmlClassPathConfigFiles;

public class DefinitionConfigurationBuilder extends
		DefaultMergeableXmlClassPathConfigFiles {
	
	private Map<String, Map> _cache = new HashMap<String, Map>();
	
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        //TODO: add any caching we want
        // (rsordillo) adding '_cache' to store jsp page references in Definitions. This should help speed up
        // performance when rendering a Definition
        if (_cache.isEmpty())
        	loadItemJspCache();
        
    }

    private void loadItemJspCache()
    	throws Exception
    {
    	Iterator itItems = getAsMergedDom4jDocument().getRootElement().selectNodes("//item").listIterator();
		
    	while (itItems.hasNext()) {
			Element nextItem = (Element) itItems.next();
			
			String nameValue = nextItem.attributeValue("name");
			if (nameValue == null)
				continue;
			
			Iterator itJsps = nextItem.selectNodes("jsps/jsp").listIterator();
			
			Map jspsObj = new HashMap();
			while (itJsps.hasNext()) {
				Element nextJsp = (Element) itJsps.next();
				jspsObj.put(nextJsp.attributeValue("name"), nextJsp.attributeValue("value"));
			}
			_cache.put(nameValue, jspsObj);
		}
    }
    
    public String getItemJspByStyle(String item, String style)
    {
    	
    	Map jspsObj = _cache.get(item);
    	if (jspsObj != null)
    	{
         	return (String)jspsObj.get(style);
    	}
    	
    	return null;
    }
    

}
