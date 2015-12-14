package org.kabling.teaming.install.shared;

import java.io.Serializable;

public class LicenseInformation implements Serializable
{

	private static final long serialVersionUID = -2159547925196665872L;

	private String issuedDate;
	private String issuedBy;
	private String keyVersion;
	
	private String productId;
	private String productTitle;
	private String productVersion;
	
	private String expirationDate;
	private String datesEffective;
	
	public LicenseInformation()
	{
	}

	public String getIssuedDate()
	{
		return issuedDate;
	}

	public void setIssuedDate(String issuedDate)
	{
		this.issuedDate = issuedDate;
	}

	public String getIssuedBy()
	{
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy)
	{
		this.issuedBy = issuedBy;
	}

	public String getKeyVersion()
	{
		return keyVersion;
	}

	public void setKeyVersion(String keyVersion)
	{
		this.keyVersion = keyVersion;
	}

	public String getProductId()
	{
		return productId;
	}

	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	public String getProductTitle()
	{
		return productTitle;
	}

	public void setProductTitle(String productTitle)
	{
		this.productTitle = productTitle;
	}

	public String getProductVersion()
	{
		return productVersion;
	}

	public void setProductVersion(String productVersion)
	{
		this.productVersion = productVersion;
	}

	public String getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate)
	{
		this.expirationDate = expirationDate;
	}

	public String getDatesEffective()
	{
		return datesEffective;
	}

	public void setDatesEffective(String datesEffective)
	{
		this.datesEffective = datesEffective;
	}
	
	
}
