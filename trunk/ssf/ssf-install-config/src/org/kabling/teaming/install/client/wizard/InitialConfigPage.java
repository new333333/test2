package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigFinishEnableEvent;
import org.kabling.teaming.install.client.ConfigNextButtonEnableEvent;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class InitialConfigPage implements IWizardPage<InstallerConfig>, ClickHandler
{

	private RadioButton useDefaultsRB;
	private RadioButton customRB;

	@Override
	public String isValid()
	{
		return null;
	}

	@Override
	public String getPageTitle()
	{
		return "Initial Page";
	}

	@Override
	public Widget getWizardUI()
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("wizardPage");
		
		Label descLabel = new Label("Explain what options to select here, Describe what they are configuring here. ");
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
		
		customRB = new RadioButton("config", "Custom Configuration");
		customRB.addStyleName("configSelectRB");
		customRB.addClickHandler(this);
		radioPanel.add(customRB);
		
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

	@Override
	public void initUIWithData(InstallerConfig object)
	{
		
	}

}
