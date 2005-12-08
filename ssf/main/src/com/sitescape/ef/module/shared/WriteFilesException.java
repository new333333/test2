package com.sitescape.ef.module.shared;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.exception.UncheckedCodedContainerException;

public class WriteFilesException extends UncheckedCodedContainerException {
	private static final String WriteFilesException_ErrorCode = "errorcode.write.files";

		public WriteFilesException() {
			super(WriteFilesException_ErrorCode);
			
		}
	
		public void setErrorArgs(Entry entry, int totalFiles, int failedFiles) {
			setErrorArgs(new Object[]{entry.getId(), new Integer(totalFiles), 
					new Integer(failedFiles)});
		}
}
