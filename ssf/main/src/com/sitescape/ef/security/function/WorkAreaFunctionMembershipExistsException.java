package com.sitescape.ef.security.function;

import com.sitescape.ef.exception.UncheckedCodedException;

public class WorkAreaFunctionMembershipExistsException extends UncheckedCodedException {
		private static final String WorkAreaFunctionMembershipExistsException_ErrorCode = "errorcode.workareafunctionmembership.exists";

		public WorkAreaFunctionMembershipExistsException(WorkAreaFunctionMembership workAreaFunctionMembership) {
			super(WorkAreaFunctionMembershipExistsException_ErrorCode, 
					new Object[]{workAreaFunctionMembership.getWorkAreaId(), 
					workAreaFunctionMembership.getFunctionId()});
		}
		
		public WorkAreaFunctionMembershipExistsException(Long workAreaId, Long functionId) {
			super(WorkAreaFunctionMembershipExistsException_ErrorCode, new Object[]{workAreaId, functionId});
		}
}
