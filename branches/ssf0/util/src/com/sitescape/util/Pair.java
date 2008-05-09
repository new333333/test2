/**
 * 
 */
package com.sitescape.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An immutable, generic class to store a pair of {@link Object}s.
 * 
 * @author dml
 *
 */
public class Pair<E, F> {
	private E first;
	private F second;
	
	public Pair(E first, F second) {
		if (first == null || second == null) {
			throw new IllegalArgumentException("Pairs may not be created with null components.");
		}
		this.first = first;
		this.second = second;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Pair)) {
			return false;
		}
		Pair<?, ?> rhs = (Pair<?, ?>) obj;
		return new EqualsBuilder()
			.append(this.first, rhs.first)
			.append(this.second, rhs.second)
			.isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.first)
			.append(this.second)
			.toHashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder("(")
			.append(first)
			.append(",")
			.append(second)
			.append(")")
			.toString();
	}

	/**
	 * @return the first
	 */
	public E getFirst() {
		return first;
	}

	/**
	 * @return the second
	 */
	public F getSecond() {
		return second;
	}

}
