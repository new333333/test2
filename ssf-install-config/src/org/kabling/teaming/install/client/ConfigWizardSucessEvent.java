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
	public WizardFinishType wizFinishType;
	public interface ConfigWizardSuccessEventHandler
	{
		void onEvent(ConfigWizardSucessEvent event);
	}

	public enum WizardFinishType
	{
		LOCAL_SUCESS,
		REMOTE_SUCESS,
		FAILURE,
		UPGRADE
	}
	

	public ConfigWizardSucessEvent(WizardFinishType wizFinishType)
	{
		this.wizFinishType = wizFinishType;
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

	public WizardFinishType getWizFinishType()
	{
		return wizFinishType;
	}
}
