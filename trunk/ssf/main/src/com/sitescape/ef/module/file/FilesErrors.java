package com.sitescape.ef.module.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sitescape.ef.util.NLT;

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
		
		public static int OTHER_PROBLEM						= 0;
		public static int PROBLEM_FILTERING					= 1;
		public static int PROBLEM_STORING_PRIMARY_FILE		= 2;
		public static int PROBLEM_GENERATING_SCALED_FILE	= 3;
		public static int PROBLEM_STORING_SCALED_FILE		= 4;
		public static int PROBLEM_GENERATING_THUMBNAIL_FILE	= 5;
		public static int PROBLEM_STORING_THUMBNAIL_FILE	= 6;
		public static int PROBLEM_DELETING_PRIMARY_FILE		= 7;
		public static int PROBLEM_DELETING_SCALED_FILE		= 8;
		public static int PROBLEM_DELETING_THUMBNAIL_FILE	= 9;
		public static int PROBLEM_CANCELING_LOCK			= 10;
		public static int PROBLEM_LOCKED_BY_ANOTHER_USER	= 11;
		public static int PROBLEM_RESERVED_BY_ANOTHER_USER  = 12;
		public static int PROBLEM_FILE_EXISTS				= 13;
		public static int PROBLEM_ARCHIVING					= 14;
		
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
			"file.error.file.exists",
			"file.error.archiving"
		};
		
		private String repositoryName;
		private String fileName;
		private int type; // one of the constants defined above
		private Exception exception;
		
		public Problem(String repositoryName, String fileName, 
				int type, Exception exception) {
			this.repositoryName = repositoryName;
			this.fileName = fileName;
			this.type = type;
			this.exception = exception;
		}
		
		public Exception getException() {
			return exception;
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
			sb.append(NLT.get(getTypeCode()))
				.append(": ")
				.append(getFileName())
				.append(" (")
				.append(getRepositoryName())
				.append(")")
				.append(": ")
				.append(getException().getLocalizedMessage());
			return sb.toString();
		}
	}

}
