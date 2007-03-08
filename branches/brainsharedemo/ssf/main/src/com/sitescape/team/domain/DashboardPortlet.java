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
