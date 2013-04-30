package org.kabling.teaming.install.client.leftnav;

import com.google.web.bindery.event.shared.Event;

public class LeftNavSelectionEvent extends Event<LeftNavSelectionEvent.LeftNavSelectEventHandler>
{

	public static final Type<LeftNavSelectEventHandler> TYPE = new Type<LeftNavSelectEventHandler>();
	private LeftNavItemType navType;
	
	/**
	 * The Interface LeftNavSelectEventHandler.
	 */
	public interface LeftNavSelectEventHandler
	{

		void onEvent(LeftNavSelectionEvent event);
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public LeftNavItemType getType()
	{
		return navType;
	}

	/**
	 * Instantiates a new left nav select event.
	 * 
	 * @param navType
	 *            the nav type
	 */
	public LeftNavSelectionEvent(LeftNavItemType navType)
	{
		this.navType = navType;
	}

	@Override
	protected void dispatch(LeftNavSelectionEvent.LeftNavSelectEventHandler arg0)
	{
		arg0.onEvent(this);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<LeftNavSelectEventHandler> getAssociatedType()
	{
		return TYPE;
	}
}
