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
package com.sitescape.team.module.dashboard;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DashboardPortlet;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.UserDashboard;
/**
 * <code>DashboardModule</code> provides dashboard-related operations
 * 
 * @author Janet McCann
 */
public interface DashboardModule {

	public Dashboard getDashboard(String id);
    public EntityDashboard createEntityDashboard(EntityIdentifier ownerId, Map properties);
    public EntityDashboard getEntityDashboard(EntityIdentifier ownerId);
    public EntityDashboard createEntityDashboard(EntityIdentifier ownerId, Element config);
    public UserDashboard createUserDashboard(EntityIdentifier ownerId, Long binderId, Map properties);
    public UserDashboard getUserDashboard(EntityIdentifier ownerId, Long binderId);
    public DashboardPortlet createDashboardPortlet(String portletName, Map properties);
    public void setProperty(String id, String key, Object value);
    public void modifyDashboard(String id, Map inputData); 
    public String addComponent(String id, String scope, String listName, Map component);
    public void modifyComponent(String id, String componentId, Map component);
    public void deleteComponent(String id, String listName, String componentId);
    	 
}