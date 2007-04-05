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

public interface IImageConverterManager
{
	static final String IMG_EXTENSION = ".jpg";
	static final int OPENOFFICE = 1,
	 				 STELLANT = 2,
	 				 IMAGEWIDTH = 200,
	 				 IMAGEHEIGHT = 200;

	public ImageConverter getConverter();
	public ImageConverter getConverter(int type);
}
