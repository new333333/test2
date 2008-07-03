/**
 * 
 */
package com.sitescape.team.module.extension.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;

import com.sitescape.team.module.extension.ExtensionDeployNotifier;
import com.sitescape.team.util.EventListener;
import com.sitescape.team.util.EventSource;
import com.sitescape.team.util.SimpleEventSource;

/**
 * @author dml
 * 
 * Wraps another {@link EventSource} for {@link File} events to provide the
 * {@link ExtensionDeployNotifier} interface.
 * 
 */
public class WrappingExtensionDeployNotifier<S extends EventSource<S, File>>
		extends SimpleEventSource<WrappingExtensionDeployNotifier<S>, File>
		implements ExtensionDeployNotifier<WrappingExtensionDeployNotifier<S>>,
		EventListener<S, File> {

	@Override
	protected WrappingExtensionDeployNotifier<S> myself() {
		return this;
	}

	public void onNotification(S source, File event) {
		propagate(event);
	}

	@Required
	public void setEventSource(S eventSource) {
		eventSource.register(this);
	}

}
