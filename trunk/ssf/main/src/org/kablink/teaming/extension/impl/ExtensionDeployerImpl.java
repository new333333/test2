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
/**
 * 
 */
package org.kablink.teaming.extension.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractAttribute;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.extension.ZoneClassManager;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.util.LockFile;
import org.kablink.util.Validator;
import org.springframework.util.FileCopyUtils;


/**
 * 
 * Listens for and deploys extensions.
 * 
 */
public class ExtensionDeployerImpl extends CommonDependencyInjection implements ExtensionDeployer {

	protected final Log logger = LogFactory.getLog(getClass());

	private String infPrefix = "WEB-INF";
	private final static String TSFILE="timestamps"; 
	private final static String LOCKFILE="lockfile";
	private final static String PICKUP="pickup"; 

	private TemplateModule templateModule;
	private DefinitionModule definitionModule;
	private ZoneClassManager zoneClassManager;
	private AdminModule adminModule;
	
	public void setTemplateModule(TemplateModule templateModule) {
		this.templateModule = templateModule;
	}
	
	public TemplateModule getTemplateModule() {
		return templateModule;
	}

	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	
	public DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	protected ZoneClassManager getZoneClassManager() {
		return zoneClassManager;
	}
	public void setZoneClassManager(ZoneClassManager zoneClassManager) {
		this.zoneClassManager = zoneClassManager;
	}
	
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}

	public AdminModule getAdminModule() {
		return adminModule;
	}

	/**
	 * Called on a timer.  Need to handle all zones
	 *
	 */
	public void check() {
		//call each zone
		final List companies = getCoreDao().findCompanies();
   		for (int i=0; i<companies.size(); ++i) {
   			Workspace zone = (Workspace)companies.get(i);
			if (zone.isDeleted()) continue;
			try {
       			User user = getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, zone.getId());
       			RequestContextUtil.setThreadContext(user).resolve();      		
       			deploy();
			} finally {
				RequestContextHolder.clear();
			}
   		}
	}
	public void deploy() {
		String sharedExtensionDir = SPropsUtil.getDirPath("data.extension.root.dir") + "extensions" + File.separator + Utils.getZoneKey() ;
		File sharedDir = new File(sharedExtensionDir);		
		if (!sharedDir.exists()) sharedDir.mkdirs();
		//this file controls access to the shared extensions directory which is checked by all cluster members
		// and the local extensions web-inf directory.  In other words, the web-inf directory is only updated when this lock is held
		LockFile lock = new LockFile(new File(sharedDir, LOCKFILE));
		if (!lock.getLock()) {
			logger.info("Could not get the deploy lock");
			return;  //try again later
		}
		//web-inf
		String localExtensionDir = DirPath.getExtensionBasePath() + File.separator + Utils.getZoneKey();
		File localDir = new File(localExtensionDir);
		if (!localDir.exists()) localDir.mkdirs();
		try {
		
			File sharedTimeFile = new File(sharedDir, TSFILE);
			if (!sharedTimeFile.exists()) sharedTimeFile.createNewFile();
			Properties shared = toProperties(sharedExtensionDir + File.separator + TSFILE);
			
			File localTimeFile = new File(localDir, TSFILE);
			Properties local = toProperties(localExtensionDir + File.separator + TSFILE);
			File[] extensions = sharedDir.listFiles(new FileOnlyFilter());
			if (extensions != null && extensions.length > 0) {
				String deployedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ENGLISH).format(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());

				for (int i=0; i<extensions.length; ++i) {
					File successDir = new File(sharedDir, PICKUP);
					if (!successDir.exists()) successDir.mkdirs();
					File extension = extensions[i];
					try {
						deploy(extension, true);
						FileHelper.move(extension, new File(successDir, extension.getName()));
						shared.put(extension.getName(), deployedDate);
						local.put(extension.getName(), deployedDate);
					} catch (IOException e) {
						logger.error("Unable to open extension " + extension.getPath(),
							e);
						File failedDir = new File(sharedDir, "failed");
						if (!failedDir.exists()) failedDir.mkdirs();
						FileHelper.move(extension, new File(failedDir, extension.getName()));	
					}
				}
				//update shared file on disk
				shared.store(new FileOutputStream(sharedExtensionDir + File.separator + TSFILE), null);				
			} 
			if (!localTimeFile.exists() || sharedTimeFile.lastModified() > localTimeFile.lastModified()) {
				//now see if another node in the cluster did initial deploy -> time to do local deploy
				//Don't know how the disks are shared
				for (Iterator iter=shared.keySet().iterator(); iter.hasNext();) {
					String key = (String)iter.next();
					//check local date
					String date = local.getProperty(key);
					if (Validator.isNull(date) || !date.equals(shared.getProperty(key))) {
						//	deploy locally
						try {
							deploy(new File(sharedExtensionDir + File.separator + PICKUP + File.separator + key), false);
						} catch (IOException e) {
							logger.error("Unable to deploy extension  " + key, e);
						}
					}
				}
				//the date isn't copied, but all that matters is that sharedTime is less than it.  
				//No other updates to sharedTime can happen until after this new localTime.
				
				FileCopyUtils.copy(sharedTimeFile, localTimeFile);	
			}
			
		} catch (Exception ex) {
			logger.error("Error in deployer", ex);
		} finally {
			lock.releaseLock();
		}		
	}
	public void deploy(File extension, boolean full) throws IOException {
		String zoneKey = Utils.getZoneKey();
		logger.info("Deploying new extension from " + extension.getPath());
		SAXReader reader = new SAXReader(false);
		reader.setIncludeExternalDTDDeclarations(false);
		final String extensionPrefix = extension.getName().substring(0, extension.getName().lastIndexOf("."));
		File extensionDir = new File(DirPath.getExtensionBasePath() + File.separator + zoneKey + File.separator + extensionPrefix);
		extensionDir.mkdirs();
		File extensionWebDir = new File(DirPath.getExtensionWebPath() + File.separator + zoneKey +  File.separator + extensionPrefix);
		extensionWebDir.mkdirs();
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(extension));
		ZipEntry entry = null;
		try {
			//load all the files
			while((entry = zipIn.getNextEntry()) != null) {
				// extract file to proper extension directory
				File inflated;
				String name = entry.getName();
				//strip off infPrefix	
				if (entry.getName().startsWith(infPrefix)) {					
					inflated = new File(extensionDir, name.substring(infPrefix.length(), name.length()));
				} else {
					inflated = new File(extensionWebDir, name);
				}
				if (entry.isDirectory()) {
					if (logger.isDebugEnabled()) logger.debug("Creating directory at " + inflated.getPath());
					inflated.mkdirs();
					zipIn.closeEntry();
					continue;
				} else {
					inflated.getParentFile().mkdirs();
					FileOutputStream entryOut = new FileOutputStream(inflated);
					if (logger.isDebugEnabled()) logger.debug("Inflating file resource to " + inflated.getPath());
					FileCopyUtils.copy(new ZipEntryStream(zipIn),  entryOut);
				}
				zipIn.closeEntry();
			}
		} finally {
			zipIn.close();
		}
		if (full) {
			//load definitions
			File defDir = new File(extensionDir.getAbsolutePath() + File.separator + "classes" + 
					File.separator + "config" +  File.separator + "definitions");
			File definitions[] = defDir.listFiles(new XMLFilter());
			if (definitions != null) {
				List<String> defs = new ArrayList();
				for (int i=0; i<definitions.length; ++i) {
					File definition = definitions[i];
					if (logger.isDebugEnabled()) logger.debug("Registering definition from " +
								definition.getPath());
						
					try {
						SAXReader xIn = new SAXReader(false);
						final Document document = xIn.read(definition);
						
							// record the "owning" extension
						document.getRootElement().add(new AbstractAttribute() {
								private static final long serialVersionUID = -7880537136055718310L;
								public QName getQName() {
									return new QName(ObjectKeys.XTAG_ATTRIBUTE_EXTENSION, document
											.getRootElement().getNamespace());
								}
								public String getValue() {
									return extensionPrefix;
								}
							});
						// attempt to add
						defs.add(getDefinitionModule().addDefinition(document, null, true).getId());
					} catch (DocumentException e) {
						logger.warn("Malformed template file " 	+ extension.getPath(), e);
					}
				}
				//resolve dependencies between definitions
				for (String id:defs) {
					if (id != null) getDefinitionModule().updateDefinitionReferences(id);
				}
			}
			//Now load templates
			File templateDir = new File(extensionDir.getAbsolutePath() + File.separator + "classes" + 
					File.separator + "config" + File.separator + "templates");
			File templates[] = templateDir.listFiles(new XMLFilter());
			if (templates != null) {
				for (int i=0; i<templates.length; ++i) {
					File template = templates[i];
					if (logger.isDebugEnabled()) 
						logger.debug("Registering template from " + template.getPath());
						
					try {
						SAXReader xIn = new SAXReader(false);
						final Document document = xIn.read(template);
						// attempt to add
						getTemplateModule().addTemplate(document, true);
					} catch (DocumentException e) {
						logger.warn("Malformed template file " + extension.getPath(), e);
					}
				}
			}
		}
		//Now move libraries into classpath
		getZoneClassManager().addExtensionLibs(extensionDir);
		
		//Add the extension info to database
		ExtensionInfo extInfo = new ExtensionInfo();
		extInfo.setName(extension.getName());
		extInfo.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		
		List extList = findExtensions(RequestContextHolder.getRequestContext().getZoneId());
		if (extList.contains(extInfo)) {
			//Extension already exists, then lets update existing extension
			List foundList = findExtensions(extension.getName(), RequestContextHolder.getRequestContext().getZoneId());
			if(foundList.size() > 0)
			{
				ExtensionInfo foundExtInfo = (ExtensionInfo) foundList.get(0);
				//TODO update the found object

				
				updateExtension(foundExtInfo);

			}
		} else {
			addExtension(extInfo);
		}
		
		logger.info("Extension deployed successfully from " + extension.getPath());
	}


	public void setInfPrefix(String infPrefix) {
		this.infPrefix = infPrefix;
	}
	protected class XMLFilter implements FilenameFilter {
	    public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".xml"));
	    }
	}
	protected class LibFilter implements FilenameFilter {
	    public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".jar"));
	    }
	}
	protected class FileOnlyFilter implements FileFilter {
	    public boolean accept(File file) {
	        if (file.isDirectory()) return false;
	        return !file.getName().equals(LOCKFILE) && !file.getName().equals(TSFILE);
	    }
	}
	private  Properties toProperties(String fileName) {
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(fileName);
			Properties props = new Properties();
			props.load(fIn);
			return props;
		}
		catch (IOException ioe) {
			return new Properties();
		}
		finally {
			if (fIn != null) {
				try {
					fIn.close();
				} catch (IOException ignore) {}
			}
		}
	}
	

	public void remove(File extension) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeExtension(ExtensionInfo ext) {
		
		// TODO call remove
		deleteExtension(ext);
		
		return false;
	}
	
	public void addExtension(ExtensionInfo extension) {
		getAdminModule().addExtension(extension);
	}

	public boolean deleteExtension(ExtensionInfo extension) {
		
		boolean retValue = true;
		
		//TODO remove extension
		getAdminModule().deleteExtension(extension.getId());
		return retValue;
	}

	public List findExtensions(Long zoneId) {
		OrderBy order = new OrderBy();
		order.addColumn("name");
		FilterControls filter = new FilterControls();
		filter.setOrderBy(order);
		return getCoreDao().loadObjects(ExtensionInfo.class, filter, zoneId);
	}

	public ExtensionInfo getExtension(String id)
			throws NoObjectByTheIdException {
		ExtensionInfo extension = null;
		
		Object obj = getCoreDao().load(ExtensionInfo.class, id);
		if(obj != null && obj instanceof ExtensionInfo)
		{
			return (ExtensionInfo) obj;
		}
		return extension;
	}

	public void updateExtension(ExtensionInfo extension) {
		getAdminModule().modifyExtension(extension);
	}
	public List findExtensions(String name, Long zoneId) {
		return getCoreDao().loadObjects(new ObjectControls(ExtensionInfo.class), new FilterControls("name", name), zoneId);
	}
	
}

