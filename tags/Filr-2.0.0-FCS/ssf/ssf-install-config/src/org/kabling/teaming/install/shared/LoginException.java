package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class LoginException extends RuntimeException implements Serializable
{
	private static final long serialVersionUID = -2530982281395088461L;
	private int errorCode;
	private String details;
	
	public LoginException()
	{
	}
	
	public LoginException(int errorCode, String details)
	{
		this.errorCode = errorCode;
		this.details = details;
	}
	
	public int getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getDetails()
	{
		return details;
	}

	public void setDetails(String details)
	{
		this.details = details;
	}

	
	
}
