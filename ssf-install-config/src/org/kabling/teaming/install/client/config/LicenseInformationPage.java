package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigModifiedEvent;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.LicensePageDlgBox;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class LicenseInformationPage extends LicensePageDlgBox
{
	private InlineLabel productTitleLabel;
	private InlineLabel productVersionLabel;
	private InlineLabel issuedOnLabel;
	private InlineLabel issuedByLabel;
	private InlineLabel expirationDateLabel;
	private FormPanel form;
	private FileUpload upload;
	private InlineLabel expirationDatekeyLabel;

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		FlowPanel sectionPanel = createSection(RBUNDLE.currentLicenseInformation());
		fPanel.add(sectionPanel);

		FlexTable table = new FlexTable();
		table.addStyleName("configSummary");
		sectionPanel.add(table);

		int row = 0;
		{
			// Product Title
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.productTitleColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			productTitleLabel = new InlineLabel();
			table.setWidget(row, 1, productTitleLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Product Version
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.productVersionColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			productVersionLabel = new InlineLabel();
			table.setWidget(row, 1, productVersionLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Issued On
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.issuedOnColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			issuedOnLabel = new InlineLabel();
			table.setWidget(row, 1, issuedOnLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Issued By
			InlineLabel keyLabel = new InlineLabel(RBUNDLE.issuedByColon());
			table.setWidget(row, 0, keyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			issuedByLabel = new InlineLabel();
			table.setWidget(row, 1, issuedByLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		{
			row++;
			// Expiration Date
			expirationDatekeyLabel = new InlineLabel(RBUNDLE.expirationDateColon());
			table.setWidget(row, 0, expirationDatekeyLabel);
			table.getFlexCellFormatter().addStyleName(row, 0, "table-key");

			expirationDateLabel = new InlineLabel();
			table.setWidget(row, 1, expirationDateLabel);
			table.getFlexCellFormatter().addStyleName(row, 1, "table-value");
		}

		FlowPanel newLicenseSectionPanel = createSection(RBUNDLE.updateLicense());
		fPanel.add(newLicenseSectionPanel);

		newLicenseSectionPanel.add(createUploadPanel());

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
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
		if (config != null)
		{
			productTitleLabel.setText(config.getProductTitle());
			productVersionLabel.setText(config.getProductVersion());
			issuedByLabel.setText(config.getIssuedBy());
			issuedOnLabel.setText(config.getIssuedDate());
			expirationDateLabel.setText(config.getExpirationDate());

			if (!config.getExpirationDate().contains("-"))
				expirationDatekeyLabel.setText(RBUNDLE.expirationDaysColon());
		}
	}

	private FlowPanel createSection(String header)
	{
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.addStyleName("section");

		if (header != null)
		{
			Label label = new Label(header);
			label.addStyleName("sectionHeader");
			flowPanel.add(label);
		}
		return flowPanel;
	}

	private FlowPanel createUploadPanel()
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		// Import/Export Title
		HTML titleDescLabel = new HTML(RBUNDLE.uploadNewLicenseDesc());
		titleDescLabel.addStyleName("configPageTitleDescLabel");
		fPanel.add(titleDescLabel);

		FlowPanel importContent = new FlowPanel();
		fPanel.add(importContent);
		importContent.addStyleName("importPageContent");

		form = new FormPanel();
		importContent.add(form);

		FlowPanel panel = new FlowPanel();
		form.setWidget(panel);

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

		// Set the value to let the back end know that we are uploading license
		panel.add(new Hidden("licenseKey", String.valueOf("true")));

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

		form.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event)
			{
				hide(true);
				AppUtil.getEventBus().fireEvent(new ConfigModifiedEvent(true,true));
			}
		});

		return fPanel;
	}

	@Override
	public boolean editSuccessful(Object obj)
	{
		// Save the configuration
		//If there is no file, nothing to do.
		if (upload.getFilename() != null && !upload.getFilename().equals(""))
		{
			form.submit();

			// Return false, we will close if the save is successful
			return false;
		}
		return true;
	}


	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("license");
		
		return helpData;
	}
}
