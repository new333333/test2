package com.sitescape.ef.docconverter.impl;

import com.sitescape.ef.docconverter.HtmlConverter;
import com.sitescape.ef.docconverter.IHtmlConverterManager;

public class HtmlConverterManagerImpl
	implements IHtmlConverterManager
{
	int _active = IHtmlConverterManager.OPENOFFICE;
	HtmlStellentConverter _stellentConverter = null;
	HtmlOpenOfficeConverter _openOfficeConverter = null;
	
	public HtmlConverterManagerImpl() {}
	
	public HtmlConverter getConverter()
	{
		if (_active == IHtmlConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (_active == IHtmlConverterManager.OPENOFFICE)
			return _openOfficeConverter;
		
		return null;
	}
	
	public HtmlConverter getConverter(int type)
	{
		if (type == IHtmlConverterManager.STELLANT)
			return _stellentConverter;
		else
		if (type == IHtmlConverterManager.OPENOFFICE)
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
	
	public void setStellentConverter(HtmlStellentConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public HtmlStellentConverter getStellentConverter()
	{
		return _stellentConverter;
	}
	
	public void setOpenOfficeConverter(HtmlOpenOfficeConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public HtmlOpenOfficeConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
