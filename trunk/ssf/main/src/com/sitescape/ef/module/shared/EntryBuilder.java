package com.sitescape.ef.module.shared;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.DefinableEntity;

import com.sitescape.ef.domain.UpdateAttributeSupport;
import com.sitescape.ef.util.InvokeUtil;
import com.sitescape.ef.util.ObjectPropertyNotFoundException;
import com.sitescape.ef.ConfigurationException;

/**
 * @author hurley
 *
 */
public class EntryBuilder {
	
	private final static Log logger = LogFactory.getLog(EntryBuilder.class);
	
	public static List buildEntries(Class clazz, Collection data) {
		try {
			List results = new ArrayList();
	    	for (Iterator iter=data.iterator();iter.hasNext();) {
	    		DefinableEntity target = (DefinableEntity)clazz.newInstance();
	     		EntryBuilder.buildEntry(target,(Map)iter.next());
	     		results.add(target);
	    	}
	    	return results;
		} catch (InstantiationException e) {
			throw new ConfigurationException(
                "Cannot instantiate entity of type '"
                        + clazz.getName() + "'");
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(
					"Cannot instantiate entity of type '"
                        	+ clazz.getName() + "'");
		}
		
	}
	public static void buildEntry(DefinableEntity target, Map data) {
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
	 * Apply the same updates to a collection of entries
	 * @param entries - collection of <code>Entry</code>
	 * @param update - a map indexed by attribute name.  Apply same update to all entries
	 */
	public static void applyUpdate(Collection entries, Map data) {
	   	for (Iterator iter=entries.iterator();iter.hasNext();) {
	   		DefinableEntity target = (DefinableEntity)iter.next();
    		updateEntry(target,data);
    	}		
	}

	public static boolean updateEntry(DefinableEntity target, Map data) {
		Set kvps = data.entrySet();
		Map.Entry entry;
		boolean changed=false;
		for (Iterator iter=kvps.iterator(); iter.hasNext();) {
			entry = (Map.Entry)iter.next();
			String attr = (String)entry.getKey();
			Object val = entry.getValue();
			try {
				Object currentVal = InvokeUtil.invokeGetter(target, attr);
				if (currentVal != null) {
					if (!currentVal.equals(val)) {
						changed=true;
						InvokeUtil.invokeSetter(target, attr, val);
					}
				} else if (val != null) {
					changed=true;
					InvokeUtil.invokeSetter(target, attr, val);
				}
			} catch (ObjectPropertyNotFoundException pe) {
				if (val == null) { 
					if (target.getCustomAttribute(attr) != null) {
						changed=true;
						target.removeCustomAttribute(attr);
					}
				
				} else {
					CustomAttribute cAttr = target.getCustomAttribute(attr);
					if (cAttr != null) {
						Object oldVal = cAttr.getValue();
						if (oldVal instanceof UpdateAttributeSupport) {
							try {
								if (((UpdateAttributeSupport)oldVal).update(val)) {
									changed=true;
								}
							} catch (ClassCastException ce) {
								cAttr.setValue(val);
								changed=true;
							}
						} else {
							if (!val.equals(oldVal)) {
								cAttr.setValue(val);
								changed=true;
							}
						}
					} else {
						changed=true;
						target.addCustomAttribute(attr, val);					
					}					
				}
			}				
		}
		return changed;

	}	

}


