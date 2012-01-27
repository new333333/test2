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
package org.kablink.teaming.module.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.exception.ApiErrorCodeSupport;
import org.kablink.teaming.exception.HttpStatusCodeSupport;
import org.kablink.teaming.remoting.ApiErrorCode;
import org.kablink.teaming.util.NLT;


public class FilesErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Problem> problems;
	
	public FilesErrors() {
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
		public static int OTHER_PROBLEM								= 0;
		public static int PROBLEM_FILTERING							= 1;
		public static int PROBLEM_STORING_PRIMARY_FILE				= 2;
		public static int PROBLEM_DELETING_PRIMARY_FILE				= 3;
		public static int PROBLEM_CANCELING_LOCK					= 4;
		public static int PROBLEM_FILE_EXISTS						= 5;
		public static int PROBLEM_ARCHIVING							= 6;
		public static int PROBLEM_MIRRORED_FILE_IN_REGULAR_FOLDER	= 7;
		public static int PROBLEM_MIRRORED_FILE_MULTIPLE			= 8;
		public static int PROBLEM_REGULAR_FILE_IN_MIRRORED_FOLDER   = 9;
		public static int PROBLEM_MIRRORED_FILE_READONLY_DRIVER		= 10;
		public static int PROBLEM_ENCRYPTION_FAILED					= 11;
		
		// Message codes corresponding to each problem type.
		public static String[] typeCodes = {
			"file.error.other",
			"file.error.filtering",
			"file.error.storing.primary.file",
			"file.error.deleting.primary.file",
			"file.error.canceling.lock",
			"entry.duplicateFileInLibrary",
			"file.error.archiving",
			"file.error.mirrored.file.in.regular.folder",
			"file.error.mirrored.file.multiple",
			"file.error.regular.file.in.mirrored.folder",
			"file.error.mirrored.file.readonly.driver",
			"file.error.encryption.failed"
		};
		
		// API error codes corresponding to each problem type.
		public static ApiErrorCode[] apiErrorCodes = {
			ApiErrorCode.GENERAL_ERROR,
			ApiErrorCode.FILE_FILTERING_ERROR,
			ApiErrorCode.FILE_WRITE_FAILED,
			ApiErrorCode.FILE_DELETE_FAILED,
			ApiErrorCode.FILE_LOCK_CANCELLATION_FAILED,
			ApiErrorCode.FILE_EXISTS,
			ApiErrorCode.FILE_ARCHIVE_FAILED,
			ApiErrorCode.MIRRORED_FILE_IN_REGULAR_FOLDER,
			ApiErrorCode.MIRRORED_FILE_MULTIPLE,
			ApiErrorCode.REGULAR_FILE_IN_MIRRORED_FOLDER,
			ApiErrorCode.MIRRORED_FILE_READONLY_DRIVER,
			ApiErrorCode.FILE_ENCRYPTION_FAILED
		};
		
		// HTTP status codes corresponding to each problem type.
		public static int[] httpStatusCodes = {
			500, // internal server error
			403, // forbidden
			500,
			500,
			500,
			409, // conflict
			500,
			400, // bad request
			400,
			400,
			400,
			500
		};

		private String repositoryName; // required
		private String fileName; // required
		private int type; // required - one of the constants defined above
		// at most one of the following two is set.
		private Exception exception; // may be null
		
		public Problem(String repositoryName, String fileName, 
				int type) {
			this.repositoryName = repositoryName;
			this.fileName = fileName;
			this.type = type;
		}
		
		public Problem(String repositoryName, String fileName, 
				int type, Exception exception) {
			this(repositoryName, fileName, type);
			this.exception = exception;
		}
		
		public Problem(Exception exception) {
			this.repositoryName = null;
			this.fileName = null;
			this.type = -1;
			this.exception = exception;
		}
		
		public Exception getException() {
			return exception; // may be null
		}
		
		public String getFileName() {
			return fileName;
		}
		
		public int getType() {
			return type;
		}
		
		public String getTypeCode() {
			return typeCodes[type];
		}
		
		public String getRepositoryName() {
			return repositoryName;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (this.fileName == null && this.repositoryName == null && this.type == -1 && this.exception != null) {
				sb.append(getException().getLocalizedMessage());
			} else {
				String typeCodeError = NLT.get(getTypeCode()).trim();
				sb.append(typeCodeError);
				if (typeCodeError.lastIndexOf(":") == -1 || 
						typeCodeError.lastIndexOf(":") < typeCodeError.length() - 1) sb.append(":");
				sb.append(" ");
				sb.append(getFileName());
				sb.append(" ");
				if (getRepositoryName() != null && !getRepositoryName().equals("")) {
					sb.append("(")
					.append(getRepositoryName())
					.append(")");
				}
				if(getException() != null) {
					sb.append(" - ");
					sb.append(getException().getLocalizedMessage());
				}
			}
			return sb.toString();
		}
		
		/* (non-Javadoc)
		 * @see org.kablink.teaming.exception.HttpStatusCodeSupport#getHttpStatusCode()
		 */
		@Override
		public int getHttpStatusCode() {
			return httpStatusCodes[type];
		}

		/* (non-Javadoc)
		 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
		 */
		@Override
		public ApiErrorCode getApiErrorCode() {
			return apiErrorCodes[type];
		}
	}

}
