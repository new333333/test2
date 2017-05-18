package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class ProductInfo implements Serializable {

	private static final long serialVersionUID = -7486653410358935418L;

	public enum ProductType { KABLINK, NOVELL_VIBE, NOVELL_FILR }
	
	//TODO: License validity ? expiry ?
	private ProductType type;
	private String productVersion;
	private String copyRight;
	private boolean configured;
	private String localIpAddress;
	
	public ProductInfo() {
		
	}

	public ProductType getType()
	{
		return type;
	}

	public void setType(ProductType type)
	{
		this.type = type;
	}

	public String getProductVersion()
	{
		return productVersion;
	}

	public void setProductVersion(String productVersion)
	{
		this.productVersion = productVersion;
	}

	public String getCopyRight()
	{
		return copyRight;
	}

	public void setCopyRight(String copyRight)
	{
		this.copyRight = copyRight;
	}

	public boolean isConfigured()
	{
		return configured;
	}

	public void setConfigured(boolean configured)
	{
		this.configured = configured;
	}

	public String getLocalIpAddress()
	{
		return localIpAddress;
	}

	public void setLocalIpAddress(String localIpAddress)
	{
		this.localIpAddress = localIpAddress;
	}

	
	
	
}
