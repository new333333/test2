package org.kabling.teaming.install.shared;

import java.io.Serializable;
import java.util.List;

/**
 * File System Configuration
 * 
 * Modify the configName to your desired configuration in the FileSystem element below. You must set the configName to the exact
 * configuration in the file: basic - Simple one-directory setup advanced - Advanced multiple-directory setup
 * 
 * WARNING! Changing a directory path does NOT relocate data after the product has been installed. You must coordinate any changes here with
 * appropriate file system modifications (including directory protections and ownership where applicable).
 * 
 * <FileSystem configName="basic"> IMPORTANT NOTE FOR WINDOWS USERS: Use forward slash as directory separator!
 * 
 * IMPORTANT NOTE FOR LINUX USERS: Over time, the default path for the truetype fonts required by Stellent has changed. For SLES, the change
 * appears to have occurred between SLES10 and SLES11.
 * 
 * The default paths: SLES10: /usr/X11R6/lib/X11/fonts/truetype SLES11: /usr/share/fonts/truetype
 * 
 * As of Teaming 3, the installer will 'sniff' the content of these directory paths to determine which to use as the default. If any *ttf
 * files are found in the SLES11 path, that will be the default. otherwise, if any *ttf files are found in the SLES10 path, that will be the
 * default. Otherwise, no default will be provided and the user must browse for the correct location.
 * 
 * 
 * The basic configuration only requires that you specify a root directory for the data and Kablink takes care of the rest.
 * 
 * 
 * With the advanced configuration you can specify individual directory locations. Add path attributes for those directories you want to
 * locate elsewhere. For example, <SimpleFileRepository path="/share/teamingdata" />
 **/

public class FileSystem implements Serializable
{

	private static final long serialVersionUID = -645027619353219289L;

	private String configName;
	private List<FileConfig> configList;

	public FileSystem()
	{
	}

	public String getConfigName()
	{
		return configName;
	}

	public void setConfigName(String configName)
	{
		this.configName = configName;
	}

	public List<FileConfig> getConfigList()
	{
		return configList;
	}

	public void setConfigList(List<FileConfig> configList)
	{
		this.configList = configList;
	}
}
