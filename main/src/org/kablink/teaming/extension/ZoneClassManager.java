package org.kablink.teaming.extension;

import java.io.File;

import org.kablink.teaming.InternalException;
public interface ZoneClassManager {
	public void initialize();
	public void addExtensionLibs(File extensionDir);
	public Object execute(ExtensionCallback extension, String className) throws ClassNotFoundException, InternalException;

}
