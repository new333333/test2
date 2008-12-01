package com.sitescape.team.extension;

import java.io.File;
import com.sitescape.team.InternalException;
public interface ZoneClassManager {
	public void initialize();
	public void addExtensionLibs(File extensionDir);
	public Object execute(ExtensionCallback extension, String className) throws ClassNotFoundException, InternalException;

}
