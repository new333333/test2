/**
 * 
 */
package com.sitescape.team.module.extension.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.extension.ExtensionDeployNotifier;
import com.sitescape.team.module.extension.ExtensionDeployer;
import com.sitescape.team.module.template.TemplateModule;

/**
 * @author dml
 * 
 * Listens for and deploys war-based extensions.
 * 
 */
public class WarExtensionDeployer<S extends ExtensionDeployNotifier<S>>
		implements ExtensionDeployer<S> {

	private static final Logger log = LoggerFactory
			.getLogger(WarExtensionDeployer.class);

	private DefinitionModule definitionModule;
	private TemplateModule templateModule;
	private String configurationFileExtension = "xml";
	private Namespace schemaInstanceNamespace = new Namespace("xsi",
			"http://www.w3.org/2001/XMLSchema-instance");
	private String schemaLocationAttribute = "schemaLocation";
	private QName schemaAttribute = new QName(schemaLocationAttribute,
			schemaInstanceNamespace);
	private String definitionsSchemaNamespace = "http://www.icecore.org/definition";
	private String templateSchemaNamespace = "http://www.icecore.org/template";
	private String extensionAttr = "extension";
	private File extensionBaseDir = new File("web/docroot/WEB-INF/opt/");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitescape.team.util.EventListener#onNotification(com.sitescape.team.util.EventSource,
	 *      java.lang.Object)
	 */
	public void onNotification(S source, File event) {
		deploy(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitescape.team.module.extension.ExtensionDeployer#deploy(java.io.File)
	 */
	public void deploy(final File extension) {
		JarInputStream warIn;
		try {
			warIn = new JarInputStream(new FileInputStream(extension), true);
		} catch (IOException e) {
			log.warn("Unable to open extension WAR at " + extension.getPath(),
					e);
			return;
		}
		SAXReader reader = new SAXReader(false);
		File extensionDir = new File(extensionBaseDir, extension.getName()
				.substring(0, extension.getName().lastIndexOf(".")));
		extensionDir.mkdirs();
		try {
			for (JarEntry entry = warIn.getNextJarEntry(); entry != null; entry = warIn
					.getNextJarEntry()) {
				// extract file to proper extension directory
				File inflated = new File(extensionDir, entry.getName());
				if (entry.isDirectory()) {
					inflated.mkdirs();
					continue;
				}
				inflated.getParentFile().mkdirs();
				FileOutputStream entryOut = new FileOutputStream(inflated);
				if (!entry.getName().endsWith(configurationFileExtension)) {
					// not an extension configuration file, just inflate
					IOUtils.copy(warIn, entryOut);
					entryOut.close();
					continue;
				}
				try {
					// note: all bytes read by SAXReader are inflated to entryOut
					final Document document = reader.read(new TeeInputStream(
							new CloseShieldInputStream(warIn), entryOut, true));
					
					Attribute schema = document.getRootElement().attribute(
							schemaAttribute);
					if (schema == null || StringUtils.isBlank(schema.getText())) {
						// unspecified xml schema, don't attempt processing
						continue;
					}
					if (schema.getText().contains(definitionsSchemaNamespace)) {
						// declared to be a definition
						// record the "owning" extension
						document.getRootElement().add(new AbstractAttribute() {
							private static final long serialVersionUID = -7880537136055718310L;
							public QName getQName() {
								return new QName(extensionAttr, document
										.getRootElement().getNamespace());
							}
							public String getValue() {
								return extension.getName().substring(0,
										extension.getName().lastIndexOf("."));
							}
						});
						// attempt to add
						definitionModule.addDefinition(document, true);
						continue;
					}
					if (schema.getText().contains(templateSchemaNamespace)) {
						// declared to be a template, attempt to add
						templateModule.addTemplate(document, true);
						continue;
					}
				} catch (DocumentException e) {
					log.warn("Malformed XML file in extension war at "
							+ extension.getPath(), e);
					// don't continue trying to deploy internally broken war
					return;
				} 
			}
		} catch (IOException e) {
			log.warn("Malformed extension war at " + extension.getPath(), e);
			return;
		} finally {
			try {
				warIn.close();
			} catch (IOException e) {
				log.warn("Unable to close extension war at " + extension.getPath(), e);
				return;
			}
		}
	}

	@Autowired
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	@Autowired
	public void setTemplateModule(TemplateModule templateModule) {
		this.templateModule = templateModule;
	}

	public void setConfigurationFileExtension(String definitionFileExtension) {
		this.configurationFileExtension = definitionFileExtension;
	}

	public void setSchemaInstanceNamespace(Namespace schemaInstanceNamespace) {
		this.schemaInstanceNamespace = schemaInstanceNamespace;
		this.schemaAttribute = new QName(schemaLocationAttribute,
				this.schemaInstanceNamespace);
	}

	public void setSchemaLocationAttribute(String schemaLocationAttribute) {
		this.schemaLocationAttribute = schemaLocationAttribute;
		this.schemaAttribute = new QName(schemaLocationAttribute,
				this.schemaInstanceNamespace);
	}

	public void setDefinitionsSchemaNamespace(String definitionsSchemaNamespace) {
		this.definitionsSchemaNamespace = definitionsSchemaNamespace;
	}

	public void setTemplateSchemaNamespace(String templateSchemaNamespace) {
		this.templateSchemaNamespace = templateSchemaNamespace;
	}

	public void setExtensionAttr(String extensionAttr) {
		this.extensionAttr = extensionAttr;
	}

	public void setExtensionBaseDir(File extensionBaseDir) {
		this.extensionBaseDir = extensionBaseDir;
	}
}
