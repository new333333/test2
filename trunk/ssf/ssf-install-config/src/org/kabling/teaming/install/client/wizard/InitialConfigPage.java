package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class InitialConfigPage implements IWizardPage<InstallerConfig>, ClickHandler
{

	private RadioButton useDefaultsRB;
	private RadioButton customRB;
	private FlowPanel fPanel;

	@Override
	public String isValid()
	{
		return null;
	}

	@Override
	public String getPageTitle()
	{
		return "Welcome Screen";
	}

	@Override
	public Widget getWizardUI()
	{
		if (fPanel == null)
		{
			fPanel = new FlowPanel();
			fPanel.addStyleName("wizardPage");

			HTML descLabel = new HTML("This configuration wizard will help to setup the FILR server.");
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

			Label defaultConfigDescLabel = new Label(
					"The default configuration will configure the FILR server to use the local lucene server and local database.");
			defaultConfigDescLabel.addStyleName("configDescLabel");
			radioPanel.add(defaultConfigDescLabel);

			customRB = new RadioButton("config", "Custom Configuration");
			customRB.addStyleName("configSelectRB");
			customRB.addClickHandler(this);
			radioPanel.add(customRB);

			Label customConfigDescLabel = new Label(
					"Custom configuration allows you configure the Lucene and Database server to run on a different system.");
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
			AppUtil.getEventBus().fireEvent(new ConfigFinishEnableEvent(useDefaultsRB.getValue()));
			AppUtil.getEventBus().fireEvent(new ConfigNextButtonEnableEvent(!useDefaultsRB.getValue()));
		}
	}

	@Override
	public void save()
	{

	}
}
