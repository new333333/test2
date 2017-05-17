package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.Network;
import org.kabling.teaming.install.shared.WebService;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

public class WebServicesPage extends ConfigPageDlgBox
{
	private CheckBox wssAuthCheckBox;
	private CheckBox basicAuthCheckBox;
	private CheckBox tokenAuthCheckBox;
	private CheckBox anonymousAccessCheckBox;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		HTML titleDescLabel = new HTML(RBUNDLE.enableWebServices());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);
		
		FlowPanel contentPanel = new FlowPanel();
		fPanel.add(contentPanel);
		
		contentPanel.addStyleName("webServicePageContent");
		
		wssAuthCheckBox = new CheckBox(RBUNDLE.enableWssAuth());
		wssAuthCheckBox.addStyleName("webServicePageAuthCheckBox");
		contentPanel.add(wssAuthCheckBox);
		
		basicAuthCheckBox = new CheckBox(RBUNDLE.enableBasicAuth());
		basicAuthCheckBox.addStyleName("webServicePageAuthCheckBox");
		contentPanel.add(basicAuthCheckBox);
		
		tokenAuthCheckBox = new CheckBox(RBUNDLE.enableTokenBasedAuth());
		tokenAuthCheckBox.addStyleName("webServicePageAuthCheckBox");
		contentPanel.add(tokenAuthCheckBox);
		
		anonymousAccessCheckBox = new CheckBox(RBUNDLE.enableAnonymousAccess());
		anonymousAccessCheckBox.addStyleName("webServicePageAuthCheckBox");
		contentPanel.add(anonymousAccessCheckBox);
		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		Network network = config.getNetwork();
		
		if (network != null)
		{
			WebService webService = network.getWebService();
			webService.setEnabled(wssAuthCheckBox.getValue());
			webService.setBasicEnabled(basicAuthCheckBox.getValue());
			webService.setTokenEnabled(tokenAuthCheckBox.getValue());
			webService.setAnonymousEnabled(anonymousAccessCheckBox.getValue());
		}

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
		Network network = config.getNetwork();
		
		if (network != null)
		{
			WebService webService = network.getWebService();
			wssAuthCheckBox.setValue(webService.isEnabled());
			tokenAuthCheckBox.setValue(webService.isTokenEnabled());
			basicAuthCheckBox.setValue(webService.isBasicEnabled());
			anonymousAccessCheckBox.setValue(webService.isAnonymousEnabled());
		}
	}

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("webservices");
		
		return helpData;
	}
	
	@Override
	public boolean editSuccessful(Object obj)
	{
		List<LeftNavItemType> sectionsToUpdate = new ArrayList<LeftNavItemType>();
		sectionsToUpdate.add(LeftNavItemType.WEB_SERVICES);
		// Save the configuration
		AppUtil.getInstallService().saveConfiguration((InstallerConfig) obj, sectionsToUpdate,saveConfigCallback);

		// Return false, we will close if the save is successful
		return false;
	}

}
