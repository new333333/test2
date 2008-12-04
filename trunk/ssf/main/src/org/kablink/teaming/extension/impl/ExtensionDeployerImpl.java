/**
 * 
 */
package org.kablink.teaming.extension.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractAttribute;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.extension.ZoneClassManager;
import org.kablink.teaming.jobs.DeployExtension;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.util.Validator;
import org.springframework.util.FileCopyUtils;


/**
 * @author dml
 * 
 * Listens for and deploys jar-based extensions.
 * 
 */
public class ExtensionDeployerImpl implements ZoneSchedule,ExtensionDeployer {

	protected final Log logger = LogFactory.getLog(getClass());

	private String infPrefix = "WEB-INF";

	private TemplateModule templateModule;
	private DefinitionModule definitionModule;
	private ZoneClassManager zoneClassManager;
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
	protected DeployExtension getProcessor(Workspace zone) {
		String jobClass = SZoneConfig.getString(zone.getName(), "extensionConfiguration/property[@name='" + DeployExtension.DEPLOY_EXTENSION_JOB + "']");
    	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (DeployExtension)ReflectHelper.getInstance(jobClass);
    		} catch (Exception e) {
 			   logger.error("Cannot instantiate DeployExtension custom class", e);
    		}
    	}
    	return (DeployExtension)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultDeployExtension.class);		   		
	}
	//called on zone delete
	public void stopScheduledJobs(Workspace zone) {
		DeployExtension job =getProcessor(zone);
   		job.remove(zone.getId());
	}
	//called on zone startup
   public void startScheduledJobs(Workspace zone) {
		//primay zone support only
	   if (zone.isDeleted()) return;
	   DeployExtension job =getProcessor(zone);
	   //make sure a timeout job is scheduled for the zone
	   String secsString = (String)SZoneConfig.getString(zone.getName(), "extensionConfiguration/property[@name='" + DeployExtension.DEPLOY_EXTENSION_SECONDS + "']");
	   int seconds = 5*60;
	   try {
		   seconds = Integer.parseInt(secsString);
	   } catch (Exception ex) {};
	   String pathName = (String)SZoneConfig.getString(zone.getName(), "extensionConfiguration/property[@name='" + DeployExtension.DEPLOY_EXTENSION_PATHNAME + "']");
	   job.schedule(zone.getId(), pathName, seconds);
		   

    }

	public void deploy(File extension) throws IOException {
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
		//Now move libraries into classpath
		getZoneClassManager().addExtensionLibs(extensionDir);
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
}
