package com.sitescape.ef.docconverter;

public class TextConverterManagerImpl
	implements ITextConverterManager
{
	int _active = ITextConverterManager.OPENOFFICE;
	//TextStellentConverter _stellentConverter = null;
	TextOpenOfficeConverter _openOfficeConverter = null;
	
	public TextConverterManagerImpl() {}
	
	public TextConverter getConverter()
	{
		//if (_active == ITextConverterManager.STELLANT)
		//	return _stellentConverter;
		//else
		if (_active == ITextConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public TextConverter getConverter(int type)
	{
		//if (type == ITextConverterManager.STELLANT)
		//	return _stellentConverter;
		//else
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
/*
	public void setStellentConverter(TextStellentConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public TextStellentConverter getStellentConverter()
	{
		return _stellentConverter;
	}
*/
	public void setOpenOfficeConverter(TextOpenOfficeConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public TextOpenOfficeConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
