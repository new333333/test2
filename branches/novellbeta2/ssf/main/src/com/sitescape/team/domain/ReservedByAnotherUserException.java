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
package com.sitescape.team.domain;

import com.sitescape.team.exception.UncheckedCodedException;

public class ReservedByAnotherUserException extends UncheckedCodedException {
	
	private static final String ReservedByAnotherUserException_ErrorCode = "errorcode.reserved.by.another.user";

	private Reservable reservable;
	
	public ReservedByAnotherUserException(Reservable reservable) {
		super(ReservedByAnotherUserException_ErrorCode, new Object[] { 
				reservable.getId(), reservable.getReservation().getPrincipal() });
		
		this.reservable = reservable;
	}

	public Reservable getReservable() {
		return reservable;
	}
}
