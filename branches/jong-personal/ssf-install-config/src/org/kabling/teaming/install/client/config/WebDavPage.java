package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.InstallerConfig.WebDAV;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

public class WebDavPage extends ConfigPageDlgBox
{
	private ListBox authListBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.webDavPageTitleDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);
		
		HTML descLabel = new HTML(RBUNDLE.webDavPageDesc());
		descLabel.addStyleName("configPageDescLabel");
		fPanel.add(descLabel);

		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		
		contentPanel.addStyleName("webDavPageContent");
		InlineLabel authLabel = new InlineLabel(RBUNDLE.webDavPageAuthMethodColon());
		contentPanel.add(authLabel);
		
		authListBox = new ListBox(false);
		authListBox.addItem(RBUNDLE.basic(), "basic");
		authListBox.addItem(RBUNDLE.digest(),"digest");
		authListBox.setSelectedIndex(0);
		contentPanel.add(authListBox);
		
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		if (authListBox.getSelectedIndex() == 0)
			config.setWebDav(WebDAV.BASIC);
		else
			config.setWebDav(WebDAV.DIGEST);
		
		return config;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
	public void initUIWithData()
	{
		if (config.getWebDav().equals(WebDAV.BASIC))
			authListBox.setSelectedIndex(0);
		else
			authListBox.setSelectedIndex(1);
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("webdav");
		
		return helpData;
	}
	
	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.WEBDAV_AUTHENTICATION);
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate,saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}

}
