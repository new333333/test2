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
package com.sitescape.team.module.shared;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.sitescape.team.ConfigurationException;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Statistics;
import com.sitescape.team.domain.UpdateAttributeSupport;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.ObjectPropertyNotFoundException;
import com.sitescape.team.util.SpringContextUtil;

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
							changed = cAttr.setValue(val);
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
	private static CoreDao getCore() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	public static void copyAttributes(DefinableEntity source, DefinableEntity destination) {
		CoreDao coreDao = getCore();
		//File attachments must already have been copied; need to do first so custom file attributes have a real object to reference
		Map catts = source.getCustomAttributes();
		for (Iterator iter=catts.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			CustomAttribute ca = (CustomAttribute)me.getValue();
			if (Statistics.ATTRIBUTE_NAME.equals(ca.getName())) continue;
			switch (ca.getValueType()) {
				case CustomAttribute.EVENT: {
					Event event = (Event)ca.getValue();
					if (event != null) {
						Event newE = (Event)event.clone();
						newE.setId(null);
						coreDao.save(newE);
						destination.addCustomAttribute(newE.getName(), newE);
					}
					break;
				}
				case CustomAttribute.ATTACHMENT: {
					//only support file attachments now
					FileAttachment sfa = (FileAttachment)ca.getValue();
					if (sfa == null) continue;
					FileAttachment dfa = destination.getFileAttachment(sfa.getFileItem().getName());
					//attach as custom attribute
					if (dfa != null) destination.addCustomAttribute(ca.getName(), dfa);
					break;
				}
				case CustomAttribute.ORDEREDSET:
				case CustomAttribute.SET: {
					//should be the same type
					Set values = ca.getValueSet();
					if (!values.isEmpty()) {
						Set newV;
						if (ca.getValueType() == CustomAttribute.ORDEREDSET) newV = new LinkedHashSet(); 
						else newV = new HashSet();
						for (Iterator it=values.iterator(); it.hasNext();) {
							Object val = it.next();
							if (val == null) continue;
							if (val instanceof Event) {
								Event newE = (Event)((Event)val).clone();
								newE.setId(null);
								coreDao.save(newE);
								newV.add(newE);
							} else if (val instanceof FileAttachment) {
								//only support file attachments now
								FileAttachment sfa = (FileAttachment)val;
								FileAttachment dfa = destination.getFileAttachment(sfa.getFileItem().getName());
								//attach as custom attribute
								if (dfa != null) newV.add(dfa);
								
							} else {
								newV.add(val);
							}
						}
						CustomAttribute v = destination.getCustomAttribute(ca.getName());
						//should exists, cause copy files would have added it.
						//but may be an issue if multiple fields point to same attachment
						if (v == null) destination.addCustomAttribute(ca.getName(), newV);
						else v.setValue(newV);
					}
					break;
				} 
				default: {
					destination.addCustomAttribute(ca.getName(), ca.getValue());
					break;
				}
			}
			
		}
		
	}
}


