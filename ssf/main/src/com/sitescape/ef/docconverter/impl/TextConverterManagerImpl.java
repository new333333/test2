package com.sitescape.ef.docconverter.impl;

import com.sitescape.ef.docconverter.ITextConverterManager;
import com.sitescape.ef.docconverter.TextConverter;

public class TextConverterManagerImpl
	implements ITextConverterManager
{
	int _active = ITextConverterManager.OPENOFFICE;
	TextConverter _stellentConverter = null,
				  _openOfficeConverter = null;
	
	public TextConverterManagerImpl() {}
	
	public TextConverter getConverter()
	{
		if (_active == ITextConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (_active == ITextConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public TextConverter getConverter(int type)
	{
		if (type == ITextConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (type == ITextConverterManager.OPENOFFICE)
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

	public void setStellentConverter(TextConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public TextConverter getStellentConverter()
	{
		return _stellentConverter;
	}

	public void setOpenOfficeConverter(TextConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public TextConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
