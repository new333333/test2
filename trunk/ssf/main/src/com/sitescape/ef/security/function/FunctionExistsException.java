package com.sitescape.ef.security.function;

import com.sitescape.ef.exception.UncheckedCodedException;

public class FunctionExistsException extends UncheckedCodedException {
		private static final String FunctionExists_ErrorCode = "error.function.exists";

		public FunctionExistsException() {
	        super(FunctionExists_ErrorCode, new Object[]{""});
		}

		public FunctionExistsException(String function) {
			super(FunctionExists_ErrorCode, new Object[]{function});
		}

		public FunctionExistsException(String function, Throwable cause) {
			super(FunctionExists_ErrorCode, new Object[]{function});
		}

		public FunctionExistsException(Throwable cause) {
			super(FunctionExists_ErrorCode, new Object[]{cause});
		}
}
