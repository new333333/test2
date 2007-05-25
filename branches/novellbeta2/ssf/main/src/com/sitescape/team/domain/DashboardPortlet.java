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
package com.sitescape.team.domain;
/**
 * @hibernate.subclass discriminator-value="P" dynamic-update="true"
 * 
 * @author Jong Kim
 * 
 */
public class DashboardPortlet extends Dashboard {
	protected String portletName;
	
	public DashboardPortlet() {
		super();
	}
	public DashboardPortlet(DashboardPortlet dashboard) {
		super(dashboard);
		setPortletName(dashboard.getPortletName());
	}
	public DashboardPortlet(String portletName) {
		super();
		this.portletName = portletName;		
	}
	public String getPortletName() {
		return portletName;
	}
	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}

}
