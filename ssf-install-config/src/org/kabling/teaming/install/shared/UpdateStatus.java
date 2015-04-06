package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class UpdateStatus implements Serializable
{

	private static final long serialVersionUID = 6152445288703080961L;

	private boolean validDataDrive;
	private boolean validHostName;
	
	private boolean success;
	private int returnCode;
	private String  message;
	
	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public int getReturnCode()
	{
		return returnCode;
	}

	public void setReturnCode(int returnCode)
	{
		this.returnCode = returnCode;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public UpdateStatus()
	{
	}

	public boolean isValidDataDrive()
	{
		return validDataDrive;
	}

	public void setValidDataDrive(boolean validDataDrive)
	{
		this.validDataDrive = validDataDrive;
	}

	public boolean isValidHostName()
	{
		return validHostName;
	}

	public void setValidHostName(boolean validHostName)
	{
		this.validHostName = validHostName;
	}
	
	
}
