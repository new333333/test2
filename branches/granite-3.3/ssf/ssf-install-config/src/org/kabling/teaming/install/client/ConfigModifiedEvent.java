package org.kabling.teaming.install.client;

import com.google.web.bindery.event.shared.Event;

/**
 * Event to let know that the installer.xml has been modified. This event is used to capture if we need 
 * to restart the tomcat server and for updating the configuration summary page
 * @author Rajesh
 *
 */
public class ConfigModifiedEvent extends Event<ConfigModifiedEvent.ConfigModifiedEventHandler>
{

	public static final Type<ConfigModifiedEventHandler> TYPE = new Type<ConfigModifiedEventHandler>();
	private boolean modified;
	private boolean requireTomcatRestart;
	public interface ConfigModifiedEventHandler
	{

		void onEvent(ConfigModifiedEvent event);
	}

	/**
	 * Get the installer.xml modification state
	 * @return - true if it in dirty state
	 */
	public boolean isModified()
	{
		return modified;
	}

	public ConfigModifiedEvent(boolean enable,boolean requireTomcatRestart)
	{
		this.modified = enable;
		this.requireTomcatRestart = requireTomcatRestart;
	}

	@Override
	protected void dispatch(ConfigModifiedEvent.ConfigModifiedEventHandler arg0)
	{
		arg0.onEvent(this);
	}

	@Override
	public com.google.web.bindery.event.shared.Event.Type<ConfigModifiedEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	public boolean isRequireTomcatRestart()
	{
		return requireTomcatRestart;
	}

	public void setRequireTomcatRestart(boolean requireTomcatRestart)
	{
		this.requireTomcatRestart = requireTomcatRestart;
	}
}
