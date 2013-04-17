package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigWizardSucessEvent;
import org.kabling.teaming.install.client.ConfigWizardSucessEvent.WizardFinishType;
import org.kabling.teaming.install.client.EditCanceledHandler;
import org.kabling.teaming.install.client.EditSuccessfulHandler;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.WarningDialog;
import org.kabling.teaming.install.client.widgets.DlgBox.DlgButtonMode;
import org.kabling.teaming.install.shared.Database;
import org.kabling.teaming.install.shared.DatabaseConfig;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo;
import org.kabling.teaming.install.shared.UpdateStatus;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class InitialConfigPage implements IWizardPage<InstallerConfig>, ClickHandler, EditCanceledHandler
{

	private RadioButton useDefaultsRB;
	private RadioButton customRB;
	private FlowPanel fPanel;
	private InstallerConfig config;
	private ConfigWizard wizard;
	private HTML upgradeDesc;
	protected AppResource RBUNDLE = AppUtil.getAppResource();
	private HTML vashareNotAvailableLabel;
	public InitialConfigPage(ConfigWizard wizard, InstallerConfig config)
	{
		this.config = config;
		this.wizard = wizard;
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

			if (config.isUpdateMode())
			{
				fPanel.add(getUpgradePanel());
			}
			else
			{

				HTML descLabel = new HTML("Pick an option.");
				descLabel.addStyleName("wizardPageDesc");
				fPanel.add(descLabel);

				FlowPanel radioPanel = new FlowPanel();
				radioPanel.addStyleName("configSelectPanel");
				fPanel.add(radioPanel);

				useDefaultsRB = new RadioButton("config", "Small Deployment");
				useDefaultsRB.addStyleName("configSelectRB");
				useDefaultsRB.addClickHandler(this);
				radioPanel.add(useDefaultsRB);

				HTML defaultConfigDescLabel = new HTML(
						"Set up an evaluation environment or small network infrastructure.<br> The Lucene search service and MySQL database will be installed on this Filr virtual appliance.");
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
				customConfigDescLabel.addStyleName("configDescLabel");
				radioPanel.add(customConfigDescLabel);
				
				vashareNotAvailableLabel = new HTML(RBUNDLE.vashareNotAvailableDesc());
				vashareNotAvailableLabel.setVisible(false);
				vashareNotAvailableLabel.addStyleName("vashareNotAvailableLabel");
				radioPanel.add(vashareNotAvailableLabel);

				if (config.isVashareAvailable())
					customRB.setValue(true);
				else
				{
					useDefaultsRB.setValue(true);
				}
			}
		}

		return fPanel;
	}

	private Widget getUpgradePanel()
	{
		FlowPanel fPanel = new FlowPanel();

		HTML titleLabel = new HTML("Upgrade Appliance");
		fPanel.add(titleLabel);
		
		ProductInfo productInfo = AppUtil.getProductInfo();
		upgradeDesc = new HTML("An older Filr configuration has been found. <br><br> Click Finish to upgrade to the version "+productInfo.getProductVersion());
		upgradeDesc.addStyleName("wizardPageDesc");
		fPanel.add(upgradeDesc);
		
		AppUtil.getInstallService().getProductInfoFromZipFile(new AsyncCallback<ProductInfo>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(ProductInfo result)
			{
				ProductInfo productInfo = AppUtil.getProductInfo();
				upgradeDesc.setHTML("An older Filr configuration has been found. <br><br>Click Finish to upgrade from version "+result.getProductVersion() +" to "+productInfo.getProductVersion() );	
			}
		});
		return fPanel;
	}

	@Override
	public boolean canFinish()
	{
		return config.isUpdateMode();
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == useDefaultsRB)
		{
			config.setAdvancedConfiguration(false);
			vashareNotAvailableLabel.setVisible(false);
		}
		else if (event.getSource() == customRB)
		{
			config.setAdvancedConfiguration(true);
			if (!config.isVashareAvailable())
				vashareNotAvailableLabel.setVisible(true);
		}
		wizard.getFinishButton().setEnabled(false);
		wizard.getNextButton().setEnabled(true);
	}

	@Override
	public void save()
	{
		// For small deployment, set the default password to be root
		if (!config.isUpdateMode() && useDefaultsRB.getValue())
		{
			Database db = config.getDatabase();
			DatabaseConfig dbConfig = db.getDatabaseConfig("Installed");
			if (dbConfig == null)
			{
				DatabaseConfig mysqlConfig = db.getDatabaseConfig("MySQL_Default");
				dbConfig = new DatabaseConfig();
				
				dbConfig.setId("Installed");
				dbConfig.setResourceDatabase(mysqlConfig.getResourceDatabase());
				dbConfig.setResourceDriverClassName(mysqlConfig.getResourceDriverClassName());
				dbConfig.setResourceFor(mysqlConfig.getResourceFor());
				dbConfig.setResourceHost(mysqlConfig.getResourceHost());
				dbConfig.setResourceUrl(mysqlConfig.getResourceUrl());
				dbConfig.setResourceUserName(mysqlConfig.getResourceUserName());
				db.getConfig().add(dbConfig);
				
				//Set the default to be Installed in the Database
				db.setConfigName("Installed");
			}
			dbConfig.setResourcePassword("root");
		}
	}

	@Override
	public IWizardPage<InstallerConfig> getPreviousPage()
	{
		return null;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage()
	{
		if (config.isUpdateMode())
			return null;

		if (useDefaultsRB.getValue())
			return wizard.dbLocalPage;
		else if (customRB.getValue())
			return wizard.dbPage;

		return wizard.importPage;
	}

	public String getDeploymentType()
	{
		if (config.isUpdateMode())
			return "upgrade";

		if (useDefaultsRB.getValue())
			return "local";
		else if (customRB.getValue())
			return "large";
		return "upgrade";
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void performUpdate(boolean ignoreDataDrive, boolean ignoreHostNameNotMatch)
	{
		AppUtil.getInstallService().updateFilr(ignoreDataDrive, ignoreHostNameNotMatch, new AsyncCallback<UpdateStatus>()
		{

			@Override
			public void onSuccess(UpdateStatus result)
			{
				wizard.hideStatusIndicator();

				if (!result.isSuccess())
				{
						{
							StringBuilder builder = new StringBuilder();

							if (!result.isValidDataDrive())
							{
								builder.append(AppUtil.getAppResource().dataDriveNotFound());
								builder.append("<br><br>");
							}

							if (!result.isValidHostName())
							{
								builder.append(AppUtil.getAppResource().hostNameNoMatch());
								builder.append("<br><br>");
							}

                            if (result.getMessage() != null)
                            {
                                builder.append(result.getMessage());
                                builder.append("<br><br>");
                            }
                            else
                            {
							    builder.append(AppUtil.getAppResource().wishToContinueUpgrade());
                            }

							WarningDialog dlg = new WarningDialog(builder.toString(),DlgButtonMode.Close);
							dlg.createAllDlgContent(AppUtil.getAppResource().warning(), null, InitialConfigPage.this,
									null);
							dlg.show(true);
						}
				}
				else
				{
					// Upgrade successful, throw the message to the user and we probably also
					// need to change the message on the top
					wizard.hide();

					// Set the flag that we have configured
					AppUtil.getProductInfo().setConfigured(true);

					AppUtil.getEventBus().fireEvent(new ConfigWizardSucessEvent(WizardFinishType.UPGRADE));
				}
			}

			@Override
			public void onFailure(Throwable caught)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean editCanceled()
	{
		wizard.getFinishButton().setEnabled(true);
		wizard.hideStatusIndicator();
		Window.Location.assign("/");
		return true;
	}
}
