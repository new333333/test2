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
package org.kablink.teaming.module.file.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.docconverter.IHtmlConverterManager;
import org.kablink.teaming.docconverter.IImageConverterManager;
import org.kablink.teaming.docconverter.ImageConverter;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.file.ConvertedFileModule;
import org.kablink.teaming.module.file.FileModule;
import org.springframework.util.FileCopyUtils;

public class ConvertedFileModuleImpl implements ConvertedFileModule {
	private FileModule fileModule;
	private IHtmlConverterManager htmlConverterManager;
	private IImageConverterManager imageConverterManager;

	public void setHtmlConverterManager(IHtmlConverterManager htmlConverterManager) {
		this.htmlConverterManager = htmlConverterManager;
	}
	
	protected IHtmlConverterManager getHtmlConverterManager() {
		return htmlConverterManager;
	}
	
	public void setImageConverterManager(IImageConverterManager imageConverterManager) {
		this.imageConverterManager = imageConverterManager;
	}
	
	protected IImageConverterManager getImageConverterManager() {
		return imageConverterManager;
	}
	
	protected FileModule getFileModule() {
		return fileModule;
	}

	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}

	public void readScaledFile(Binder binder, DefinableEntity entry, 
			FileAttachment fa, OutputStream out)
	{
		ImageConverter converter = null;
		
		try
		{
			converter = this.imageConverterManager.getConverter();
			FileCopyUtils.copy(converter.convertToScaledImage(binder, entry, fa,
									new ImageConverter.Parameters(IImageConverterManager.IMAGEWIDTH, IImageConverterManager.IMAGEHEIGHT)),
							   out);
		}
		catch (IOException e)
		{
			try {
				getFileModule().readFile(binder, entry, fa, out);				
			}
			catch(Exception ex) {
				// out.print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
				throw new UncheckedIOException(e);
			}
		}
	}
	
	public void readThumbnailFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out)
	{
		ImageConverter converter = null;
		
		try
		{
			converter = this.imageConverterManager.getConverter();
			FileCopyUtils.copy(converter.convertToThumbnail(binder, entry, fa,
									new ImageConverter.Parameters(IImageConverterManager.IMAGEWIDTH, 0)),
							   out);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Read cached HTML conversion file. If this file does not exist we must create it by going into the file
	 * respository fetching the non-HTML file an running conversion program to generate HTML file.
	 * 
	 *	@param	url		If images or URL tags exist we need the url to insert a valid HTML reference to items
	 *	@param	binder	File location information
	 *	@param	entry	File location information
	 *	@param	fa		File attachment information
	 *	@param	out		Output Stream that we will feed HTML file too
	 *
	 */
	public void readCacheHtmlFile(String url, Binder binder, DefinableEntity entry, FileAttachment fa, OutputStream out) 
	{
		InputStream is = null;

		try
		{
			is = htmlConverterManager.getConverter().convert(url, binder, entry, fa);
			FileCopyUtils.copy(is, out);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
		finally {
			if (is != null)
			{
				try
				{
					is.close();
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Read cached URL referenced file from cache repository.
	 * 
	 *	@param	binder			File location information
	 *	@param	entry			File location information
	 *	@param	fa				File attachment information
	 *	@param	out				Output Stream that we will feed HTML file too
	 *	@param	urlFileName		Name of url file we will process
	 *
	 */
	public void readCacheUrlReferenceFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out, String urlFileName)
	{
		InputStream is = null;
		
		try
		{
			urlFileName = (new File(urlFileName)).getName();  // Prevent ../ filename hacks
			is = htmlConverterManager.getConverter().getCachedFile(binder, entry, fa, urlFileName);
			FileCopyUtils.copy(is, out);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
		finally {
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException io) {}
			}
		}
	}
	
	/**
	 * Read cached image file from cache repository.
	 * 
	 *	@param	binder			File location information
	 *	@param	entry			File location information
	 *	@param	fa				File attachment information
	 *	@param	out				Output Stream that we will feed HTML file too
	 *	@param	imageFileName	Name of image file we will process
	 *
	 */
	public void readCacheImageReferenceFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out, String imageFileName)
	{
		InputStream is = null;
		
		try
		{
			is = htmlConverterManager.getConverter().getCachedFile(binder, entry, fa, imageFileName);
			FileCopyUtils.copy(is, out);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
		finally {
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException io) {}
			}
		}		
	}
	

}
