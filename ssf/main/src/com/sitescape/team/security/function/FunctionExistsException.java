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
package com.sitescape.team.security.function;

import com.sitescape.team.exception.UncheckedCodedException;

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
