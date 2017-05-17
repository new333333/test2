/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.binder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.NLT;
import org.kablink.util.HttpStatusCodeSupport;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.api.ApiErrorCodeSupport;


public class EntryDataErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(EntryDataErrors.class);

	private List<Problem> problems;
	
	public EntryDataErrors() {
		this.problems = new ArrayList();
	}
	
	public void addProblem(Problem problem) {
		problems.add(problem);
	}
	
	public List<Problem> getProblems() {
		return problems;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < problems.size(); i++) {
			if(i > 0)
				sb.append("\n");
			sb.append(((Problem) problems.get(i)).toString());
		}
		return sb.toString();
	}
	
	public static class Problem implements ApiErrorCodeSupport, HttpStatusCodeSupport, Serializable {
		private static final long serialVersionUID = 1L;
		
		// Problem types
		public static int GENERAL_PROBLEM = 0;
		public static int INVALID_HTML = 1;
		public static int INVALID_CAPTCHA_RESPONSE = 2;
		
		private int type; // required - one of the constants defined above
		
		// Message codes corresponding to each problem type.
		public static String[] typeCodes = {
			"general.error.anErrorOccurred",
			"general.error.invalidHTML",
			"captcha.error.invalidResponse"
		};
		
		// API error codes corresponding to each problem type.
		public static ApiErrorCode[] apiErrorCodes = {
			ApiErrorCode.SERVER_ERROR,
			ApiErrorCode.INVALID_HTML,
			ApiErrorCode.INVALID_CAPTCHA_RESPONSE
		};
		
		// HTTP status codes corresponding to each problem type.
		public static int[] httpStatusCodes = {
			500, // internal server error
			400, // bad request			
			400, // bad request			
		};
		
		private Exception exception; // may be null
		
		public Problem(int type, Exception e) {
			this.type = type;
			this.exception = e;
			if(e != null)
				logger.error("Entry data error (type=" + type + ")", e);
			else
				logger.error("Entry data error (type=" + type + ")");
		}

		public Exception getException() {
			return exception; // may be null
		}
		
		public int getType() {
			return type;
		}
		
		public String getTypeCode() {
			return typeCodes[type];
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			String typeCodeError = NLT.get(getTypeCode()).trim();
			sb.append(typeCodeError);
			return sb.toString();
		}

		/* (non-Javadoc)
		 * @see org.kablink.teaming.exception.HttpStatusCodeSupport#getHttpStatusCode()
		 */
		@Override
		public int getHttpStatusCode() {
			// Exception has precedence
			if(exception != null && exception instanceof HttpStatusCodeSupport)
				return ((HttpStatusCodeSupport) exception).getHttpStatusCode();
			else 
				return httpStatusCodes[type];
		}

		/* (non-Javadoc)
		 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
		 */
		@Override
		public ApiErrorCode getApiErrorCode() {
			// Exception has precedence
			if(exception != null && exception instanceof ApiErrorCodeSupport)
				return ((ApiErrorCodeSupport) exception).getApiErrorCode();
			else
				return apiErrorCodes[type];
		}

	}

}
