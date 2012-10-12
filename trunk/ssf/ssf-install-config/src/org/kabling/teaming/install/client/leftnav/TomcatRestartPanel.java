package org.kabling.teaming.install.client.leftnav;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigModifiedEvent;
import org.kabling.teaming.install.client.ConfigModifiedEvent.ConfigModifiedEventHandler;
import org.kabling.teaming.install.client.i18n.AppResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class TomcatRestartPanel extends Composite implements ConfigModifiedEventHandler, ClickHandler
{
	private FlowPanel tomcatRestartPanel;
	private RestartFilrServerCallback callback = new RestartFilrServerCallback();
	private Button restartTomcatButton;
	private InlineLabel tomcatRestartLabel;
	private AppResource RBUNDLE = AppUtil.getAppResource();

	public TomcatRestartPanel()
	{
		tomcatRestartPanel = new FlowPanel();
		tomcatRestartPanel.addStyleName("tomcatRestartPanel");

		// Button for restarting tomcat
		restartTomcatButton = new Button(RBUNDLE.restartFilrServer());
		restartTomcatButton.addClickHandler(this);
		restartTomcatButton.addStyleName("tomcatRestartButton");
		tomcatRestartPanel.add(restartTomcatButton);

		tomcatRestartLabel = new InlineLabel(RBUNDLE.tomcatRestartRequired());
		tomcatRestartLabel.addStyleName("tomcatRestartDescLabel");
		tomcatRestartPanel.add(tomcatRestartLabel);

		// Look for config modifications
		AppUtil.getEventBus().addHandler(ConfigModifiedEvent.TYPE, this);

		tomcatRestartPanel.setVisible(false);
		initWidget(tomcatRestartPanel);
	}

	@Override
	public void onEvent(ConfigModifiedEvent event)
	{
		// Config has been changed
		// We always want to show this panel after the initial configuration change
		tomcatRestartPanel.setVisible(true);

		// Enable the restart button if the configuration has changed
		restartTomcatButton.setEnabled(event.isModified());

		// Show the right text based on if the configuration has changed or filr server has been restarted
		if (event.isModified())
		{
			tomcatRestartLabel.setText(RBUNDLE.tomcatRestartRequired());
		}
		else
		{
			tomcatRestartLabel.setText(RBUNDLE.configUpToDate());
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		// Disable the button, we don't wnat the user to click again
		restartTomcatButton.setEnabled(false);

		// Restrat Filr server
		AppUtil.getInstallService().startFilrServer(callback);
	}

	class RestartFilrServerCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			// TODO:
		}

		@Override
		public void onSuccess(Void result)
		{
			// All configuration is up to date
			AppUtil.getEventBus().fireEvent(new ConfigModifiedEvent(false));
		}
	}
}
