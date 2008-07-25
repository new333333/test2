package com.sitescape.team.domain;

public class AuthenticationConfig extends ZonedObject {

    protected String id;
	protected String url;
	protected String mappings;
	protected String userSearches;
	protected int position;
	
	protected AuthenticationConfig()
	{
		
	}
	
	public AuthenticationConfig(String url, String mappings, String userSearches)
	{
		setUrl(url);
		setMappings(mappings);
		setUserSearches(userSearches);
	}

	public String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }

    public String getMappings() {
		return mappings;
	}

	public void setMappings(String mappings) {
		this.mappings = mappings;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserSearches() {
		return userSearches;
	}

	public void setUserSearches(String userSearches) {
		this.userSearches = userSearches;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
