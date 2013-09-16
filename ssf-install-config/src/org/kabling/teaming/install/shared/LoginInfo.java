package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class LoginInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2719503886846492571L;

	private String user;
	
	public LoginInfo()
	{
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}
	
	
}
