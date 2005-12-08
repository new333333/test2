package com.sitescape.ef.module.sample;

import com.sitescape.ef.exception.UncheckedCodedException;

public class SalaryMaxedOutException extends UncheckedCodedException {
	
	private static final String SalaryMaxedOutException_ErrorCode = "errorcode.salary.maxed.out";
	
	public SalaryMaxedOutException(String employeeName, int maxAmount) {
		super(SalaryMaxedOutException_ErrorCode, new Object[]{employeeName, new Integer(maxAmount)});
	}
}
