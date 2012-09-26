package org.kabling.teaming.install.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class MainContentPanel extends Composite
{
	public MainContentPanel()
	{
		FlowPanel div = new FlowPanel();
		div.addStyleName("rightContent");
		
		initWidget(div);
	}
}
