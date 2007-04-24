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
package com.sitescape.team.ssfs;

/**
 * Thrown to indicate that the actual type of the object 
 * (either folder or file) does not match the expectation. 
 * 
 * @author jong
 *
 */
public class TypeMismatchException extends RuntimeException {

	public TypeMismatchException() {
	}
	
	public TypeMismatchException(String msg) {
		super(msg);
	}
}
