package com.sitescape.ef.pipeline.util;

import java.io.File;
import java.io.IOException;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.util.FileHelper;

public class TempFileUtil {

	public static File createTempFile(String prefix, File fileDir) 
		throws UncheckedIOException {
		try {
			if(!fileDir.exists())
				FileHelper.mkdirs(fileDir);
			return File.createTempFile(prefix + "_" + System.currentTimeMillis() + "_", null, fileDir);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
}
