package org.kabling.teaming.install.client.config;

import java.util.ArrayList;
import java.util.List;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigPageDlgBox;
import org.kabling.teaming.install.client.HelpData;
import org.kabling.teaming.install.client.leftnav.LeftNavItemType;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.ProductInfo.ProductType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 *  UI for setting up clustering
 *
 */
public class ExportConfigurationPage extends ConfigPageDlgBox implements ClickHandler
{
	private Button exportButton;

	public ExportConfigurationPage()
	{
		super(DlgButtonMode.Close);

		// Don't show the header close button
		showHeaderCloseButton(false);
	}

	@Override
	public Panel createContent(Object propertiesObj)
	{
		FlowPanel fPanel = new FlowPanel();
		fPanel.addStyleName("configPage");

		FlowPanel exportContent = createSection(RBUNDLE.exportFilrServerConfig());
		fPanel.add(exportContent);

		HTML exportLabel = new HTML(RBUNDLE.exportDesc());
		exportLabel.addStyleName("configPageTitleDescLabel");
		exportContent.add(exportLabel);

		exportButton = new Button(RBUNDLE.export());
		exportButton.addClickHandler(this);
		exportButton.addStyleName("exportButton");
		exportContent.add(exportButton);

		return fPanel;
	}

	@Override
	public Object getDataFromDlg()
	{
		return null;
	}

	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	@Override
	public void initUIWithData()
	{

	}

	@Override
	public void onClick(ClickEvent event)
	{
		super.onClick(event);
		if (event.getSource() == exportButton)
		{
			//Since the filr is overlayed into root.war, the path we need to go for filr is /InstallConfig/fileUpload
			
			if (GWT.isProdMode() && !AppUtil.getProductInfo().getType().equals(ProductType.NOVELL_FILR))
				Window.Location.replace("/filrconfig/InstallConfig/fileUpload");
			else
				Window.Location.replace("/InstallConfig/fileUpload");
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

	@Override
	public HelpData getHelpData()
	{
		HelpData helpData =  new HelpData();
		helpData.setPageId("update");
		
		return helpData;
	}
	
	@Override
	public boolean editSuccessful(Object obj)
	{
		return true;
	}

}
