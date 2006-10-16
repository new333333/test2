package com.sitescape.ef.module.dashboard;
import java.util.Map;

import com.sitescape.ef.domain.Dashboard;
import com.sitescape.ef.domain.DashboardPortlet;
import com.sitescape.ef.domain.EntityDashboard;
import com.sitescape.ef.domain.UserDashboard;
import com.sitescape.ef.domain.EntityIdentifier;
/**
 * <code>DashboardModule</code> provides dashboard-related operations
 * 
 * @author Janet McCann
 */
public interface DashboardModule {

	public Dashboard getDashboard(String id);
    public EntityDashboard createEntityDashboard(EntityIdentifier ownerId, Map properties);
    public EntityDashboard getEntityDashboard(EntityIdentifier ownerId);
    public UserDashboard createUserDashboard(EntityIdentifier ownerId, Long binderId, Map properties);
    public UserDashboard getUserDashboard(EntityIdentifier ownerId, Long binderId);
    public DashboardPortlet createDashboardPortlet(String portletName, Map properties);
    public void setProperty(String id, String key, Object value);
    public void modifyDashboard(String id, Map inputData); 
    public String addComponent(String id, String scope, String listName, Map component);
    public void modifyComponent(String id, String componentId, Map component);
    public void deleteComponent(String id, String listName, String componentId);
    	 
}