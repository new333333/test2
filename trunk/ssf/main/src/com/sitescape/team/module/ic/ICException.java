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
package com.sitescape.team.module.ic;

public class ICException extends Exception {

	/**
	 * There was exception, look at <code>cause</code>.
	 */
	public static final int RPC_EXCEPTION = 0;
	
	/**
	 * There was error returned by IC, look at <code>errorCode</code>.
	 */
	public static final int RPC_ERROR = 1;
	
	private int type = RPC_EXCEPTION;
	
	private Integer errorCode = null;

	public ICException(Integer errorCode) {
		super(Integer.toString(errorCode));
		this.errorCode = errorCode;
		this.type = RPC_ERROR;
	}
	
	public ICException(Throwable cause) {
		super(cause);
		this.type = RPC_EXCEPTION;		
	}
	
	public Integer getErrorCode() {
		return errorCode;
	}

	public int getType() {
		return type;
	}

}
