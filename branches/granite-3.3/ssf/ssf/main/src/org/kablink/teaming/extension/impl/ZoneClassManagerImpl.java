/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.extension.impl;
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
import org.kablink.teaming.InternalException;
import org.kablink.teaming.extension.ExtensionCallback;
import org.kablink.teaming.extension.ZoneClassManager;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.Utils;


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
