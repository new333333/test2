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
package org.kablink.teaming.module.dashboard.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Dashboard;
import org.kablink.teaming.domain.DashboardPortlet;
import org.kablink.teaming.domain.EntityDashboard;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserDashboard;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;
import org.kablink.util.Validator;

/**
 * This module gives us the transaction semantics to deal with the dashboard.  The dashboard
 * became a domain object because we created a dashboard portlet and could no longer
 * use folder properties to store this.
 * @author Janet
 *
 */
public class DashboardModuleImpl extends CommonDependencyInjection implements DashboardModule {
	protected BinderModule binderModule;
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	public Dashboard getDashboard(String id) {
		return getCoreDao().loadDashboard(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	public EntityDashboard getEntityDashboard(EntityIdentifier ownerId) {
		return getCoreDao().loadEntityDashboard(ownerId);
	}
    public EntityDashboard createEntityDashboard(EntityIdentifier ownerId, Element config) {
       	EntityDashboard d = new EntityDashboard(ownerId, config);
        User user = RequestContextHolder.getRequestContext().getUser();
        d.setCreation(new HistoryStamp(user));
        d.setModification(d.getCreation());
        getCoreDao().save(d);
        return d;
    }
    public EntityDashboard createEntityDashboard(EntityIdentifier ownerId, Map properties) {
    	EntityDashboard d = new EntityDashboard(ownerId);
        User user = RequestContextHolder.getRequestContext().getUser();
        d.setCreation(new HistoryStamp(user));
        d.setModification(d.getCreation());
        d.setProperties(properties);
        getCoreDao().save(d);
        return d;
    }
	public UserDashboard getUserDashboard(EntityIdentifier ownerId, Long binderId) {
		return getCoreDao().loadUserDashboard(ownerId, binderId);
	}
	public UserDashboard createUserDashboard(EntityIdentifier ownerId, Long binderId, Map properties) {
		UserDashboard d = new UserDashboard(ownerId, binderId);
	    User user = RequestContextHolder.getRequestContext().getUser();
	    d.setCreation(new HistoryStamp(user));
	    d.setModification(d.getCreation());
        d.setProperties(properties);
	    getCoreDao().save(d);
	    return d;
	}
    public DashboardPortlet createDashboardPortlet(String portletName, Map properties) {
    	DashboardPortlet d = new DashboardPortlet(portletName);
        User user = RequestContextHolder.getRequestContext().getUser();
        d.setCreation(new HistoryStamp(user));
        d.setModification(d.getCreation());
        d.setProperties(properties);
        getCoreDao().save(d);
        return d;
    }
    public void modifyDashboard(String id, Map inputData) {
    	Dashboard d = getCoreDao().loadDashboard(id, RequestContextHolder.getRequestContext().getZoneId());
    	checkAccess(d);
    	for (Iterator iter=inputData.entrySet().iterator(); iter.hasNext();) {
    		Map.Entry me = (Map.Entry)iter.next();
			try {
				InvokeUtil.invokeSetter(d, (String)me.getKey(), me.getValue());
			} catch (ObjectPropertyNotFoundException pe) {
				//treat as a property
		   		d.setProperty((String)me.getKey(), me.getValue());
			}
   	}
    }
    public void setProperty(String id, String key, Object value) {
    	Dashboard d = getCoreDao().loadDashboard(id, RequestContextHolder.getRequestContext().getZoneId());
    	checkAccess(d);
   		d.setProperty(key, value);
   	   	
    }
    public String addComponent(String id, String scope, String listName, Map component) {
    	Dashboard d = getCoreDao().loadDashboard(id, RequestContextHolder.getRequestContext().getZoneId());
    	checkAccess(d);
		
    	Map components = (Map) d.getProperty(Dashboard.COMPONENTS);
    	if (components == null) {
    		components = new HashMap();
    		d.setProperty(Dashboard.COMPONENTS, components);
    	}
		int nextComponent = d.getNextComponentId();
		String cId = scope + "_" + String.valueOf(nextComponent);
		components.put(cId, component);
		d.setNextComponentId(++nextComponent);
		
		//Add this new component to the list
		Map componentListItem = new HashMap();
		componentListItem.put(Dashboard.ID, cId);
		componentListItem.put(Dashboard.SCOPE, scope);
		componentListItem.put(Dashboard.VISIBLE, new Boolean(true));
		List componentList = (List) d.getProperty(listName);
		if (componentList == null) {
			componentList = new ArrayList();
			d.setProperty(listName, componentList);
		}
		componentList.add(0, componentListItem);
		return cId;
    }
    public void modifyComponent(String id, String componentId, Map component) {
    	Dashboard d = getCoreDao().loadDashboard(id, RequestContextHolder.getRequestContext().getZoneId());
    	checkAccess(d);
		
    	Map components = (Map) d.getProperty(Dashboard.COMPONENTS);
    	if (components == null) {
    		components = new HashMap();
    		d.setProperty(Dashboard.COMPONENTS, components);
    	}
    	components.put(componentId, component);
    }
    
    public void modifyComponentOnlyIfPermitted(String id, String componentId, Map component) {
    	RequestContext rc = RequestContextHolder.getRequestContext();
    	Dashboard d = getCoreDao().loadDashboard(id, rc.getZoneId());
    	try {
    		checkAccess(d);
    	}
    	catch(AccessControlException e) {
			// Bugzilla 506743
			// The user executing this code doesn't have the right to update it. 
			// In this case, instead of aborting the entire request, return normally. 
			// The component will be fixed up later when someone else with appropriate
			// right executes this same code.
    		// Bugzilla 510406
    		// Return normally from here so that this soft and legitimate failure 
    		// wouldn't cause the entire Hibernate session to be cleared for this request.
    		if(logger.isDebugEnabled())
    			logger.debug("The caller " + rc.toString() + " does not have the right to modify the component " + componentId + " on the dashboard " + id);
    		return;
    	}
		
    	Map components = (Map) d.getProperty(Dashboard.COMPONENTS);
    	if (components == null) {
    		components = new HashMap();
    		d.setProperty(Dashboard.COMPONENTS, components);
    	}
    	components.put(componentId, component);	
    }
    
    public void deleteComponent(String id, String listName, String componentId) {
    	Dashboard d = getCoreDao().loadDashboard(id, RequestContextHolder.getRequestContext().getZoneId());
    	checkAccess(d);
		
    	if (Validator.isNotNull(listName)) {
			List dashboardList = (List) d.getProperty(listName);
			for (int i = 0; i < dashboardList.size(); i++) {
				Map component = (Map) dashboardList.get(i);
				String cId = (String) component.get(Dashboard.ID);
				if (cId.equals(componentId)) {
					//We have found the component to be deleted
					dashboardList.remove(i);
				}
			}
		}
		//Delete the component itself
		Map components = (Map) d.getProperty(Dashboard.COMPONENTS);
		if (components != null && components.containsKey(componentId)) {
			components.remove(componentId);
		}
  	
    }
    
    private void checkAccess(Dashboard d)  {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	if (!(d instanceof EntityDashboard)) return;
    	//this is either a binder or user entity dashoard
    	EntityDashboard e = (EntityDashboard)d;
    	EntityIdentifier id = e.getOwnerIdentifier();
    	//only user can modify 
    	if (EntityIdentifier.EntityType.user.equals(id.getEntityType())) {
    		if (user.getId().equals(id.getEntityId())) return ;
    		User entity = getProfileDao().loadUser(id.getEntityId(), user.getZoneId());
  			AccessUtils.modifyCheck(user, entity);  			     		   				
    	} else {
    		//must be a binder 
    		Binder binder = getCoreDao().loadBinder(id.getEntityId(), user.getZoneId());
    		if (binder instanceof TemplateBinder) {
       			getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(user.getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
    		} else {
       			getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
    		}
    	}
    }

}
