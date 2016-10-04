package org.kabling.teaming.install.client;

import org.kabling.teaming.install.client.i18n.AppResource;
import org.kabling.teaming.install.client.widgets.DlgBox;
import org.kabling.teaming.install.shared.InstallerConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

/**
 * Base class for Configuration page dialogs. This class extends DlgBox and has this modifications.
 * - Default error panel 
 * - Buttons are aligned to the left side
 * - Dialog does not show header close button
 * - Will read the installer.xml while loading
 * - Will save the installer.xml when the user presses ok button
 *
 */
public abstract class ConfigPageDlgBox extends DlgBox implements EditSuccessfulHandler
{
	private GetConfigInformationCallback getConfigCallback = new GetConfigInformationCallback();
	protected SaveConfigInformationCallback saveConfigCallback = new SaveConfigInformationCallback();
	private Label errorLabel;

	// Installer.xml configuration data
	protected InstallerConfig config;
	protected AppResource RBUNDLE = AppUtil.getAppResource();

	public ConfigPageDlgBox()
	{
		super(false, true, DlgButtonMode.OkCancel, true);

		// Don't show the header close button
		showHeaderCloseButton(false);

		AppUtil.getInstallService().getConfiguration(getConfigCallback);
		
		addStyleName("configPageDlgBox");
	}

	public ConfigPageDlgBox(DlgButtonMode mode )
	{
		super(false, true, mode, true);

		// Don't show the header close button
		showHeaderCloseButton(false);

		AppUtil.getInstallService().getConfiguration(getConfigCallback);
		
		addStyleName("configPageDlgBox");
	}
	
	/**
	 * After loading the installer.xml, we will call the dialog which extends this to update their UI
	 */
	public abstract void initUIWithData();

	/**
	 * Set the error message
	 */
	public void setErrorMessage(String text)
	{
		if (errorLabel == null)
		{
			errorLabel = new Label();
			errorLabel.setStyleName("errorLabel");
			getErrorPanel().add(errorLabel);
		}

		errorLabel.setText(text);
		getErrorPanel().setVisible(text != null);
	}

	/**
	 * Clear the error message
	 */
	public void clearErrorMessage()
	{
		getErrorPanel().setVisible(false);
	}

	class GetConfigInformationCallback implements AsyncCallback<InstallerConfig>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			// TODO:
		}

		@Override
		public void onSuccess(InstallerConfig result)
		{
			//Save the configuration
			config = result;
			
			//Let the implementation class initialize the UI with the data
			initUIWithData();
		}
	}

	class SaveConfigInformationCallback implements AsyncCallback<Void>
	{

		@Override
		public void onFailure(Throwable caught)
		{
			setErrorMessage(RBUNDLE.unableToSaveConfiguration());
		}

		@Override
		public void onSuccess(Void coid)
		{
			//Configuration got saved
			//Fire the event so that summary page can update the UI
			//And we will allow the restart of tomcat
			AppUtil.getEventBus().fireEvent(new ConfigModifiedEvent(true,true));
			hide();
		}
	}
}
