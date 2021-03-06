/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.antivirus;

import java.io.Serializable;

import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.api.ApiErrorCodeSupport;

/**
 * @author Jong
 * 
 * This class encapsulates information about a file that failed one or more tests
 * performed by the anti-virus scanning software.
 * <p>
 * This object must NOT be used to signal an environmental or configuration/setup 
 * error such as inaccessible or mis-configured virus scanner, network problem, 
 * I/O problem, timeout, invalid credentials, etc.
 * The use of this object implies that the virus scanner successfully ran to completion
 * and performed all tests on the file it was configured to do, and it determined 
 * according to the test results and its policy restrictions that the file should
 * be rejected.
 * <p>
 * This class is used in conjunction with <code>VirusDetectedException</code>.
 */
public class VirusDetectedError implements ApiErrorCodeSupport, Serializable {

	/**
	 * Enum for error type
	 *
	 */
	public enum Type {
		/**
		 * The file is infected with virus.
		 */
		Virus,
		/**
		 * The file violates some policy restrictions.
		 */
		PolicyRestrictionViolation,
		/**
		 * Other error (catch-all), usually vendor specific error
		 */
		Other
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// name of file - required
	private String fileName;
	
	// error type - required
	private Type type;
	
	// error message - optional
	private String message;
	
	public VirusDetectedError(String fileName, Type type, String message) {
		this.fileName = fileName;
		this.type = type;
		this.message = message;
	}
	
	public String getFileName() {
		return fileName;
	}
	public String getMessage() {
		return message;
	}
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("filename=[")
		.append(fileName)
		.append("], type=[")
		.append(type.name())
		.append("], message=[")
		.append((message != null)? message:"")
		.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.kablink.util.api.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		switch(this.type) {
		case Virus:
			return ApiErrorCode.FILE_AV_VIRUS;
		case PolicyRestrictionViolation:
			return ApiErrorCode.FILE_AV_POLICY_VIOLATION;
		case Other:
		default: // This should never happen
			return ApiErrorCode.FILE_AV_OTHER;
		}
	}
}
