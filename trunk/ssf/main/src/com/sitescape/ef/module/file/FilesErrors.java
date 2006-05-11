package com.sitescape.ef.module.file;

import java.util.ArrayList;
import java.util.List;

import com.sitescape.ef.util.NLT;

public class FilesErrors {

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
	
	public static class Problem {
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
		
		public static String[] typeCodes = {
			"fileupload.error.other",
			"fileupload.error.filtering",
			"fileupload.error.storing.primary.file",
			"fileupload.error.generating.scaled.file",
			"fileupload.error.storing.scaled.file",
			"fileupload.error.generating.thumbnail.file",
			"fileupload.error.storing.thumbnail.file",
			"fileupload.error.deleting.primary.file",
			"fileupload.error.deleting.scaled.file",
			"fileupload.error.deleting.thumbnail.file",
			"fileupload.error.canceling.lock",
			"fileupload.error.locked.by.another.user",
			"fileupload.error.reserved.by.another.user"
		};
		
		private String repositoryServiceName;
		private String fileName;
		private int type; // one of the constants defined above
		private Exception exception;
		
		public Problem(String repositoryServiceName, String fileName, 
				int type, Exception exception) {
			this.repositoryServiceName = repositoryServiceName;
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
		
		public String getRepositoryServiceName() {
			return repositoryServiceName;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(NLT.get(getTypeCode()))
				.append(": ")
				.append(getFileName())
				.append(" (")
				.append(getRepositoryServiceName())
				.append(")")
				.append(": ")
				.append(getException().getLocalizedMessage());
			return sb.toString();
		}
	}

}
