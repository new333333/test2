package com.sitescape.ef.domain;

import com.sitescape.ef.exception.UncheckedCodedException;

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