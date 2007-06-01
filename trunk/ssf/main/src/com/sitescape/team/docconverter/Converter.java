package com.sitescape.team.docconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.FilePathUtil;
import com.sitescape.team.util.FileStore;
import com.sitescape.team.util.SPropsUtil;

public abstract class Converter<T>
{
	protected FileStore cacheFileStore;
	public Converter() {
		cacheFileStore = new FileStore(SPropsUtil.getString("cache.file.store.dir"));
	}
	
	/*
	 * The vendor-specific subclasses (like TextStellentConverter, etc) will implement this method,
	 *   which does the actual file-to-file conversion.  Intermediate classes (like TextConverter) just
	 *   leave it undefined and declare themselves abstract.
	 */
	public abstract void convert(String ifp, String ofp, long timeout, T parameters)
		throws Exception;

	/*
	 * Direct subclasses (like TextConverter) will provide public convert() methods, which (for the most part)
	 *   will just pass their arguments along to this method, adding the sub-directory name and file extension
	 *   to be used in the conversion
	 */
	protected InputStream convert(Binder binder, DefinableEntity entry, FileAttachment fa, T parameters, String subdir, String suffix)
		throws IOException
	{
		String relativeFilePath = fa.getFileItem().getName();
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, relativeFilePath);
		String convertedFilePath = filePath + suffix;
		File convertedFile = cacheFileStore.getFile(convertedFilePath);

		if (!convertedFile.exists()
				|| convertedFile.lastModified() < fa.getModification().getDate().getTime())
		{
			createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, parameters);
		}

		return new FileInputStream(convertedFile);
	}


	protected void createCachedFile(File convertedFile, Binder binder, DefinableEntity entry, FileAttachment fa,
									String filePath, String relativeFilePath, T parameters)
		throws IOException
	{
		InputStream is = null;
		FileOutputStream fos = null;
		File copyOfOriginalFile = null;
		try
		{
			File parentDir = convertedFile.getParentFile();
			if(!parentDir.exists())
				parentDir.mkdirs();

			is = RepositoryUtil.readVersioned(fa.getRepositoryName(), binder, entry, relativeFilePath, fa.getHighestVersion().getVersionName());
			copyOfOriginalFile = cacheFileStore.getFile(filePath);
			fos = new FileOutputStream(copyOfOriginalFile);
			FileCopyUtils.copy(is, fos);
			convert(copyOfOriginalFile.getAbsolutePath(), convertedFile.getAbsolutePath(), 30000, parameters);
		}
		catch(Exception e) {
		}
		finally
		{
			if (is != null)
				is.close();
			if (fos != null)
				fos.close();
			if(copyOfOriginalFile != null && copyOfOriginalFile.exists()) {
				copyOfOriginalFile.delete();
			}
		}
	}

	protected InputStream getCachedFile(Binder binder, DefinableEntity entry, FileAttachment fa, String fileName, String subdir)
		throws IOException
	{
		String filePath = FilePathUtil.getFilePath(binder, entry, fa, subdir, fileName);
		File imageFile = cacheFileStore.getFile(filePath);
		return new FileInputStream(imageFile);
	}

}