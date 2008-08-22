/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sitescape.team.util.NLT;

public class FilesErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List problems;
	
	public FilesErrors() {
		this.problems = new ArrayList();
	}
	
	public void addProblem(Problem problem) {
		problems.add(problem);
	}
	
	public List getProblems() {
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
	
	public static class Problem implements Serializable {
		private static final long serialVersionUID = 1L;
		
		// Problem types
		public static int OTHER_PROBLEM								= 0;
		public static int PROBLEM_FILTERING							= 1;
		public static int PROBLEM_STORING_PRIMARY_FILE				= 2;
		public static int PROBLEM_GENERATING_SCALED_FILE			= 3;
		public static int PROBLEM_STORING_SCALED_FILE				= 4;
		public static int PROBLEM_GENERATING_THUMBNAIL_FILE			= 5;
		public static int PROBLEM_STORING_THUMBNAIL_FILE			= 6;
		public static int PROBLEM_DELETING_PRIMARY_FILE				= 7;
		public static int PROBLEM_DELETING_SCALED_FILE				= 8;
		public static int PROBLEM_DELETING_THUMBNAIL_FILE			= 9;
		public static int PROBLEM_CANCELING_LOCK					= 10;
		public static int PROBLEM_LOCKED_BY_ANOTHER_USER			= 11;
		public static int PROBLEM_RESERVED_BY_ANOTHER_USER  		= 12;
		public static int PROBLEM_FILE_EXISTS						= 13;
		public static int PROBLEM_ARCHIVING							= 14;
		public static int PROBLEM_MIRRORED_FILE_IN_REGULAR_FOLDER	= 15;
		public static int PROBLEM_MIRRORED_FILE_MULTIPLE			= 16;
		public static int PROBLEM_REGULAR_FILE_IN_MIRRORED_FOLDER   = 17;
		public static int PROBLEM_MIRRORED_FILE_READONLY_DRIVER		= 18;
		
		// Message codes corresponding to each problem type.
		public static String[] typeCodes = {
			"file.error.other",
			"file.error.filtering",
			"file.error.storing.primary.file",
			"file.error.generating.scaled.file",
			"file.error.storing.scaled.file",
			"file.error.generating.thumbnail.file",
			"file.error.storing.thumbnail.file",
			"file.error.deleting.primary.file",
			"file.error.deleting.scaled.file",
			"file.error.deleting.thumbnail.file",
			"file.error.canceling.lock",
			"file.error.locked.by.another.user",
			"file.error.reserved.by.another.user",
			"entry.duplicateFileInLibrary",
			"file.error.archiving",
			"file.error.mirrored.file.in.regular.folder",
			"file.error.mirrored.file.multiple",
			"file.error.regular.file.in.mirrored.folder",
			"file.error.mirrored.file.readonly.driver"
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
			return sb.toString();
		}
	}

}
