package org.kabling.teaming.install.client;

import com.google.web.bindery.event.shared.Event;

/**
 * Event to let know that the installer.xml has been modified. This event is used for updating the configuration summary page
 * @author Rajesh
 *
 */
public class RevertChangesEvent extends Event<RevertChangesEvent.RevertChangesEventHandler>
{

	public static final Type<RevertChangesEventHandler> TYPE = new Type<RevertChangesEventHandler>();
	private boolean modified;
	public interface RevertChangesEventHandler
	{

		void onEvent(RevertChangesEvent event);
	}

	/**
	 * Get the installer.xml modification state
	 * @return - true if it in dirty state
	 */
	public boolean isModified()
	{
		return modified;
	}

	public RevertChangesEvent(boolean enable)
	{
		this.modified = enable;
	}

	@Override
	protected void dispatch(RevertChangesEvent.RevertChangesEventHandler arg0)
	{
		arg0.onEvent(this);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<RevertChangesEventHandler> getAssociatedType()
	{
		return TYPE;
	}
}
