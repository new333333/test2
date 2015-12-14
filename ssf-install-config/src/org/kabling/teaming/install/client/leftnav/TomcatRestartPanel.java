package org.kabling.teaming.install.client.leftnav;

import org.kabling.teaming.install.client.AppUtil;
import org.kabling.teaming.install.client.ConfigModifiedEvent;
import org.kabling.teaming.install.client.ConfigModifiedEvent.ConfigModifiedEventHandler;
import org.kabling.teaming.install.client.RevertChangesEvent;
import org.kabling.teaming.install.client.RevertChangesEvent.RevertChangesEventHandler;
import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.StatusIndicator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class TomcatRestartPanel extends Composite implements ConfigModifiedEventHandler, ClickHandler,RevertChangesEventHandler
{
	private FlowPanel tomcatRestartPanel;
	private RestartFilrServerCallback callback = new RestartFilrServerCallback();
	private RevertChangesCallback revertCallback = new RevertChangesCallback();
	private UnsavedConfigExistsCallback unsavedChangesExistsCallback = new UnsavedConfigExistsCallback();
	private Button restartTomcatButton;
	private Button revertButton;
	private InlineLabel tomcatRestartLabel;
	private AppResource RBUNDLE = AppUtil.getAppResource();
	private StatusIndicator statusIndicator;

	public TomcatRestartPanel()
	{
		tomcatRestartPanel = new FlowPanel();
		tomcatRestartPanel.addStyleName("tomcatRestartPanel");

		// Button for restarting tomcat
		restartTomcatButton = new Button(RBUNDLE.reconfigureServer());
		restartTomcatButton.addClickHandler(this);
		restartTomcatButton.addStyleName("tomcatRestartButton");
		tomcatRestartPanel.add(restartTomcatButton);

		tomcatRestartLabel = new InlineLabel(RBUNDLE.tomcatRestartRequired());
		tomcatRestartLabel.addStyleName("tomcatRestartDescLabel");
		tomcatRestartPanel.add(tomcatRestartLabel);

		// Look for config modifications
		AppUtil.getEventBus().addHandler(ConfigModifiedEvent.TYPE, this);
		AppUtil.getEventBus().addHandler(RevertChangesEvent.TYPE, this);

		// Button for restarting tomcat
		revertButton = new Button(RBUNDLE.revertChanges());
		revertButton.addClickHandler(this);
		revertButton.addStyleName("revertChangesButton");
		tomcatRestartPanel.add(revertButton);

		tomcatRestartPanel.setVisible(false);
		initWidget(tomcatRestartPanel);
		
		AppUtil.getInstallService().isUnsavedConfigurationExists(unsavedChangesExistsCallback);
	}

	@Override
	public void onEvent(ConfigModifiedEvent event)
	{
		// Config has been changed
		// We always want to show this panel after the initial configuration change
		tomcatRestartPanel.setVisible(true);

		// Enable the restart button if the configuration has changed
		// If it already requires a restart, we don't have to do anything
		if (!restartTomcatButton.isEnabled())
			restartTomcatButton.setEnabled(event.isRequireTomcatRestart());

		// Show the right text based on if the configuration has changed or filr server has been restarted
		if (restartTomcatButton.isEnabled())
		{
			tomcatRestartLabel.setText(RBUNDLE.tomcatRestartRequired());
		}
		else
		{
			tomcatRestartLabel.setText(RBUNDLE.configUpToDate());
		}
		
		revertButton.setEnabled(true);
	}

	@Override
	public void onClick(ClickEvent event)
	{
		if (event.getSource() == restartTomcatButton)
		{
			// Disable the button, we don't wnat the user to click again
			restartTomcatButton.setEnabled(false);

			if (statusIndicator == null)
			{
				statusIndicator = new StatusIndicator(RBUNDLE.reconfiguringServer());
				statusIndicator.setGlassEnabled(true);
			}
			statusIndicator.center();

			// Restrat Filr server
			AppUtil.getInstallService().reconfigure(true, callback);
		}
		else if (event.getSource() == revertButton)
		{
			revertButton.setEnabled(false);
			// Restrat Filr server
			AppUtil.getInstallService().reverConfiguration(revertCallback);
		}
	}

	class RestartFilrServerCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			statusIndicator.hide();
		}

		@Override
		public void onSuccess(Void result)
		{
			statusIndicator.hide();
			AppUtil.getEventBus().fireEvent(new ConfigModifiedEvent(false, false));
		}
	}
	
	class RevertChangesCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
		}

		@Override
		public void onSuccess(Void result)
		{
			tomcatRestartPanel.setVisible(false);
			
			// Configuration modified, update the summary page
			AppUtil.getEventBus().fireEvent(new RevertChangesEvent(true));
		}
	}
	
	class UnsavedConfigExistsCallback implements AsyncCallback<Boolean>
	{

		@Override
		public void onFailure(Throwable caught)
		{
		}

		@Override
		public void onSuccess(Boolean result)
		{
			tomcatRestartPanel.setVisible(result != null && result.booleanValue());
		}
	}

	@Override
	public void onEvent(RevertChangesEvent event)
	{
		tomcatRestartPanel.setVisible(true);

		//Disable reconfigure changes button
		restartTomcatButton.setEnabled(false);

		// Updat the text
		tomcatRestartLabel.setText(RBUNDLE.changesReverted());
	}
}
