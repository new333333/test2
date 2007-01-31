package com.sitescape.ef.docconverter.impl;

import com.sitescape.ef.docconverter.IImageConverterManager;
import com.sitescape.ef.docconverter.ImageConverter;

public class ImageConverterManagerImpl
	implements IImageConverterManager
{
	int _active = IImageConverterManager.OPENOFFICE;
	//ImageStellentConverter _stellentConverter = null;
	ImageOpenOfficeConverter _openOfficeConverter = null;
	
	public ImageConverterManagerImpl() {}
	
	public ImageConverter getConverter()
	{
		//if (_active == IImageConverterManager.STELLANT)
		//	return _stellentConverter;
		//else
		if (_active == IImageConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public ImageConverter getConverter(int type)
	{
		//if (type == IImageConverterManager.STELLANT)
		//	return _stellentConverter;
		//else
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
/*
	public void setStellentConverter(ImageStellentConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public ImageStellentConverter getStellentConverter()
	{
		return _stellentConverter;
	}
*/
	public void setOpenOfficeConverter(ImageOpenOfficeConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public ImageOpenOfficeConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
