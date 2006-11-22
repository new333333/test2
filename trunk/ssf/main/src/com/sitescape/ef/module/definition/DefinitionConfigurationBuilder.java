package com.sitescape.ef.module.definition;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.Element;

import com.sitescape.ef.util.DefaultMergeableXmlClassPathConfigFiles;

public class DefinitionConfigurationBuilder extends
		DefaultMergeableXmlClassPathConfigFiles {
	
	private Map<String, ItemJsps> _cache = new HashMap<String, ItemJsps>();
	
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
    	ItemJsps jspsObj = null;
    	Iterator itItems = getAsMergedDom4jDocument().getRootElement().selectNodes("//item").listIterator();
		
    	while (itItems.hasNext()) {
			Element nextItem = (Element) itItems.next();
			
			String nameValue = nextItem.attributeValue("name");
			if (nameValue == null)
				continue;
			
			Iterator itJsps = nextItem.selectNodes("jsps/jsp").listIterator();
			
			jspsObj = new ItemJsps();
			while (itJsps.hasNext()) {
				Element nextJsp = (Element) itJsps.next();

				String jspNameValue = nextJsp.attributeValue("name");
				if (jspNameValue.equals("view"))
					jspsObj.setView(nextJsp.attributeValue("value"));
				else
				if (jspNameValue.equals("form"))
					jspsObj.setForm(nextJsp.attributeValue("value"));
			}
			_cache.put(nameValue, jspsObj);
		}
    }
    
    public String getItemJspByStyle(String item, String style)
    {
    	ItemJsps jspsObj = null;
    	
    	jspsObj = _cache.get(item);
    	if (jspsObj != null)
    	{
    		if (style.equals("view"))
    			return jspsObj.getView();
    		else
    		if (style.equals("form"))
        		return jspsObj.getForm();
    	}
    	
    	return "";
    }
    
    /**
     * Represent information held within <jsps> entry that is part of an <item> tag in the
     * definition_builder_config.xml file
     * 
     * @author Rob
     *
     */
    class ItemJsps
    {
    	String _view = "",
    		   _form = "";
    	
    	public String getView()
    	{
    		return _view;
    	}
    	
    	public String getForm()
    	{
    		return _form;
    	}
    	
    	public void setView(String view_in)
    		throws Exception
    	{
    		_view = view_in;
    	}
    	
    	public void setForm(String form_in)
    		throws Exception
    	{
    		_form = form_in;
    	}
    }
}
