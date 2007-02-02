package com.sitescape.team.docconverter;

public interface ITextConverterManager {
	static final int OPENOFFICE = 1,
	 				 STELLANT = 2;

	public TextConverter getConverter();
	public TextConverter getConverter(int type);
}
