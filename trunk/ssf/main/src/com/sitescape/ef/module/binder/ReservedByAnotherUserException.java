package com.sitescape.ef.module.binder;

import com.sitescape.ef.domain.Reservable;
import com.sitescape.ef.exception.UncheckedCodedException;

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
