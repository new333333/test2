package org.kabling.teaming.install.client.leftnav;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class LeftNavContentPanel extends Composite
{
	public LeftNavContentPanel()
	{
		FlowPanel leftNavContent = new FlowPanel();
		initWidget(leftNavContent);

		// Product type is filr
		{
			leftNavContent.add(getLeftNavItem("File System", LeftNavItemType.FILE_SYSTEM));
			leftNavContent.add(getLeftNavItem("Network Configuration", LeftNavItemType.NETWORK));
			leftNavContent.add(getLeftNavItem("Lucene", LeftNavItemType.LUCENE));
			leftNavContent.add(getLeftNavItem("Database", LeftNavItemType.DATABASE));
			leftNavContent.add(getLeftNavItem("Environment", LeftNavItemType.ENVIRONMENT));
			leftNavContent.add(getLeftNavItem("Clustering", LeftNavItemType.CLUSTERING));
			leftNavContent.add(getLeftNavItem("Mirrored Folders", LeftNavItemType.MIRROR_FOLDERS));
		}
	}

	private Widget getLeftNavItem(String name, LeftNavItemType type)
	{
		LeftNavItemPanel panel = new LeftNavItemPanel(name, type);
		return panel;
	}
}
