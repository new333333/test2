package org.kabling.teaming.install.client;

import com.google.web.bindery.event.shared.Event;

/**
 * Event to let know that the installer.xml has been modified. This event is used to capture if we need 
 * to restart the tomcat server and for updating the configuration summary page
 * @author Rajesh
 *
 */
public class ConfigWizardSucessEvent extends Event<ConfigWizardSucessEvent.ConfigWizardSuccessEventHandler>
{

	public static final Type<ConfigWizardSuccessEventHandler> TYPE = new Type<ConfigWizardSuccessEventHandler>();
	private boolean sucess;
	public interface ConfigWizardSuccessEventHandler
	{
		void onEvent(ConfigWizardSucessEvent event);
	}

	/**
	 * Get the installer.xml modification state
	 * @return - true if it in dirty state
	 */
	public boolean isSucess()
	{
		return sucess;
	}

	public ConfigWizardSucessEvent(boolean sucess)
	{
		this.sucess = sucess;
	}

	@Override
	protected void dispatch(ConfigWizardSucessEvent.ConfigWizardSuccessEventHandler arg0)
	{
		arg0.onEvent(this);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<ConfigWizardSuccessEventHandler> getAssociatedType()
	{
		return TYPE;
	}

}
