package com.sitescape.team.extension.impl;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.InternalException;
import com.sitescape.team.extension.ExtensionCallback;
import com.sitescape.team.extension.ZoneClassManager;
import com.sitescape.team.util.DirPath;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.Utils;

public class ZoneClassManagerImpl implements ZoneClassManager {
	Map<String, ZoneClassLoader> zones = new HashMap();
	protected final Log logger = LogFactory.getLog(getClass());

	public void initialize() {
		//get list of depoyed extensions
		ZoneClassLoader loader = new ZoneClassLoader(new URL[0]); 
		//throw away old references
		String zoneKey = Utils.getZoneKey();
		zones.put(zoneKey, loader);
		File extensionBaseDir = new File(DirPath.getExtensionBasePath() + File.separator + zoneKey);
		File extensions[] = extensionBaseDir.listFiles();
		if (extensions != null) {
			for (int i=0; i<extensions.length; ++i) {
				loader.addExtensionLibs(extensions[i]);
			}
		}
	}
	public void addExtensionLibs(File extensionDir) {
		String zoneKey = Utils.getZoneKey();
		ZoneClassLoader loader = zones.get(zoneKey);
		if (loader == null || loader.hasExtension(extensionDir)) {
			//recreate
			initialize();
			return;
		} else {
			loader.addExtensionLibs(extensionDir);
		}
		
	}
	public Object execute(ExtensionCallback extension, String className) throws ClassNotFoundException, InternalException {
		String zoneKey = Utils.getZoneKey();
		URLClassLoader zoneLoader = zones.get(zoneKey);
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		Class actionClass = null;
       	try {
       		if (zoneLoader != null) {
       			Thread.currentThread().setContextClassLoader(zoneLoader);
       			actionClass = zoneLoader.loadClass(className);
       		} else {
       			actionClass = ReflectHelper.classForName(className);
       		}
   			Object action = (Object)actionClass.newInstance();
       		return extension.execute(action);
       	} catch (IllegalAccessException e) {
			throw new InternalException(e);
		} catch (InstantiationException e) {
			throw new InternalException(e);
		} finally {
       		Thread.currentThread().setContextClassLoader(current);
      	}
	}
	protected class LibFilter implements FilenameFilter {
	    public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".jar"));
	    }
	}
	protected class ZoneClassLoader extends URLClassLoader {
		private HashSet<String>extensions = new HashSet();
		public ZoneClassLoader(URL[] urls) {
			super(urls, Thread.currentThread().getContextClassLoader());
		}
		protected void addExtensionLibs(File extensionDir) {
			extensions.add(extensionDir.getName());
			FilenameFilter jarFilter = new LibFilter();
			File extensionLib = new File(extensionDir, "lib");
			File jars[] = extensionLib.listFiles(jarFilter);
			URL[] existing = getURLs();
			if (jars != null) {
				for (int j=0; j<jars.length; ++j) {
					File jar = jars[j];
					try {
						addURL(jar.toURL());
					} catch (MalformedURLException mf) {
						logger.error("Error in url " + jar.getAbsolutePath());
					}
				}
				
			}
		}
		protected boolean hasExtension(File extensionDir) {
			URL[] existing = getURLs();
			return extensions.contains(extensionDir.getName()); 			
		}

	}
}
