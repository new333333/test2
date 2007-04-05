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

import com.sitescape.team.docconverter.ITextConverterManager;
import com.sitescape.team.docconverter.TextConverter;

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
