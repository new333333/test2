package com.sitescape.ef.module.shared;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;

import com.sitescape.ef.domain.UpdateAttributeSupport;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.InvokeUtil;
import com.sitescape.ef.util.ObjectPropertyNotFoundException;
import com.sitescape.ef.ConfigurationException;

/**
 * @author hurley
 *
 */
public class EntryBuilder {
	public static List buildEntries(Class clazz, Collection data) {
		try {
			List results = new ArrayList();
	    	for (Iterator iter=data.iterator();iter.hasNext();) {
				Entry target = (Entry)clazz.newInstance();
	     		EntryBuilder.buildEntry(target,(Map)iter.next());
	     		results.add(target);
	    	}
	    	return results;
		} catch (InstantiationException e) {
			throw new ConfigurationException(
                "Cannot instantiate entry of type '"
                        + clazz.getName() + "'");
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(
					"Cannot instantiate entry of type '"
                        	+ clazz.getName() + "'");
		}
		
	}
	public static void buildEntry(Entry target, Map data) {
		Set kvps = data.entrySet();
		Map.Entry entry;
		for (Iterator iter=kvps.iterator(); iter.hasNext();) {
			entry = (Map.Entry)iter.next();
			String attr = (String)entry.getKey();
			Object val = entry.getValue();
			try {
				InvokeUtil.invokeSetter(target, attr, val);
			} catch (ObjectPropertyNotFoundException pe) {
				if (val == null) continue;
				target.addCustomAttribute(attr, val);
			}				
		}

	}
	/**
	 * Apply different updates to a collection of entries
	 * @param entries - collection of <code>Entry</code>
	 * @param data - a map index by the entry id, whose values are a map of updates
	 */
	public static void updateEntries(Collection entries, Map data) {
    	for (Iterator iter=entries.iterator();iter.hasNext();) {
     		Entry target = (Entry)iter.next();
    		updateEntry(target,(Map)data.get(target.getId()));
    	}

	}
	/**
	 * Apply the same updates to a collection of entries
	 * @param entries - collection of <code>Entry</code>
	 * @param update - a map indexed by attribute name.  Apply same update to all entries
	 */
	public static void applyUpdate(Collection entries, Map data) {
	   	for (Iterator iter=entries.iterator();iter.hasNext();) {
     		Entry target = (Entry)iter.next();
    		updateEntry(target,data);
    	}		
	}

	public static void updateEntry(Entry target, Map data) {
		Set kvps = data.entrySet();
		Map.Entry entry;
		for (Iterator iter=kvps.iterator(); iter.hasNext();) {
			entry = (Map.Entry)iter.next();
			String attr = (String)entry.getKey();
			Object val = entry.getValue();
			try {
				InvokeUtil.invokeSetter(target, attr, val);
			} catch (ObjectPropertyNotFoundException pe) {
				if (val == null) 
					target.removeCustomAttribute(attr);
				else {
					CustomAttribute cAttr = target.getCustomAttribute(attr);
					if (cAttr != null) {
						Object oldVal = cAttr.getValue();
						if (oldVal instanceof UpdateAttributeSupport) {
							try {
								((UpdateAttributeSupport)oldVal).update(val);
							} catch (ClassCastException ce) {
								cAttr.setValue(val);
							}
						} else
							cAttr.setValue(val);
					} else {
						target.addCustomAttribute(attr, val);					
					}
				}
			}				
		}

	}	
    public static void writeFiles(FileModule fileModule, Binder binder, Entry entry, List fileData)
    			throws WriteFilesException {
    	WriteFilesException wfe = new WriteFilesException();
	
    	for(int i = 0; i < fileData.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileData.get(i);
    		try {
    			fileModule.writeFile(binder, entry, fui);
    		} catch (Exception e) {
    			wfe.addException(e);
    		}
    		//See if this file wants a scaled thumbnail
    		if (fui.getMaxHeight() != null && fui.getMaxWidth() != null) {
    			//Create the scaled file
    			//??? TODO code needed
    		}
    	}
	
    	if(wfe.size() > 0) {
    		//At least one file failed to be written successfully.
    		wfe.setErrorArgs(entry, fileData.size(), wfe.size());
    		throw wfe;
    	}
    }	
}


