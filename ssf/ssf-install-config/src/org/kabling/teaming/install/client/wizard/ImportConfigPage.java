package org.kabling.teaming.install.client.wizard;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class ImportConfigPage implements IWizardPage<InstallerConfig>, ClickHandler
{

	private FlowPanel fPanel;
	private FormPanel form;
	private FileUpload upload;
	private AppResource RBUNDLE = AppUtil.getAppResource();
	private Button importButton;
	private ConfigWizard wizard;

	public ImportConfigPage(ConfigWizard wizard)
	{
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

			FlowPanel panel = new FlowPanel();
			form.setWidget(panel);

			if (GWT.isProdMode())
			{
				form.setAction("/InstallConfig" + "/fileUpload");
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
			panel.add(upload);

			form.addSubmitHandler(new SubmitHandler()
			{

				@Override
				public void onSubmit(SubmitEvent event)
				{
				}
			});

			importButton = new Button("Import");
			importButton.addClickHandler(this);
			importButton.addStyleName("exportButton");
			importContent.add(importButton);
		}
		return fPanel;
	}

	@Override
	public boolean canFinish()
	{
		return false;
	}

	@Override
	public void save()
	{

	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == importButton)
		{
			
		}
	}
	@Override
	public IWizardPage<InstallerConfig> getPreviousPage() {
		return wizard.configPage;
	}
	@Override
	public IWizardPage<InstallerConfig> getNextPage() {
		return null;
	}
}
