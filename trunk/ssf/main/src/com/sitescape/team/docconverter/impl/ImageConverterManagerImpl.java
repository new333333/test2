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
package com.sitescape.team.docconverter.impl;

import com.sitescape.team.docconverter.IImageConverterManager;
import com.sitescape.team.docconverter.ImageConverter;

public class ImageConverterManagerImpl
	implements IImageConverterManager
{
	int _active = IImageConverterManager.OPENOFFICE;
	ImageConverter _stellentConverter = null,
				   _openOfficeConverter = null;
	
	public ImageConverterManagerImpl() {}
	
	public ImageConverter getConverter()
	{
		if (_active == IImageConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (_active == IImageConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public ImageConverter getConverter(int type)
	{
		if (type == IImageConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (type == IImageConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public void setActive(int active)
	{
		_active = active;
	}
	
	public int getActive()
	{
		return _active;
	}

	public void setStellentConverter(ImageConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public ImageConverter getStellentConverter()
	{
		return _stellentConverter;
	}

	public void setOpenOfficeConverter(ImageConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public ImageConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
