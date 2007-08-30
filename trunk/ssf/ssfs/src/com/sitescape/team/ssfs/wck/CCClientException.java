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
package com.sitescape.team.ssfs.wck;

public class CCClientException extends RuntimeException {

	private boolean warning = false;
	
	public CCClientException() {
	}
	
	public CCClientException(String msg) {
		super(msg);
	}
	
	public CCClientException(String msg, boolean warning) {
		this(msg);
		this.warning = warning;
	}
	
	public boolean isWarning() {
		return warning;
	}
}
