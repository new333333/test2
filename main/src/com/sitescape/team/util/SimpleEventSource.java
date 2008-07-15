/**
 * 
 */
package com.sitescape.team.util;

import java.util.ArrayList;
import java.util.Collection;
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
	 * Returns the concrete class which implements {@link EventSource}.
	 * 
	 * @return the concrete class which implements {@link EventSource}.
	 */
	@SuppressWarnings("unchecked")
	final protected S myself() {
		return (S) this;
	}

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

	/* (non-Javadoc)
	 * @see com.sitescape.team.util.EventSource#setListeners(java.util.Collection)
	 */
	public void setListeners(Collection<? extends EventListener<S, E>> listeners) {
		this.listeners = new ArrayList<EventListener<S,E>>(listeners);
	}
}
