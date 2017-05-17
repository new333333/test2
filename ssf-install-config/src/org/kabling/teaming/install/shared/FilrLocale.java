package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class FilrLocale implements Serializable,Comparable<FilrLocale>
{

	private static final long serialVersionUID = 7682619962036011720L;

	private String language;
	private String country;
	private String displayName;
	
	public FilrLocale()
	{
	}
	
	public FilrLocale(String lang, String country,String displayName)
	{
		this.language = lang;
		this.country = country;
		this.displayName = displayName;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public int compareTo(FilrLocale o)
	{
		return displayName.compareTo(o.getDisplayName());
	}
}
