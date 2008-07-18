/**
 * 
 */
package com.sitescape.team.util;

import com.sitescape.team.util.CollectionUtil.Func0;
import com.sitescape.team.util.CollectionUtil.Func1;

/**
 * A class which represents an optional value. 
 * @author dml
 *
 */
public abstract class Maybe<E> {
	
	public static <E> Maybe<E> maybe(E e) {
		return e == null? new Nothing<E>() : new Some<E>(e);
	}
	
	/**
	 * Returns true if this object represents an undefined value. 
	 * @return whether a call to {@link #or} will return the alternative value.
	 */
	public abstract boolean isEmpty();
	/**
	 * Returns true if this object represents a defined value.
	 * @return whether a call to {@link #or} will not return the alternative value.
	 */
	public boolean isDefined() {
		return !isEmpty();
	}
	
	/**
	 * If this <code>Maybe</code> is defined, return it. Otherwise, return an
	 * alternative value.
	 * 
	 * @param alternative - a default value
	 * @return the value of this or an alternative
	 */
	public abstract E or (E alternative);
	
	/**
	 * If this object is defined, return it. Otherwise, return the result of
	 * evaluating <code>alternative</code>.
	 * 
	 * @param alternative -
	 *            a computation whose result is to be returned if this is
	 *            undefined
	 * @return the value of this or the the result of evaluating
	 *         <code>alternative</code>
	 */
	public abstract E orElse(Func0<E> alternative);
	
	/**
	 * If this object is defined, return the <code>Maybe</code>-wrapped version.  Otherwise, return the result of {@link #maybe(Object)} for <code>alternative</code>
	 * @param alternative - maybe an <code>E</code>
	 * @return this or <code>Maybe</code> the <code>alternative</code>
	 */
	public abstract Maybe<E> orMaybe(E alternative);
	
	/**
	 * If this object is defined, run a {@link Func1} and return its value.  Otherwise, return a default value. 
	 * @param <V> - the type to be returned
	 * @param then - the function to be run if this value is defined.
	 * @param otherwise - a default return value
	 * @return the result of running <code>then</code> or <code>otherwise</code>
	 */
	public abstract <V> V and(Func1<E, V> then, V otherwise);
	
	
	public static class Nothing<E> extends Maybe<E> {
		private Nothing() {}
		@Override
		public E or(E alternative) {
			return alternative;
		}
		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public E orElse(Func0<E> alternative) {
			return alternative.apply();
		}
		@Override
		public <V> V and(Func1<E, V> then, V otherwise) {
			return otherwise;
		}
		@Override
		public Maybe<E> orMaybe(E alternative) {
			return maybe(alternative);
		}
	}
	
	public static class Some<E> extends Maybe<E> {
		
		private E e; 
		private Some(E e) {
			this.e = e;
		}
		@Override
		public E or(E alternative) {
			return e;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public E orElse(Func0<E> alternative) {
			return e;
		}
		@Override
		public <V> V and(Func1<E, V> then, V otherwise) {
			return then.apply(e);
		}
		@Override
		public Maybe<E> orMaybe(E alternative) {
			return this;
		}
	}

}
