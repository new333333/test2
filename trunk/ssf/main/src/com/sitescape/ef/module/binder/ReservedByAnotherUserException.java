package com.sitescape.ef.module.binder;

import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.exception.UncheckedCodedException;

public class ReservedByAnotherUserException extends UncheckedCodedException {
	
	private static final String ReservedByAnotherUserException_ErrorCode = "errorcode.reserved.by.another.user";

	private DefinableEntity entity;
	private Principal reservationOwner;
	
	public ReservedByAnotherUserException(DefinableEntity entity, 
			Principal reservationOwner) {
		super(ReservedByAnotherUserException_ErrorCode, new Object[] { 
				entity.getId(), reservationOwner.getName() });
		
		this.entity = entity;
		this.reservationOwner = reservationOwner;
	}

	public DefinableEntity getEntity() {
		return entity;
	}

	public Principal getReservationOwner() {
		return reservationOwner;
	}

}
