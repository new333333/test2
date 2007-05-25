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

/**
  *
 * @author  Peter Hurley
 * @version $Revision: 1.0 $
 *
 */
public class DefinitionInvalidOperation extends UncheckedCodedException {
	private static final String DefinitionInvalidOperation_ErrorCode = "errorcode.definition.operation.invalid";

	public DefinitionInvalidOperation() {
        super(DefinitionInvalidOperation_ErrorCode, new Object[]{""});
	}

	public DefinitionInvalidOperation(String msg) {
		super(DefinitionInvalidOperation_ErrorCode, new Object[]{msg});
	}

	public DefinitionInvalidOperation(String msg, Throwable cause) {
		super(DefinitionInvalidOperation_ErrorCode, new Object[]{msg, cause});
	}

	public DefinitionInvalidOperation(Throwable cause) {
		super(DefinitionInvalidOperation_ErrorCode, new Object[]{cause});
	}

}