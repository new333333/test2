package org.kabling.teaming.install.client;

import com.google.web.bindery.event.shared.Event;

public class ConfigNextButtonEnableEvent extends Event<ConfigNextButtonEnableEvent.ConfigNextEnableEventHandler>
{

	public static final Type<ConfigNextEnableEventHandler> TYPE = new Type<ConfigNextEnableEventHandler>();
	private boolean enable;
	
	public interface ConfigNextEnableEventHandler
	{

		void onEvent(ConfigNextButtonEnableEvent event);
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public boolean isEnabled()
	{
		return enable;
	}

	/**
	 * 
	 * @param navType
	 *            the nav type
	 */
	public ConfigNextButtonEnableEvent(boolean enable)
	{
		this.enable = enable;
	}

	@Override
	protected void dispatch(ConfigNextButtonEnableEvent.ConfigNextEnableEventHandler arg0)
	{
		arg0.onEvent(this);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<ConfigNextEnableEventHandler> getAssociatedType()
	{
		return TYPE;
	}
}
