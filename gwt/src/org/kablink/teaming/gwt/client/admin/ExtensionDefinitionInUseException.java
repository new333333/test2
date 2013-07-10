package org.kablink.teaming.gwt.client.admin;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ExtensionDefinitionInUseException extends Exception implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7551726317679663592L;

	String message;

    public ExtensionDefinitionInUseException(Exception e)
    {
        message = e.getMessage();
    }

    public ExtensionDefinitionInUseException()
    {
    }

    public ExtensionDefinitionInUseException(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
	
}
