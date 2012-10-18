package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class StoragePage implements IWizardPage<InstallerConfig>,ClickHandler
{

	private InstallerConfig config;
	private ConfigWizard wizard;
	private FlowPanel content;
	private AppResource RBUNDLE = AppUtil.getAppResource();
	private RadioButton smbRadioButton;
	private RadioButton nfsRadioButton;
	private RadioButton deviceRadioButton;
	private FlowPanel storageDetailsPanel;

	public StoragePage(ConfigWizard wizard, InstallerConfig config)
	{
		this.config = config;
		this.wizard = wizard;
	}

	@Override
	public Widget getWizardUI()
	{
		if (content == null)
		{
			content = new FlowPanel();
			content.addStyleName("wizardPage");

			HTML descLabel = new HTML("Select the storage type to use for remote storage:");
			descLabel.addStyleName("wizardPageDesc");
			content.add(descLabel);

			FlowPanel storageTypePanel = new FlowPanel();
			content.add(storageTypePanel);
			
			deviceRadioButton = new RadioButton("storage","Device");
			deviceRadioButton.addStyleName("configSelectRB");
			deviceRadioButton.addClickHandler(this);
			storageTypePanel.add(deviceRadioButton);
			
			nfsRadioButton = new RadioButton("storage","NFS");
			nfsRadioButton.addStyleName("configSelectRB");
			nfsRadioButton.addClickHandler(this);
			storageTypePanel.add(nfsRadioButton);
			
			smbRadioButton = new RadioButton("storage","SMB/CIFS(1.0)");
			smbRadioButton.addStyleName("configSelectRB");
			smbRadioButton.addClickHandler(this);
			storageTypePanel.add(smbRadioButton);

			storageDetailsPanel = new FlowPanel();
			storageDetailsPanel.addStyleName("storageDetailsPanel");
			content.add(storageDetailsPanel);
			
			initUIWithData();
		}
		return content;
	}

	@Override
	public String getPageTitle()
	{
		return "Storage";
	}

	@Override
	public boolean canFinish()
	{
		return true;
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public void save()
	{
		
	}

	public void initUIWithData()
	{
	
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == deviceRadioButton)
		{
			storageDetailsPanel.clear();
			storageDetailsPanel.add(new DeviceStorageConfigPanel());
		}
		else if (event.getSource() == nfsRadioButton)
		{
			storageDetailsPanel.clear();
			storageDetailsPanel.add(new NfsStorageConfigPanel());
		}
		else if (event.getSource() == smbRadioButton)
		{
			storageDetailsPanel.clear();
		}
	}

}
