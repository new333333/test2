/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.core.io.AbstractResource;

/**
 * Class copied from Spring 1.2.9. After update to new Spring version (at least 1.2.3) please removed this class and use this one from Spring.
 * 
 * 
 * @author spring
 *
 */
public class ByteArrayResource extends AbstractResource {

	private final byte[] byteArray;

	private final String description;


	/**
	 * Create a new ByteArrayResource.
	 * @param byteArray the byte array to wrap
	 */
	public ByteArrayResource(byte[] byteArray) {
		this(byteArray, "resource loaded from byte array");
	}

	/**
	 * Create a new ByteArrayResource.
	 * @param byteArray the byte array to wrap
	 * @param description where the byte array comes from
	 */
	public ByteArrayResource(byte[] byteArray, String description) {
		if (byteArray == null) {
			throw new IllegalArgumentException("Byte array must not be null");
		}
		this.byteArray = byteArray;
		this.description = description;
	}

	/**
	 * Return the underlying byte array.
	 */
	public final byte[] getByteArray() {
		return byteArray;
	}


	/**
	 * This implementation always returns <code>true</code>.
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * This implementation returns a ByteArrayInputStream for the
	 * underlying byte array.
	 * @see java.io.ByteArrayInputStream
	 */
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.byteArray);
	}

	/**
	 * This implementation returns the passed-in description, if any.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * This implementation compares the underlying byte array.
	 * @see java.util.Arrays#equals(byte[], byte[])
	 */
	public boolean equals(Object obj) {
		return (obj == this ||
		    (obj instanceof ByteArrayResource && Arrays.equals(((ByteArrayResource) obj).byteArray, this.byteArray)));
	}

	/**
	 * This implementation returns the hash code of the underlying byte array.
	 */
	public int hashCode() {
		return byte[].class.hashCode() * 29 * this.byteArray.length;
	}

}
