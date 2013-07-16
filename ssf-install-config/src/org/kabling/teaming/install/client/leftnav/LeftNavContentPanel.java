package org.kabling.teaming.install.client.leftnav;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class LeftNavContentPanel extends Composite
{
	public LeftNavContentPanel()
	{
		FlowPanel leftNavContent = new FlowPanel();
		initWidget(leftNavContent);

		AppResource appResource = AppUtil.getAppResource();
		//We need to change what is shown on the left navigation based on product type, platform
		// Product type is filr
		{
			leftNavContent.add(getLeftNavItem(appResource.network(), LeftNavItemType.NETWORK));
			leftNavContent.add(getLeftNavItem(appResource.database(), LeftNavItemType.DATABASE));
			leftNavContent.add(getLeftNavItem(appResource.lucene(), LeftNavItemType.LUCENE));
			leftNavContent.add(getLeftNavItem(appResource.defaultLocale(), LeftNavItemType.ENVIRONMENT));
			leftNavContent.add(getLeftNavItem(appResource.clustering(), LeftNavItemType.CLUSTERING));
			leftNavContent.add(getLeftNavItem(appResource.reverseProxy(), LeftNavItemType.NOVELL_ACCESS_MANAGER));
			leftNavContent.add(getLeftNavItem(appResource.outboundEmail(), LeftNavItemType.OUTBOUND_EMAIL));
			//leftNavContent.add(getLeftNavItem(appResource.inboundEmail(), LeftNavItemType.INBOUND_EMAIL));
			leftNavContent.add(getLeftNavItem(appResource.requestsAndConnections(),
					LeftNavItemType.REQUESTS_AND_CONNECTIONS));
			//leftNavContent.add(getLeftNavItem(appResource.webServices(), LeftNavItemType.WEB_SERVICES));
			leftNavContent.add(getLeftNavItem(appResource.javaJDK(), LeftNavItemType.JAVA_JDK));
			leftNavContent.add(getLeftNavItem(appResource.webDavAuthentication(), LeftNavItemType.WEBDAV_AUTHENTICATION));
			leftNavContent.add(getLeftNavItem(appResource.license(), LeftNavItemType.LICENSE_INFORMATION));
		}

		//Add Tomcat Restart Panel
		leftNavContent.add(new TomcatRestartPanel());
	}

	private Widget getLeftNavItem(String name, LeftNavItemType type)
	{
		LeftNavItemPanel panel = new LeftNavItemPanel(name, type);
		return panel;
	}
}
