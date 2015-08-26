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

import java.util.List;

import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.VibeRuntimeException;
import org.kablink.util.api.ApiErrorCode;

/**
 * @author Jong
 *
 * This exception is thrown to indicate that one or more of the input files is
 * rejected by the anti-virus scanner.
 * <p>
 * This object must NOT be used to signal an environmental or configuration/setup 
 * error such as inaccessible or mis-configured virus scanner, network problem, 
 * I/O problem, timeout, invalid credentials, etc.
 * The use of this object implies that the virus scanner successfully ran to completion
 * and performed all tests on the file it was configured to do, and it determined 
 * according to the test results and its policy restrictions that the file should
 * be rejected.
 * <p>
 * This class is used in conjunction with <code>VirusDetectedError</code>.
 */
public class VirusDetectedException extends VibeRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<VirusDetectedError> errors;
	
	public VirusDetectedException(List<VirusDetectedError> errors) {
		if(errors == null || errors.size() == 0)
			throw new IllegalArgumentException("There is no error");
		this.errors = errors;
	}
	
	public List<VirusDetectedError> getErrors() {
		return errors;
	}
	
	@Override
	public String getMessage() {
		StringBuffer sb = new StringBuffer("Number of input files that either are infected or violate policy restrictions is " + errors.size() + "\n");
		for(int i = 0; i < errors.size(); i++) {
			if(i > 0)
				sb.append("\n");
			sb.append("(");
			sb.append(i+1);
			sb.append(") ");
			sb.append(errors.get(i).toString());
		}
		return sb.toString();
	}
	
	@Override
	public String getLocalizedMessage() {
		List<String> errorStrings = MiscUtil.getLocalizedVirusDetectedErrorStrings(this.errors);
		return MiscUtil.getSeparatedErrorList(errorStrings, "\n");
	}

	/* (non-Javadoc)
	 * @see org.kablink.util.HttpStatusCodeSupport#getHttpStatusCode()
	 */
	@Override
	public int getHttpStatusCode() {
		// According to the latest RFC 7231, the HTTP status code 400 Bad Request
		// is defined more broadly than before as:
		// The 400 (Bad Request) status code indicates that the server cannot or
		// will not process the request due to something that is perceived to be
		// a client error (e.g., malformed request syntax, invalid request
		// message framing, or deceptive request routine).
		// So, this is the right code to return under this circumstances.
		return 400;
	}

	/* (non-Javadoc)
	 * @see org.kablink.util.api.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.ANTIVIRUS_DETECTED;
	}

}
