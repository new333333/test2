package com.sitescape.team.module.extension.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.extension.ExtensionDeployNotifier;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.CollectionUtil.Predicate;

public class WarExtensionDeployerTest<S extends ExtensionDeployNotifier<S>>
		extends AbstractTestBase {

	private static final String testWarName = "extension-test"; 
	private static final String testWar = testWarName + ".war";
	private static final File extInfDir = new File("web/docroot/WEB-INF/opt/");
	private static final File extDir = new File("web/docroot/opt");
	private static final String definitionName = "WarExtensionDeployerTest-2300224201166748020L";
	private static final String templateName = "WarExtensionDeployerTest-4778106701760087515L";

	@Autowired
	private WarExtensionDeployer<S> deployer;
	@Autowired
	private S eventSource;
	@Autowired
	private DefinitionModule definitions;
	@Autowired
	private ResourceLoader loader;
	@Autowired
	private TemplateModule templates;
	
	@Test
	@SuppressWarnings("unchecked")
	public void registersDefinitions() throws Exception {
		deployer.deploy(loader.getResource(testWar).getFile());

		Definition def = definitions.getDefinitionByName(definitionName);
		assertNotNull(def);
		assertNotNull(def.getId());
		assertEquals(definitionName, def.getName());
	}

	@Test
	public void registersTemplates() throws Exception {
		deployer.deploy(loader.getResource(testWar).getFile());
		defaultRequestContext();
		TemplateBinder tb = templates.getTemplateByName(templateName);

		assertNotNull(tb);
		assertEquals(templateName, tb.getName());
	}
	
	@Test
	public void deployAddsExtensionAttrToDef() throws Exception {
		deployer.deploy(loader.getResource(testWar).getFile());

		Definition def = definitions.getDefinitionByName(definitionName);
		assertEquals(testWarName, def
				.getDefinition().selectSingleNode("/*/@" + ObjectKeys.XTAG_ATTRIBUTE_EXTENSION).getText());
	}
	
	@Test
	public void deploysInfFiles() throws Exception {
		FileUtils.cleanDirectory(extInfDir);

		File war = loader.getResource(testWar).getFile();
		deployer.deploy(war);
		JarInputStream is = new JarInputStream(new FileInputStream(war));
		final List<String> names = new ArrayList<String>();
		for (JarEntry e = is.getNextJarEntry(); e != null; e = is.getNextJarEntry()) {
			if (!e.getName().startsWith("WEB-INF")) {
				// only look at WEB-INF/** for this test
				continue;
			}
			if (e.isDirectory() && e.getName().endsWith("/")) {
				names.add(e.getName().substring(0, e.getName().length() - 1));
				continue;
			}
			names.add(e.getName());
		}
		final List<File> fs = new ArrayList<File>();
		new ExtDirWalker(extInfDir, testWarName).getAll(fs);
		
		// remove all names which have been extracted
		List<String> fs0 = CollectionUtil.filter(new Predicate<String>() {
			public Boolean apply(String x) {
				for (File f : fs) {
					if (f.getPath().endsWith(x)) {
						return false;
					}
				}
				return true;
			}}, names);
		assertTrue(fs0.isEmpty());
	}
	
	@Test
	public void deploysWebFiles() throws Exception {
		FileUtils.cleanDirectory(extDir);

		File war = loader.getResource(testWar).getFile();
		deployer.deploy(war);
		JarInputStream is = new JarInputStream(new FileInputStream(war));
		final List<String> names = new ArrayList<String>();
		for (JarEntry e = is.getNextJarEntry(); e != null; e = is.getNextJarEntry()) {
			if (e.getName().startsWith("WEB-INF")) {
				// don't look at WEB-INF/** for this test
				continue;
			}
			if (e.isDirectory() && e.getName().endsWith("/")) {
				names.add(e.getName().substring(0, e.getName().length() - 1));
				continue;
			}
			names.add(e.getName());
		}
		final List<File> fs = new ArrayList<File>();
		new ExtDirWalker(extDir, testWarName).getAll(fs);
		
		// remove all names which have been extracted
		List<String> fs0 = CollectionUtil.filter(new Predicate<String>() {
			public Boolean apply(String x) {
				for (File f : fs) {
					if (f.getPath().endsWith(x)) {
						return false;
					}
				}
				return true;
			}}, names);
		assertTrue(fs0.isEmpty());
	}

	@Test
	public void onNotification() throws Exception {
		deployer.onNotification(eventSource, loader.getResource(testWar).getFile());
		
		defaultRequestContext();
		TemplateBinder tb = templates.getTemplateByName(templateName);

		assertNotNull(tb);
		assertEquals(templateName, tb.getName());
	}
	
	@Test
	public void overwritesOldDefinitions() throws Exception {
		deployer.deploy(loader.getResource(testWar).getFile());
		deployer.deploy(loader.getResource(testWar).getFile());		
	}
	
	private static class ExtDirWalker extends DirectoryWalker {
		private File context;
		private String extName;
		
		private ExtDirWalker(File context, String extName) {
			this.context = context;
			this.extName = extName;
		}
		
		public void getAll(List<File> fs) throws IOException {
			walk(new File(context, extName), fs);
		}
		@Override
		@SuppressWarnings("unchecked")
		protected boolean handleDirectory(File directory, int depth,
				Collection results) throws IOException {
			return results.add(directory);
		}
		@Override
		@SuppressWarnings("unchecked")
		protected void handleFile(File file, int depth, Collection results)
				throws IOException {
			results.add(file);
		}
	}
}
