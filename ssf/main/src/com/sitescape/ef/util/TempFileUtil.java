package com.sitescape.ef.util;

import java.io.File;
import java.io.IOException;

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
}
