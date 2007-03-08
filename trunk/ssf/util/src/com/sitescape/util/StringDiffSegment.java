package com.sitescape.util;

public class StringDiffSegment
{
	private int _type;
	private String _content = "";
	public final static int UNKNOWN = 0,
					 		SAME = 1,
					 		ADDITION = 2,
					 		SUBTRACTION = 3;
	
	public StringDiffSegment()
	{
		_type = UNKNOWN;
	}
	
	public StringDiffSegment(int type, String content)
	{
		_type = type;
		_content = content;
	}
	
	public int getType()
	{
		return _type;
	}
	
	public void setType(int type)
		throws Exception
	{
		_type = type;
		return;
	}
	
	public String getContent()
	{
		return _content;
	}
	
	public void setContent(String content)
		throws Exception
	{
		if (content == null)
			content = "";
		_content = content;
		return;
	}
}
