package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigWizardSucessEvent;
import org.kabling.teaming.install.client.ConfigWizardSucessEvent.WizardFinishType;
import org.kabling.teaming.install.client.EditCanceledHandler;
import org.kabling.teaming.install.client.EditSuccessfulHandler;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.WarningDialog;
import org.kabling.teaming.install.client.widgets.DlgBox.DlgButtonMode;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class UpgradeAppliancePage implements IWizardPage<InstallerConfig>, EditCanceledHandler
{

	private FlowPanel fPanel;
	private FormPanel form;
	private FileUpload upload;
	private AppResource RBUNDLE = AppUtil.getAppResource();
	private ConfigWizard wizard;
	private FlowPanel flowPanelWithHiddenData;
	private Hidden updgradeOvewritePanel;

	public UpgradeAppliancePage(ConfigWizard wizard)
	{
		this.wizard = wizard;
	}

	@Override
	public boolean isValid()
	{
		if (upload == null || upload.getFilename() == null || upload.getFilename().equals(""))
		{
			wizard.setErrorMessage("No file has been selected for upload");
			return false;
		}
		return true;
	}

	@Override
	public String getPageTitle()
	{
		return "Import Filr Configuration";
	}

	@Override
	public Widget getWizardUI()
	{
		if (fPanel == null)
		{
			fPanel = new FlowPanel();
			fPanel.addStyleName("configPage");

			// Import/Export Title
			HTML titleDescLabel = new HTML(RBUNDLE.importExportPageDesc());
			titleDescLabel.addStyleName("configPageTitleDescLabel");
			fPanel.add(titleDescLabel);

			FlowPanel importContent = new FlowPanel();
			fPanel.add(importContent);
			importContent.addStyleName("importPageContent");

			form = new FormPanel();
			importContent.add(form);

			flowPanelWithHiddenData = new FlowPanel();
			form.setWidget(flowPanelWithHiddenData);

			updgradeOvewritePanel = new Hidden("upgradeOverwrite", String.valueOf("false"));
			flowPanelWithHiddenData.add(updgradeOvewritePanel);

			if (GWT.isProdMode() && !AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
			{
				form.setAction("/filrconfig/InstallConfig/fileUpload");
			}
			else
			{
				form.setAction("/InstallConfig/fileUpload");
			}

			// Because we're going to add a FileUpload widget, we'll need to set
			// the
			// form to use the POST method, and multipart MIME encoding.
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);

			// Create a FileUpload widget.
			upload = new FileUpload();
			upload.setName("uploadFormElement");
			flowPanelWithHiddenData.add(upload);

			form.addSubmitHandler(new SubmitHandler()
			{

				@Override
				public void onSubmit(SubmitEvent event)
				{
				}
			});

			form.addSubmitCompleteHandler(new SubmitCompleteHandler()
			{

				@Override
				public void onSubmitComplete(SubmitCompleteEvent event)
				{
					String results = event.getResults();
					GWT.log("Results "+results);
					if (results != null)
					{
						// parse the XML document into a DOM
						Document messageDom = XMLParser.parse(results);

						// find the sender's display name in an attribute of the <from> tag
						String success = messageDom.getDocumentElement().getAttribute("success");
						if (success == null || success.equals("false"))
						{
							NodeList dataDriveElement = messageDom.getElementsByTagName("datadrive");
							NodeList hostNameElement = messageDom.getElementsByTagName("hostname");

							if ((dataDriveElement == null || dataDriveElement.getLength() == 0)
									|| (hostNameElement == null || hostNameElement.getLength() == 0))
							{
								wizard.setErrorMessage("Import process failed");
								wizard.hideStatusIndicator();
							}
							else
							{
								StringBuilder builder = new StringBuilder();
								
								if (dataDriveElement != null && dataDriveElement.getLength() > 0)
								{
									if (dataDriveElement.item(0).getChildNodes().item(0).getNodeValue().equals("false"))
									{
									builder.append(RBUNDLE.dataDriveNotFound());
									builder.append("<br><br>");
									}
								}
								
								if (hostNameElement != null && hostNameElement.getLength() > 0)
								{
									if (hostNameElement.item(0).getChildNodes().item(0).getNodeValue().equals("false"))
									{
										builder.append(RBUNDLE.hostNameNoMatch());
										builder.append("<br><br>");
									}
								}
								
								builder.append(RBUNDLE.wishToContinueUpgrade());
								
								WarningDialog dlg = new WarningDialog(builder.toString(),DlgButtonMode.Close);
								dlg.createAllDlgContent(RBUNDLE.warning(),null, UpgradeAppliancePage.this, null);
								dlg.show(true);
							}
						}
						else if (success != null && success.equals("true"))
						{
							// Upgrade successful, throw the message to the user and we probably also
							// need to change the message on the top
							wizard.hideStatusIndicator();
							wizard.hide();
							
							// Set the flag that we have configured
							AppUtil.getProductInfo().setConfigured(true);

							AppUtil.getEventBus().fireEvent(new ConfigWizardSucessEvent(WizardFinishType.UPGRADE));
						}
					}
				}
			});
		}
		return fPanel;
	}

	@Override
	public boolean canFinish()
	{
		return true;
	}

	@Override
	public void save()
	{

	}

	public void upgrade()
	{
		form.submit();
	}

	@Override
	public IWizardPage<InstallerConfig> getPreviousPage()
	{
		return wizard.configPage;
	}

	@Override
	public IWizardPage<InstallerConfig> getNextPage()
	{
		return null;
	}

	@Override
	public boolean editCanceled()
	{
		wizard.getFinishButton().setEnabled(true);
		wizard.hideStatusIndicator();
		Window.Location.assign("/");
		return true;
	}

	@Override
	public FocusWidget getWidgetToFocus()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
