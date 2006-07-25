package com.sitescape.ef.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import com.sitescape.ef.UncheckedIOException;

public class TempFileUtil {

	public static File createTempFile(String prefix, File fileDir) 
		throws UncheckedIOException {
		try {
			if(!fileDir.exists())
				FileHelper.mkdirs(fileDir);
			return File.createTempFile(prefix + "_", null, fileDir);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
	
	public static File createTempFileWithContent(String prefix, File fileDir, InputStream content)
		throws UncheckedIOException {
		File tempFile = TempFileUtil.createTempFile(prefix, fileDir);
		try {
			FileCopyUtils.copy(content, new BufferedOutputStream(new FileOutputStream(tempFile)));
			return tempFile;
		}
		catch(IOException e) {
			tempFile.delete();
			throw new UncheckedIOException(e);
		}
	}
}
