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
		return "Filr Configuration";
	}

	@Override
	public Widget getWizardUI()
	{
		if (fPanel == null)
		{
			fPanel = new FlowPanel();
			fPanel.addStyleName("wizardPage");

			HTML descLabel = new HTML(
					"Your first task is to configure the Filr virtual appliance to access<br> - A Lucene search server <br> - A MySQL database.<br><br<br> Pick an option below.");
			descLabel.addStyleName("wizardPageDesc");
			fPanel.add(descLabel);

			FlowPanel radioPanel = new FlowPanel();
			radioPanel.addStyleName("configSelectPanel");
			fPanel.add(radioPanel);

			useDefaultsRB = new RadioButton("config", "Use Defaults");
			useDefaultsRB.addStyleName("configSelectRB");
			useDefaultsRB.addClickHandler(this);
			useDefaultsRB.setValue(true);
			radioPanel.add(useDefaultsRB);

			HTML defaultConfigDescLabel = new HTML(
					"Use the default Lucene search server and MySQL database that are providd with the Filr virtual appliance."
							+ "<br><br>A good choice for evaluation environments and small networks. All components run in the Filr virtual appliance");
			defaultConfigDescLabel.addStyleName("configDescLabel");
			radioPanel.add(defaultConfigDescLabel);

			customRB = new RadioButton("config", "Custom Configuration");
			customRB.addStyleName("configSelectRB");
			customRB.addClickHandler(this);
			radioPanel.add(customRB);

			HTML customConfigDescLabel = new HTML(
					"Specify a Lucene search server and MySQL database for the Filr virtual appliance to use. <br><br>"
							+ "The recommended choice for most environments because you can set up a robust infrastructure in which the components run separately.");
			customConfigDescLabel.addStyleName("configDescLabel");
			radioPanel.add(customConfigDescLabel);
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
