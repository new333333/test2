package com.sitescape.ef.module.file;

import java.util.ArrayList;
import java.util.List;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.exception.UncheckedException;

public class WriteFilesException extends UncheckedException {
		
	private Binder binder;
	private Entry entry;
	private List problems;
	
	public WriteFilesException(Binder binder, Entry entry) {
		this.binder = binder;
		this.entry = entry;
		this.problems = new ArrayList();
	}
	
	public void addProblem(Problem problem) {
		problems.add(problem);
	}
	
	public List getProblems() {
		return problems;
	}
	
	public Binder getBinder() {
		return binder;
	}
	
	public Entry getEntry() {
		return entry;
	}
	
	public static class Problem {
		public static int PROBLEM_STORING_PRIMARY_FILE		= 0;
		public static int PROBLEM_GENERATING_SCALED_FILE	= 1;
		public static int PROBLEM_STORING_SCALED_FILE		= 2;
		public static int PROBLEM_GENERATING_THUMBNAIL_FILE	= 3;
		public static int PROBLEM_STORING_THUMBNAIL_FILE	= 4;
		public static int OTHER_PROBLEM						= 5;
		
		public static String[] typeCodes = {
			"fileupload.error.storing.primary.file",
			"fileupload.error.generating.scaled.file",
			"fileupload.error.storing.scaled.file",
			"fileupload.error.generating.thumbnail.file",
			"fileupload.error.storing.thumbnail.file",
			"fileupload.error.other"
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
