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

import com.sitescape.team.docconverter.HtmlConverter;
import com.sitescape.team.docconverter.IHtmlConverterManager;

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
