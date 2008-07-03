/**
 * 
 */
package com.sitescape.team.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dml
 * 
 * A simple implementation of {@link EventSource}.
 * 
 * @param <S> -
 *            the type of the {@link EventSource}
 * @param <E> -
 *            the type of events generated
 */
public abstract class SimpleEventSource<S extends EventSource<S, E>, E>
		implements EventSource<S, E> {

	private List<EventListener<S, E>> listeners = new ArrayList<EventListener<S, E>>();

	/**
	 * Should return the concrete class which implements {@link EventSource}.
	 * Typically, this method is implemented by:
	 * <p>
	 * <code>return this;</code>
	 * </p>
	 * 
	 * @return the concrete class which implements {@link EventSource}.
	 */
	abstract protected S myself();

	/**
	 * Notifies all {@link EventListener}s of the specified <code>E</code>
	 * @param event - the event to send to all {@link EventListener}s
	 */
	protected void propagate(E event) {
		for (EventListener<S, E> l : listeners) {
			l.onNotification(myself(), event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitescape.team.util.EventSource#register(com.sitescape.team.util.EventListener)
	 */
	public void register(EventListener<S, E> listener) {
		listeners.add(listener);
	}

}
