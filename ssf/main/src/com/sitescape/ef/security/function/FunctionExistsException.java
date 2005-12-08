package com.sitescape.ef.security.function;

import com.sitescape.ef.exception.UncheckedCodedException;

public class FunctionExistsException extends UncheckedCodedException {
		private static final String FunctionExistsException_ErrorCode = "errorcode.function.exists";

		public FunctionExistsException() {
	        super(FunctionExistsException_ErrorCode, new Object[]{""});
		}

		public FunctionExistsException(String function) {
			super(FunctionExistsException_ErrorCode, new Object[]{function});
		}

		public FunctionExistsException(String function, Throwable cause) {
			super(FunctionExistsException_ErrorCode, new Object[]{function});
		}

		public FunctionExistsException(Throwable cause) {
			super(FunctionExistsException_ErrorCode, new Object[]{cause});
		}
}
