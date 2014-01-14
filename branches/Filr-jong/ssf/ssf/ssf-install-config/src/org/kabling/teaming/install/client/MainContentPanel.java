package org.kabling.teaming.install.client;

import org.kabling.teaming.install.client.ConfigWizardSucessEvent.ConfigWizardSuccessEventHandler;
import org.kabling.teaming.install.client.config.ConfigSummaryPage;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class MainContentPanel extends Composite implements ConfigWizardSuccessEventHandler
{
	private FlowPanel div;

	public MainContentPanel()
	{
		div = new FlowPanel();
		div.addStyleName("rightContent");

		if (AppUtil.getProductInfo().isConfigured())
		{
			div.add(new ConfigSummaryPage());
		}
		initWidget(div);

		// Look for config modifications
		AppUtil.getEventBus().addHandler(ConfigWizardSucessEvent.TYPE, this);
	}

	@Override
	public void onEvent(ConfigWizardSucessEvent event)
	{
		div.clear();
		div.add(new ConfigSummaryPage(event.getWizFinishType()));
	}
}
