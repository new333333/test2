package org.kabling.teaming.install.client;

import com.google.web.bindery.event.shared.Event;

/**
 * Event to let know if the initial configuration wizard finish button should be enabled or disabled
 *
 */
public class ConfigFinishEnableEvent extends Event<ConfigFinishEnableEvent.ConfigFinishEnableEventHandler>
{

	public static final Type<ConfigFinishEnableEventHandler> TYPE = new Type<ConfigFinishEnableEventHandler>();
	private boolean enable;

	public interface ConfigFinishEnableEventHandler
	{

		void onEvent(ConfigFinishEnableEvent event);
	}

	/**
	 * Get the status
	 * 
	 * @return true if finish button should be enabled
	 */
	public boolean isEnabled()
	{
		return enable;
	}

	/**
	 * Create a event with the status of how the finish button should be
	 * @param enable
	 */
	public ConfigFinishEnableEvent(boolean enable)
	{
		this.enable = enable;
	}

	@Override
	protected void dispatch(ConfigFinishEnableEvent.ConfigFinishEnableEventHandler arg0)
	{
		arg0.onEvent(this);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<ConfigFinishEnableEventHandler> getAssociatedType()
	{
		return TYPE;
	}
}
