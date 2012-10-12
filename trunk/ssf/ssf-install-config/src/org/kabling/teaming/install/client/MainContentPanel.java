package org.kabling.teaming.install.client;

import org.kabling.teaming.install.client.config.ConfigSummaryPage;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class MainContentPanel extends Composite
{
	public MainContentPanel()
	{
		FlowPanel div = new FlowPanel();
		div.addStyleName("rightContent");

		// TODO: We probably need to show a different page after the initial configuration wizard

		div.add(new ConfigSummaryPage());
		initWidget(div);
	}
}
