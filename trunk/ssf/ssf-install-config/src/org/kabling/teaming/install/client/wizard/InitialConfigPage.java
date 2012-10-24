package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class InitialConfigPage implements IWizardPage<InstallerConfig>, ClickHandler
{

	private RadioButton useDefaultsRB;
	private RadioButton customRB;
	private FlowPanel fPanel;
	private InstallerConfig config;
	private RadioButton upgradeRB;

	public InitialConfigPage(InstallerConfig config)
	{
		this.config = config;
	}
	
	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public String getPageTitle()
	{
		return "Filr Configuration Wizard";
	}

	@Override
	public Widget getWizardUI()
	{
		if (fPanel == null)
		{
			fPanel = new FlowPanel();
			fPanel.addStyleName("wizardPage");

			HTML descLabel = new HTML(
					"Pick an option");
			descLabel.addStyleName("wizardPageDesc");
			fPanel.add(descLabel);

			FlowPanel radioPanel = new FlowPanel();
			radioPanel.addStyleName("configSelectPanel");
			fPanel.add(radioPanel);

			useDefaultsRB = new RadioButton("config", "Small Deployment");
			useDefaultsRB.addStyleName("configSelectRB");
			useDefaultsRB.addClickHandler(this);
			useDefaultsRB.setValue(true);
			radioPanel.add(useDefaultsRB);

			HTML defaultConfigDescLabel = new HTML(
					"Set up an evaluation environment or small network infrastructure.<br> The Lucene search service and MySQL database will on this Filr virtual appliance.");
			defaultConfigDescLabel.addStyleName("configDescLabel");
			radioPanel.add(defaultConfigDescLabel);

			customRB = new RadioButton("config", "Large Deployment");
			customRB.addStyleName("configSelectRB");
			customRB.addClickHandler(this);
			radioPanel.add(customRB);

			HTML customConfigDescLabel = new HTML(
					"Set up a scalable infrastructure. <br> The Lucene search server and MySQL database will run separately.");
			customConfigDescLabel.addStyleName("configDescLabel");
			radioPanel.add(customConfigDescLabel);
			
			upgradeRB = new RadioButton("config", "Upgrade");
			upgradeRB.addStyleName("configSelectRB");
			upgradeRB.addClickHandler(this);
			radioPanel.add(upgradeRB);

			HTML upgradeConfigDescLabel = new HTML(
					"Upgrade from an older Filr appliance. <br> Select this option if you have already exported the configuration details from the appliance and would like to use those settings.");
			upgradeConfigDescLabel.addStyleName("configDescLabel");
			radioPanel.add(upgradeConfigDescLabel);
		}
		AppUtil.getEventBus().fireEvent(new ConfigNextButtonEnableEvent(!useDefaultsRB.getValue()));

		return fPanel;
	}

	@Override
	public boolean canFinish()
	{
		if (useDefaultsRB == null || useDefaultsRB.getValue())
			return true;

		return false;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == useDefaultsRB || event.getSource() == customRB)
		{
			config.setAdvancedConfiguration(customRB.getValue());
			
			AppUtil.getEventBus().fireEvent(new ConfigFinishEnableEvent(useDefaultsRB.getValue()));
			AppUtil.getEventBus().fireEvent(new ConfigNextButtonEnableEvent(!useDefaultsRB.getValue()));
		}
	}

	@Override
	public void save()
	{

	}
}
