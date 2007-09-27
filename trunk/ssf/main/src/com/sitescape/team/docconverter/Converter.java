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
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.FilePathUtil;
import com.sitescape.team.util.FileStore;
import com.sitescape.team.util.SPropsUtil;

public abstract class Converter<T>
{
	protected FileStore cacheFileStore;
	private FileModule fileModule;
	
	public Converter() {
		cacheFileStore = new FileStore(SPropsUtil.getString("cache.file.store.dir"));
	}
	
	protected FileModule getFileModule() {
		return fileModule;
	}
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
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
			if(fa.getFileItem().getLength() > SPropsUtil.getLong("doc.conversion.size.threshold", 31457280L)) {
				convertedFile.delete();
				File parentDir = convertedFile.getParentFile();
				if(!parentDir.exists())
					parentDir.mkdirs();
				createConvertedFileWithDefaultContent(convertedFile);
			}
			else {
				createCachedFile(convertedFile, binder, entry, fa, filePath, relativeFilePath, parameters);
			}
		}

		return new FileInputStream(convertedFile);
	}

	protected abstract void createConvertedFileWithDefaultContent(File convertedFile) throws IOException;

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

			is = getFileModule().readFile(binder, entry, fa);
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