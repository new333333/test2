/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.docconverter;

import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;

public abstract class ImageConverter extends Converter<ImageConverter.Parameters>
{
	protected String _defaultImage = "";
	protected final Log logger = LogFactory.getLog(getClass());
	protected String _nullTransform = "";
	private static final String SCALED_SUBDIR = "scaled";
	private static final String THUMB_SUBDIR = "thumb";
	private static final String IMG_FILE_SUFFIX = ".jpg";

	public InputStream convertToScaledImage(Binder binder, DefinableEntity entry, FileAttachment fa, ImageConverter.Parameters parameters)
	throws IOException
{
	return super.convert(binder, entry, fa, parameters, SCALED_SUBDIR, IMG_FILE_SUFFIX);
}

	public InputStream convertToThumbnail(Binder binder, DefinableEntity entry, FileAttachment fa, ImageConverter.Parameters parameters)
	throws IOException
{
	return super.convert(binder, entry, fa, parameters, THUMB_SUBDIR, IMG_FILE_SUFFIX);
}

	public void setDefaultImage(String image_in)
	{
		_defaultImage = image_in;
	}
	
	public String getDefaultImage()
	{
		return _defaultImage;
	}
	
	public static class Parameters
	{
		int width;
		int height;
		
		public Parameters(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() { return width; }
		public int getHeight() { return height; }
	}
}
