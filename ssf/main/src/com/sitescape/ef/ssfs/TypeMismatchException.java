package com.sitescape.ef.ssfs;

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
