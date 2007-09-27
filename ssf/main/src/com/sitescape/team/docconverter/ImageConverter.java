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
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

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
	
	protected void createConvertedFileWithDefaultContent(File convertedFile) throws IOException {
		FileCopyUtils.copy(new File(_defaultImage), convertedFile);
	}


}
