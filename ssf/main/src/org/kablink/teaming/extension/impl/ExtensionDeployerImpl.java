/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.dom4j.Element;
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
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidOperation;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.NoBinderByTheNameException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.extension.ZoneClassManager;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.teaming.web.servlet.listener.ContextListenerPostSpring;
import org.kablink.util.LockFile;
import org.kablink.util.Validator;

import org.springframework.util.FileCopyUtils;

/**
 * Listens for and deploys extensions.
 * 
 * @author Nathan Jensen
 */
@SuppressWarnings({"unchecked", "unused"})
public class ExtensionDeployerImpl extends CommonDependencyInjection implements ExtensionDeployer, Runnable {
	protected final Log logger = LogFactory.getLog(getClass());

	private String infPrefix = "WEB-INF";
	private final static String TSFILE="timestamps"; 
	private final static String LOCKFILE="lockfile";
	private final static String PICKUP="pickup";
	private final static String REMOVAL="removal";

	private TemplateModule templateModule;
	private DefinitionModule definitionModule;
	private ZoneClassManager zoneClassManager;
	private AdminModule adminModule;
	private ZoneModule zoneModule;
	private BinderModule binderModule;
	
	public BinderModule getBinderModule() {
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

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

	public ZoneModule getZoneModule() {
		return zoneModule;
	}

	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

	/**
	 * Called on a timer.  Need to handle all zones
	 *
	 */
	@Override
	public void check() {
		if(ContextListenerPostSpring.isStartupInProgress()) {
			if(logger.isDebugEnabled())
				logger.debug("Server still starting up, skipping this round");
			return;
		}
		
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
		getAdminModule().checkAccess(AdminOperation.manageExtensions);
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
		
		//Check for the local web-inf extensions dir
		String localExtensionDir = DirPath.getExtensionBasePath() + File.separator + Utils.getZoneKey();
		File localDir = new File(localExtensionDir);
		if (!localDir.exists()) localDir.mkdirs();
		try {
			//Get a handle to or create shared Time Stamp File.. 
			File sharedTimeFile = new File(sharedDir, TSFILE);
			if (!sharedTimeFile.exists()) sharedTimeFile.createNewFile();
			Properties shared = toProperties(sharedExtensionDir + File.separator + TSFILE);
			//Get a handle to or create local time stamp file - this is under the WEB-INF extensions dir
			File localTimeFile = new File(localDir, TSFILE);
			Properties local = toProperties(localExtensionDir + File.separator + TSFILE);
			//Get a list of files that are of type zip - under the shared dir
			File[] extensions = sharedDir.listFiles(new FileOnlyFilter());
			//If there are files to deploy
			if (extensions != null && extensions.length > 0) {
				//get the current date
				String deployedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ENGLISH).format(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
				//try and deploy each extension
				for (int i=0; i<extensions.length; ++i) {
					//Get a handle or create the pickup dir exists
					File successDir = new File(sharedDir, PICKUP);
					if (!successDir.exists()) successDir.mkdirs();
					File extension = extensions[i];
					try {
						//deploy the extension
						deploy(extension, true, deployedDate);
						//now move the extension to the pickup directory
						FileHelper.move(extension, new File(successDir, extension.getName()));
						//check and remove the extension from the removal dir
						removeExtensionFromRemovalDir(sharedDir, extension.getName());
						//update the TSProperties of the shared and local properties with the deployed date
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
				//update shared TS file on disk
				shared.store(new FileOutputStream(sharedExtensionDir + File.separator + TSFILE), null);				
			}
			//This code is more for clustered servers,  an extension will already be in the pickup dir
			//However, the localTimeFile will not exist
			//Also, the second part of the or handles the case where there is an updated version deployed in the cluster
			if (!localTimeFile.exists() || sharedTimeFile.lastModified() > localTimeFile.lastModified()) {
				//now see if another node in the cluster did initial deploy -> time to do local deploy
				for (Iterator iter=shared.keySet().iterator(); iter.hasNext();) {
					String key = (String)iter.next();
					//check local date
					String date = local.getProperty(key);
					if (Validator.isNull(date) || !date.equals(shared.getProperty(key))) {
						//	deploy locally
						try {
							deploy(new File(sharedExtensionDir + File.separator + PICKUP + File.separator + key), false, shared.getProperty(key));
						} catch (IOException e) {
							logger.error("Unable to deploy extension  " + key, e);
						}
					}
				}
				
				//check in removal dir
				File removalDir = new File(sharedDir, REMOVAL);
				if(removalDir.exists()) {
					File[] removedExtensions = removalDir.listFiles(new FileOnlyFilter());
					//If there are files to deploy
					if (removedExtensions != null && removedExtensions.length > 0) {
						for (int i=0; i<removedExtensions.length; ++i) {
							File removeExtension = removedExtensions[i];
							//Get the name and see if it is in the local TS, if it is then remove it
							remove(removeExtension);
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
	
	private void removeExtensionFromRemovalDir(File sharedDir, String extensionName){
		File removalDir = new File(sharedDir, REMOVAL);
		if(removalDir.exists()) {
			File extFile = new File(removalDir, extensionName);
			if(extFile.delete()) {
				logger.info("Deleted extension from the removal dir: "+extFile.getPath());
			}
		}
	}
	
	@Override
	public void deploy(File extension, boolean full, String deployedDate) throws IOException {
		getAdminModule().checkAccess(AdminOperation.manageExtensions);
		String zoneKey = Utils.getZoneKey();
		logger.info("Deploying new extension from " + extension.getPath());
		SAXReader reader = XmlUtil.getSAXReader(false);
		reader.setIncludeExternalDTDDeclarations(false);
		
		//Get the extension name
		final String extensionPrefix = extension.getName().substring(0, extension.getName().lastIndexOf("."));
		
		//Extension dir under WEB-INF
		File extensionDir = new File(DirPath.getExtensionBasePath() + File.separator + zoneKey + File.separator + extensionPrefix);
		extensionDir.mkdirs();
		
		//Extension dir under webapp
		File extensionWebDir = new File(DirPath.getExtensionWebPath() + File.separator + zoneKey +  File.separator + extensionPrefix);
		extensionWebDir.mkdirs();
		
		//zipFile - file under shared directory
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
			//List foundList = findExtensions(extension.getName(), RequestContextHolder.getRequestContext().getZoneId());
		}
		if (full) {
			
			ExtensionInfo extInfo = createExtensionInfo(extension, extensionPrefix, extensionWebDir, deployedDate);
			
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
						SAXReader xIn = XmlUtil.getSAXReader(false);
						final Document document = xIn.read(definition);
						
							// record the "owning" extension
						document.getRootElement().add(new AbstractAttribute() {
								private static final long serialVersionUID = -7880537136055718310L;
								@Override
								public QName getQName() {
									return new QName(ObjectKeys.XTAG_ATTRIBUTE_EXTENSION, document
											.getRootElement().getNamespace());
								}
								@Override
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
						SAXReader xIn = XmlUtil.getSAXReader(false);
						final Document document = xIn.read(template);
						// attempt to add
						getTemplateModule().addTemplate(null, document, true);
					} catch (DocumentException e) {
						logger.warn("Malformed template file " + extension.getPath(), e);
					}
				}
			}

			List extList = findExtensions(RequestContextHolder.getRequestContext().getZoneId());
			if (extList.contains(extInfo)) {
				//Extension already exists, then lets update existing extension
				List foundList = findExtensions(extensionPrefix, RequestContextHolder.getRequestContext().getZoneId());
				if(foundList.size() > 0)
				{
					ExtensionInfo foundExtInfo = (ExtensionInfo) foundList.get(0);
					logger.info("Extension found: updated extension " + foundExtInfo.getName());
					foundExtInfo.setDateDeployed(deployedDate);
					updateExtension(extInfo, foundExtInfo);
				}
			} else {
				extInfo.setDateDeployed(deployedDate);
				addExtension(extInfo);
			}
		}
		//Now move libraries into classpath
		getZoneClassManager().addExtensionLibs(extensionDir);
		
		logger.info("Extension deployed successfully from " + extension.getPath());
	}


	public void setInfPrefix(String infPrefix) {
		this.infPrefix = infPrefix;
	}
	protected class XMLFilter implements FilenameFilter {
	    @Override
		public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".xml"));
	    }
	}
	protected class LibFilter implements FilenameFilter {
	    @Override
		public boolean accept(File dir, String name) {
	        return (name.toLowerCase().endsWith(".jar"));
	    }
	}
	protected class FileOnlyFilter implements FileFilter {
	    @Override
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
	
	//1. Done - Remove Definitions, Templates, 
	//2. Cannot Remove from ClassLoader - may require a restart
	//3. Done - Remove Dirs
	//3. Done - Remove keys from TS properties 
	@Override
	public boolean remove(File extension) {
		boolean removeFiles = false;
		logger.info("Begin remove extension " + extension.getPath());
		String zoneKey = Utils.getZoneKey();
		
		//Get the extension name
		final String extensionPrefix = extension.getName().substring(0, extension.getName().lastIndexOf("."));
		//Extension dir under WEB-INF
		File extensionDir = new File(DirPath.getExtensionBasePath() + File.separator + zoneKey + File.separator + extensionPrefix);
		
		boolean removedTemplates = removeTemplates(extensionDir);
		boolean removedDefinitions = removeDefinitions(extensionDir);
		
		//if we can remove the templates and defintions then we can go ahead and remove the files associated with the extension
		if(removedTemplates && removedDefinitions){
			removeFiles = true;
		}
		
		if( removeFiles && extensionDir.exists() ) {
			logger.info("Removing extension files: " + extensionDir );
			removeFiles(extensionDir);
		}
		
		//Extension dir under webapp
		File extensionWebDir = new File(DirPath.getExtensionWebPath() + File.separator + zoneKey +  File.separator + extensionPrefix);
		if( removeFiles && extensionWebDir.exists() ) {
			logger.info("Removing extension files: " + extensionWebDir );
			removeFiles(extensionWebDir);
		}
		
		return removeFiles;
	}
	
	private void removeFiles(File path){
		if( path.isDirectory() ) {
			File[] files = path.listFiles();
			int cnt = ((files != null)? files.length : 0);
			for(int i=0; i < cnt; i++){
				File file = files[i];
				removeFiles(file);
			}
			//remove the directory when it is empty
			path.delete();
		} else {
			//remove the file
			path.delete();
		}
	}
	
	private boolean removeDefinitions(File extensionDir){
		
		boolean removedDefinitions = true;
		
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
					SAXReader xIn = XmlUtil.getSAXReader(false);
					
					final Document document = xIn.read(definition);
					
					// Get the extension name
					final String extensionPrefix = extensionDir.getName();
							
						// record the "owning" extension
					document.getRootElement().add(new AbstractAttribute() {
							private static final long serialVersionUID = -7880537136055718310L;
							@Override
							public QName getQName() {
								return new QName(ObjectKeys.XTAG_ATTRIBUTE_EXTENSION, document
										.getRootElement().getNamespace());
							}
							@Override
							public String getValue() {
								return extensionPrefix;
							}
						});
					
			    	Element root = document.getRootElement();
					String name = null;
					String title = null;
					
					if (Validator.isNull(name)) name = root.attributeValue("name");
					if (Validator.isNull(name)) name = DefinitionUtils.getPropertyValue(root, "name");
					if (Validator.isNull(title)) title = root.attributeValue("caption");
					if (Validator.isNull(title)) title = DefinitionUtils.getPropertyValue(root, "caption");
					if (Validator.isNull(name)) {
						name=title;
					}

					if(name.startsWith("_")){
						logger.info("Should not remove default definitions - skipping: "+name);
						continue;
					}
					Definition def = getDefinitionModule().getDefinitionByName(null, true, name);
					logger.info("Removing definition: " + def.getName());
					getDefinitionModule().deleteDefinition(def.getId());
					
					// attempt to add
					//defs.add(getDefinitionModule().addDefinition(document, null, true).getId());
				} catch (DocumentException e) {
					removedDefinitions = false;
					logger.warn("Malformed definition file " 	+ extensionDir.getPath(), e);
				} catch (NoDefinitionByTheIdException e){
					removedDefinitions = true;
					logger.warn(e.getMessage());
				} catch (Exception e) {
					removedDefinitions = false;
					logger.warn("Error removing definition " 	+ extensionDir.getPath(), e);
				}
			}
			
		}
		
		return removedDefinitions;
	}
	
	private boolean removeTemplates(File extensionDir){
		boolean removedTemplates = true;
		
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
					SAXReader xIn = XmlUtil.getSAXReader(false);
					final Document document = xIn.read(template);
					
					 Element config = document.getRootElement();
					 //check name
					 String name = (String)XmlUtils.getCustomAttribute(config, ObjectKeys.XTAG_BINDER_NAME);
					 logger.info("Removing template: " + name);
					 if (Validator.isNull(name)) {
						 name = (String)XmlUtils.getCustomAttribute(config, ObjectKeys.XTAG_TEMPLATE_TITLE);
						 if (Validator.isNull(name)) {
							 throw new IllegalArgumentException(NLT.get("general.required.name"));
						 }
					 }
					 String internalId = config.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID);
					 if (Validator.isNull(internalId)) {
						 
						 TemplateBinder templateBinder = null;
						 try
						 {
							 templateBinder = getTemplateModule().getTemplateByName(name);
							 
						 } catch (NoBinderByTheNameException nameEx) {
							 //must already be removed....
							 return true;
						 }
						 	
						 if(templateBinder != null){
							 Long configId = templateBinder.getId();
							 getBinderModule().deleteBinder(configId);
						 }

					 } 
				} catch (DocumentException e) {
					removedTemplates = false;
					logger.warn("Malformed template file " + extensionDir.getPath(), e);
				} catch (Exception e) {
					removedTemplates = false;
					logger.warn("Error removing definition " 	+ extensionDir.getPath(), e);
				}
			}
		}
		
		return removedTemplates;
	}
	
	//1. Move Extension to removal dir
	@Override
	public boolean removeExtension(ExtensionInfo ext) {
		getAdminModule().checkAccess(AdminOperation.manageExtensions);
		
		logger.info("Removing Extension from the filesystem");
		
		ZoneInfo zone = getZoneModule().getZoneInfo(ext.getZoneId());
		String zoneKey = Utils.getZoneKey(zone);
		
		String sharedExtensionDir = SPropsUtil.getDirPath("data.extension.root.dir") + "extensions" + File.separator + zoneKey ;
		File sharedDir = new File(sharedExtensionDir);		
		//if the shareDir does exist, we cann't remove the extension
		if (!sharedDir.exists()) return false;
		
		//this file controls access to the shared extensions directory which is checked by all cluster members
		// and the local extensions web-inf directory.  In other words, the web-inf directory is only updated when this lock is held
		LockFile lock = new LockFile(new File(sharedDir, LOCKFILE));
		if (!lock.getLock()) {
			logger.info("Could not get the deploy lock");
			return false;  //try again later
		}

		//Check for the local web-inf extensions dir
		String localExtensionDir = DirPath.getExtensionBasePath() + File.separator + zoneKey;
		File localDir = new File(localExtensionDir);
		if( !localDir.exists() ) {
			logger.error("Could not find the local Extension dir " + localDir.getPath());
			return false;
		}
		try {
			// Get a handle to or create shared Time Stamp File..
			File sharedTimeFile = new File(sharedDir, TSFILE);
			if (!sharedTimeFile.exists())
				sharedTimeFile.createNewFile();
			Properties shared = toProperties(sharedExtensionDir
					+ File.separator + TSFILE);

			//Get a handle to or create local time stamp file - this is under the WEB-INF extensions dir
			File localTimeFile = new File(localDir, TSFILE);
			Properties local = toProperties(localExtensionDir + File.separator + TSFILE);
			
			String extensionName = ext.getName();

			// Get a handle or create the pickup dir exists
			File removalDir = new File(sharedDir, REMOVAL);
			if (!removalDir.exists())
				removalDir.mkdirs();

			File pickUpDir = new File(sharedDir, PICKUP);
			if (pickUpDir.exists()) {

				// get the file list
				// Get a list of files that are of type zip - under the shared dir
				File foundExtension = null;
				File[] extensions = pickUpDir.listFiles(new FileOnlyFilter());
				// If there are files to deploy
				if (extensions != null && extensions.length > 0) {
					for (int i = 0; i < extensions.length; ++i) {
						File extension = extensions[i];
						// Get the extension name
						final String extensionPrefix = extension.getName()
								.substring(0,
										extension.getName().lastIndexOf("."));
						if (extensionPrefix.equals(extensionName)) {
							foundExtension = extension;
							break;
						}
					}
				}

				if(foundExtension != null){
					
					if( checkDefinitionsInUse(foundExtension, zoneKey) )
						throw new DefinitionInvalidOperation();
					
					//remove the WEB-INF and files under webapp
					boolean okToRemove = remove(foundExtension);
					if(okToRemove) {
						// now move the extension to the removal directory
						FileHelper.move(foundExtension, new File(removalDir,
								foundExtension.getName()));

						// remove the extenison from the pickup directory
						logger.info("Removing the extension from the pickup directory");
						foundExtension.delete();

						// update the TSProperties of the shared and local
						// properties with the deployed date
						logger.info("Removing the extension key from the shared timestamp file.");
						shared.remove(foundExtension.getName());

						//update shared TS file on disk
						shared.store(new FileOutputStream(sharedExtensionDir + File.separator + TSFILE), null);		

						//the date isn't copied, but all that matters is that sharedTime is less than it.  
						//No other updates to sharedTime can happen until after this new localTime.
						FileCopyUtils.copy(sharedTimeFile, localTimeFile);	

						deleteExtension(ext);
					} else {
						//TODO should we send error back to the client...
					}
				} else {
					logger.error("No extension was found could not remove.");
				}
			}
		} 
		catch (Exception ex) {

			if(ex instanceof DefinitionInvalidOperation){
				throw (DefinitionInvalidOperation) ex;
			}
			
			logger.error("Error in deployer", ex);
		}
		finally {
			lock.releaseLock();
		}		
		
		return true;
	}
	
	@Override
	public boolean checkDefinitionsInUse(ExtensionInfo ext){

		ZoneInfo zone = getZoneModule().getZoneInfo(ext.getZoneId());
		String zoneKey = Utils.getZoneKey(zone);
		
		String sharedExtensionDir = SPropsUtil.getDirPath("data.extension.root.dir") + "extensions" + File.separator + zoneKey ;
		File sharedDir = new File(sharedExtensionDir);		
		//if the shareDir does exist, we cann't remove the extension
		if (!sharedDir.exists()) return false;
		
		String extensionName = ext.getName();
		File pickUpDir = new File(sharedDir, PICKUP);
		if (pickUpDir.exists()) {

			// get the file list
			// Get a list of files that are of type zip - under the shared dir
			File foundExtension = null;
			File[] extensions = pickUpDir.listFiles(new FileOnlyFilter());
			// If there are files to deploy
			if (extensions != null && extensions.length > 0) {
				for (int i = 0; i < extensions.length; ++i) {
					File extension = extensions[i];
					// Get the extension name
					final String extensionPrefix = extension.getName().substring(0, extension.getName().lastIndexOf("."));
					if (extensionPrefix.equals(extensionName)) {
						foundExtension = extension;
						break;
					}
				}
			}

			if(foundExtension != null){
				if( checkDefinitionsInUse(foundExtension, zoneKey) )
					return true; //throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
			}
		}
			
		return false;
	}
	
	private boolean checkDefinitionsInUse(File extension, String zoneKey) {
		boolean inUse = false;
		
		logger.info("Begin remove extension " + extension.getPath());
		
		//Get the extension name
		final String extensionPrefix = extension.getName().substring(0, extension.getName().lastIndexOf("."));
		//Extension dir under WEB-INF
		File extensionDir = new File(DirPath.getExtensionBasePath() + File.separator + zoneKey + File.separator + extensionPrefix);
		
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
					SAXReader xIn = XmlUtil.getSAXReader(false);
					
					final Document document = xIn.read(definition);
					// record the "owning" extension
					document.getRootElement().add(new AbstractAttribute() {
							private static final long serialVersionUID = -7880537136055718310L;
							@Override
							public QName getQName() {
								return new QName(ObjectKeys.XTAG_ATTRIBUTE_EXTENSION, document
										.getRootElement().getNamespace());
							}
							@Override
							public String getValue() {
								return extensionPrefix;
							}
						});
					
			    	Element root = document.getRootElement();
					String name = null;
					String title = null;
					
					if (Validator.isNull(name)) name = root.attributeValue("name");
					if (Validator.isNull(name)) name = DefinitionUtils.getPropertyValue(root, "name");
					if (Validator.isNull(title)) title = root.attributeValue("caption");
					if (Validator.isNull(title)) title = DefinitionUtils.getPropertyValue(root, "caption");
					if (Validator.isNull(name)) {
						name=title;
					}

					Definition def = getDefinitionModule().getDefinitionByName(null, true, name);
					inUse = getDefinitionModule().checkDefInUse(def.getId());
					
					if(inUse){
						logger.info("Found definition in use: "+def.getName());
						break;
					}
					
				} catch (DocumentException e) {
					logger.warn("Malformed definition file " 	+ extensionDir.getPath(), e);
				} catch (NoDefinitionByTheIdException e){
					logger.warn(e.getMessage());
				} catch (Exception e) {
					logger.warn("Error checking definition is in use " 	+ extensionDir.getPath(), e);
				}
			}
		}
		
		return inUse;
	}
	
	
	@Override
	public void addExtension(ExtensionInfo extension) {
		getAdminModule().addExtension(extension);
	}

	/**
	 * Remove the extensionInfo object from the database.
	 * 
	 */
	@Override
	public boolean deleteExtension(ExtensionInfo extension) {
		
		boolean retValue = true;

		getAdminModule().deleteExtension(extension.getId());
		return retValue;
	}

	@Override
	public List findExtensions() {
//		OrderBy order = new OrderBy();
//		order.addColumn("name");
//		FilterControls filter = new FilterControls();
//		filter.setOrderBy(order);
//		filter.setZoneCheck(false);
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
//		return getCoreDao().loadObjects(ExtensionInfo.class, filter, zoneId);
		return findExtensions(zoneId);
	}
	
	@Override
	public List findExtensions(Long zoneId) {
		OrderBy order = new OrderBy();
		order.addColumn("name");
		FilterControls filter = new FilterControls();
		filter.setOrderBy(order);
		return getCoreDao().loadObjects(ExtensionInfo.class, filter, zoneId);
	}

	@Override
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

	@Override
	public void updateExtension(ExtensionInfo newInfo, ExtensionInfo existingInfo) {
		
		existingInfo.setAuthor(newInfo.getAuthor());
		existingInfo.setAuthorEmail(newInfo.getAuthorEmail());
		existingInfo.setAuthorSite(newInfo.getAuthorSite());
		existingInfo.setDateCreated(newInfo.getDateCreated());
		existingInfo.setDescription(newInfo.getDescription());
		existingInfo.setTitle(newInfo.getTitle());
		existingInfo.setType(newInfo.getType());
		existingInfo.setVersion(newInfo.getVersion());
		
		getAdminModule().modifyExtension(existingInfo);
	}
	@Override
	public List findExtensions(String name, Long zoneId) {
		return getCoreDao().loadObjects(new ObjectControls(ExtensionInfo.class), new FilterControls("name", name), zoneId);
	}
	
	private ExtensionInfo createExtensionInfo(File extension, String extensionPrefix, File extensionWebDir, String deployedDate){
		//Add the extension info to database
		ExtensionInfo extInfo = new ExtensionInfo();
		extInfo.setName(extensionPrefix);
		extInfo.setTitle(extensionPrefix);
		extInfo.setDescription(NLT.get("administration.extensions.pluginMissing"));
		extInfo.setDateDeployed(deployedDate);
		extInfo.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
				
		File installFile = new File(extensionWebDir.getAbsolutePath() + File.separator + "install.xml");
		if(!installFile.exists()){
			//missing the install.xml file
			logger.info("Missing the install.xml file from: "+extensionPrefix);
			return extInfo;
		}
		
		try {
			SAXReader xInstall = XmlUtil.getSAXReader(false);
			final Document installDoc = xInstall.read(installFile);
			if( installDoc != null && installDoc.getRootElement() != null) {
				
				if(installDoc.getRootElement().attributeValue("version") != null ){
					String version = installDoc.getRootElement().attributeValue("version");
					extInfo.setVersion(version);
				}
				if(installDoc.getRootElement().selectSingleNode("./title") != null){
					Element titleEle = (Element)installDoc.getRootElement().selectSingleNode("./title");
					String title = titleEle.getText();
					extInfo.setTitle(title);
				}
				if(installDoc.getRootElement().selectSingleNode("./author") != null){
					Element authorEle = (Element)installDoc.getRootElement().selectSingleNode("./author");
					String author = authorEle.getText();
					extInfo.setAuthor(author);
				}
				if(installDoc.getRootElement().selectSingleNode("./authorEmail") != null){
					Element authorEmailEle = (Element)installDoc.getRootElement().selectSingleNode("./authorEmail");
					String authorEmail = authorEmailEle.getText();
					extInfo.setAuthorEmail(authorEmail);
				}
				if(installDoc.getRootElement().selectSingleNode("./authorUrl") != null){
					Element authorSiteEle = (Element)installDoc.getRootElement().selectSingleNode("./authorUrl");
					String authorSite = authorSiteEle.getText();
					extInfo.setAuthorSite(authorSite);
				}
				if(installDoc.getRootElement().selectSingleNode("./creationDate") != null){
					Element creationDateEle = (Element)installDoc.getRootElement().selectSingleNode("./creationDate");
					String createDate = creationDateEle.getText();
					extInfo.setDateCreated(createDate);
				}
				if(installDoc.getRootElement().selectSingleNode("./description") != null){
					Element descrEle = (Element)installDoc.getRootElement().selectSingleNode("./description");
					String description = descrEle.getText();
					extInfo.setDescription(description);
				}
			} 
		} catch (DocumentException e) {
			logger.warn("Malformed template file " 	+ extension.getPath(), e);
		}
		
		return extInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.check();
	}
	
}

