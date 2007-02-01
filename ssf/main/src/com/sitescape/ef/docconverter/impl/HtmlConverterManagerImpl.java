package com.sitescape.ef.docconverter.impl;

import com.sitescape.ef.docconverter.HtmlConverter;
import com.sitescape.ef.docconverter.IHtmlConverterManager;

public class HtmlConverterManagerImpl
	implements IHtmlConverterManager
{
	int _active = IHtmlConverterManager.OPENOFFICE;
	HtmlConverter _stellentConverter = null,
				  _openOfficeConverter = null;
	
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
	
	public void setStellentConverter(HtmlConverter converter_in)
	{
		_stellentConverter = converter_in;
	}
	
	public HtmlConverter getStellentConverter()
	{
		return _stellentConverter;
	}
	
	public void setOpenOfficeConverter(HtmlConverter converter_in)
	{
		_openOfficeConverter = converter_in;
	}
	
	public HtmlConverter getOpenOfficeConverter()
	{
		return _openOfficeConverter;
	}
}
