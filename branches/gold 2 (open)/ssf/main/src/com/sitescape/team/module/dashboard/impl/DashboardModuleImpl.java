/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.dashboard.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserDashboard;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.ObjectPropertyNotFoundException;
import com.sitescape.util.Validator;
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
		return getCoreDao().loadDashboard(id);
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
    	Dashboard d = getCoreDao().loadDashboard(id);
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
    	Dashboard d = getCoreDao().loadDashboard(id);
    	checkAccess(d);
   		d.setProperty(key, value);
   	   	
    }
    public String addComponent(String id, String scope, String listName, Map component) {
    	Dashboard d = getCoreDao().loadDashboard(id);
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
    	Dashboard d = getCoreDao().loadDashboard(id);
    	checkAccess(d);
		
    	Map components = (Map) d.getProperty(Dashboard.COMPONENTS);
    	if (components == null) {
    		components = new HashMap();
    		d.setProperty(Dashboard.COMPONENTS, components);
    	}
    	components.put(componentId, component);
    }
    
    public void deleteComponent(String id, String listName, String componentId) {
    	Dashboard d = getCoreDao().loadDashboard(id);
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
       			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
    		} else {
       			getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
    		}
    	}
    }

}
