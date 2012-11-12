package org.kabling.teaming.install.client.config;

import org.kabling.teaming.install.client.ConfigPageDlgBox;

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
			if (GWT.isProdMode())
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

}
