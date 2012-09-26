package org.kabling.teaming.install.shared;

import java.io.Serializable;

/**
 * Mirrored Folders Configuration Settings
 * 
 * Mirrored folders are local/shared directories that are exposed within Kablink. The directories must be configured here first before they
 * are available to the folder configuration interface within Kablink (see Modify a Folder).
 * 
 * For security reasons the set of people who can map Kablink folders to these shared directories is limited Specify the specific Kablink
 * users or groups that are allowed to map each folder (separated by semi-colons).
 * 
 * Each mirrored folder configuration must have a unique id (use a-z,0-9), and a title to be used in the user interface. Set enabled to true
 * to make the mirrored folder configuration active. The examples below show how to set up both file system and webdav mirrors.
 * 
 * By default the mirrored folder is set to readonly. This means that Kablink users can access the files, but cannot modify them. Set the
 * readonly attribute to false to allow read/write access to the folder (based on both this server's access to the directory and the Kablink
 * user's access).
 * 
 * When zoneId is set to blank, the mirrored folder is configured for the default zone. To configure mirrored folders for zones, you must
 * first configure them in the site administration, then add additional mirrored folders specifying the zone id.
 * 
 * There are two mirrored folder types: file (for normal file systems), and webdav (for WebdDAV servers) that support basic authentication.
 **/
public class MirroredFolder implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private boolean enabled;
	private String type;
	private String id;
	private String title;
	private String rootPath;
	private boolean readOnly;
	private String zoneId;

	private String allowedUsersList;
	private String allowedGroupsList;

	private String webDAVHostUrl;
	private String webDAVUser;
	private String webDAVPassword;

	public MirroredFolder()
	{
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getRootPath()
	{
		return rootPath;
	}

	public void setRootPath(String rootPath)
	{
		this.rootPath = rootPath;
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	public String getZoneId()
	{
		return zoneId;
	}

	public void setZoneId(String zoneId)
	{
		this.zoneId = zoneId;
	}

	public String getAllowedUsersList()
	{
		return allowedUsersList;
	}

	public void setAllowedUsersList(String allowedUsersList)
	{
		this.allowedUsersList = allowedUsersList;
	}

	public String getAllowedGroupsList()
	{
		return allowedGroupsList;
	}

	public void setAllowedGroupsList(String allowedGroupsList)
	{
		this.allowedGroupsList = allowedGroupsList;
	}

	public String getWebDAVHostUrl()
	{
		return webDAVHostUrl;
	}

	public void setWebDAVHostUrl(String webDAVHostUrl)
	{
		this.webDAVHostUrl = webDAVHostUrl;
	}

	public String getWebDAVUser()
	{
		return webDAVUser;
	}

	public void setWebDAVUser(String webDAVUser)
	{
		this.webDAVUser = webDAVUser;
	}

	public String getWebDAVPassword()
	{
		return webDAVPassword;
	}

	public void setWebDAVPassword(String webDAVPassword)
	{
		this.webDAVPassword = webDAVPassword;
	}
}
