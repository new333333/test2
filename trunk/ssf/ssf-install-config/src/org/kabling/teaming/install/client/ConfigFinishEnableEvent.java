package org.kabling.teaming.install.client;

import com.google.web.bindery.event.shared.Event;

public class ConfigFinishEnableEvent extends Event<ConfigFinishEnableEvent.ConfigFinishEnableEventHandler>
{

	public static final Type<ConfigFinishEnableEventHandler> TYPE = new Type<ConfigFinishEnableEventHandler>();
	private boolean enable;
	
	public interface ConfigFinishEnableEventHandler
	{

		void onEvent(ConfigFinishEnableEvent event);
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
