package com.sitescape.ef.module.file;

import java.util.ArrayList;
import java.util.List;

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
	
	public static class Problem {
		public static int OTHER_PROBLEM						= 0;
		public static int PROBLEM_FILTERING					= 1;
		public static int PROBLEM_STORING_PRIMARY_FILE		= 2;
		public static int PROBLEM_GENERATING_SCALED_FILE	= 3;
		public static int PROBLEM_STORING_SCALED_FILE		= 4;
		public static int PROBLEM_GENERATING_THUMBNAIL_FILE	= 5;
		public static int PROBLEM_STORING_THUMBNAIL_FILE	= 6;
		
		public static String[] typeCodes = {
			"fileupload.error.other",
			"fileupload.error.filtering",
			"fileupload.error.storing.primary.file",
			"fileupload.error.generating.scaled.file",
			"fileupload.error.storing.scaled.file",
			"fileupload.error.generating.thumbnail.file",
			"fileupload.error.storing.thumbnail.file"
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
	}

}
