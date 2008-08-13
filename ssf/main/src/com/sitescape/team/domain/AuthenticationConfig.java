package com.sitescape.team.domain;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.ClobStringType;

public class AuthenticationConfig extends ZonedObject {
	protected static Log logger = LogFactory.getLog(AuthenticationConfig.class);

    protected String id;
	protected String url;
	protected String userIdAttribute;
	protected Map<String,String> mappings;
	protected List<SearchInfo> userSearches;
	protected List<SearchInfo> groupSearches;
	protected String principal;
	protected String credentials;
	protected int position;
	
	protected AuthenticationConfig()
	{
		
	}
	
	public AuthenticationConfig(String url, String userIdAttribute, Map<String,String> mappings, List<SearchInfo> userSearches,
								List<SearchInfo> groupSearches,
								String principal, String credentials)
	{
		setUrl(url);
		setUserIdAttribute(userIdAttribute);
		setMappings(mappings);
		setUserSearches(userSearches);
		setGroupSearches(groupSearches);
		setPrincipal(principal);
		setCredentials(credentials);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }

    public Map<String,String> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String,String> mappings) {
		this.mappings = mappings;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserIdAttribute() {
		return userIdAttribute;
	}

	public void setUserIdAttribute(String userIdAttribute) {
		this.userIdAttribute = userIdAttribute;
	}

	public List<SearchInfo> getUserSearches() {
		return userSearches;
	}

	public void setUserSearches(List<SearchInfo> userSearches) {
		this.userSearches = userSearches;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public static class SearchInfo
	{
		private String baseDn;
		private String filter;
		private boolean searchSubtree;
		
		public SearchInfo(String baseDn, String filter, boolean searchSubtree)
		{
			setBaseDn(baseDn);
			setFilter(filter);
			setSearchSubtree(searchSubtree);
		}

		public String getBaseDn() {
			return baseDn;
		}

		public void setBaseDn(String baseDn) {
			this.baseDn = baseDn;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String query) {
			this.filter = query;
		}

		public boolean isSearchSubtree() {
			return searchSubtree;
		}

		public void setSearchSubtree(boolean searchSubtree) {
			this.searchSubtree = searchSubtree;
		}
	}

	public List<SearchInfo> getGroupSearches() {
		return groupSearches;
	}

	public void setGroupSearches(List<SearchInfo> groupSearches) {
		this.groupSearches = groupSearches;
	}
}
