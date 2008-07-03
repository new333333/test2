package com.sitescape.team.module.extension.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.extension.ExtensionDeployNotifier;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.security.dao.SecurityDao;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.CollectionUtil.Predicate;
import com.sitescape.util.Pair;

public class WarExtensionDeployerTest<S extends ExtensionDeployNotifier<S>>
		extends AbstractTestBase {

	private static final String testWarName = "extension-test"; 
	private static final String testWar = testWarName + ".war";
	private static final File extDir = new File("web/docroot/WEB-INF/opt/");
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
	private WorkAreaFunctionMembershipManager memberships;
	@Autowired
	private FunctionManager functions;
	@Autowired
	private SecurityDao security;
	@Autowired
	private TemplateModule templates;

	@Test
	@SuppressWarnings("unchecked")
	public void registersDefinitions() throws Exception {
		setupDeploy();

		deployer.deploy(loader.getResource(testWar).getFile());

		Definition def = definitions.getDefinitionByName(definitionName);
		assertNotNull(def);
		assertNotNull(def.getId());
		assertEquals(definitionName, def.getName());
	}

	@Test
	public void registersTemplates() throws Exception {
		setupDeploy();

		deployer.deploy(loader.getResource(testWar).getFile());
		TemplateBinder tb = templates.getTemplateByName(templateName);

		assertNotNull(tb);
		assertEquals(templateName, tb.getName());
	}
	
	@Test
	public void deployAddsExtensionAttrToDef() throws Exception {
		setupDeploy();

		deployer.deploy(loader.getResource(testWar).getFile());

		Definition def = definitions.getDefinitionByName(definitionName);
		assertEquals(testWarName, def
				.getDefinition().selectSingleNode("/*/@extension").getText());
	}
	
	@Test
	public void deploysFiles() throws Exception {
		FileUtils.cleanDirectory(extDir);
		setupDeploy();

		File war = loader.getResource(testWar).getFile();
		deployer.deploy(war);
		JarInputStream is = new JarInputStream(new FileInputStream(war));
		final List<String> names = new ArrayList<String>();
		for (JarEntry e = is.getNextJarEntry(); e != null; e = is.getNextJarEntry()) {
			if (e.isDirectory() && e.getName().endsWith("/")) {
				names.add(e.getName().substring(0, e.getName().length() - 1));
				continue;
			}
			names.add(e.getName());
		}
		final List<File> fs = new ArrayList<File>();
		new DirectoryWalker() {
			public void getAll() throws IOException {
				walk(new File(extDir, testWarName), fs);
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
		}.getAll();
		
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
		setupDeploy();

		deployer.onNotification(eventSource, loader.getResource(testWar).getFile());
		TemplateBinder tb = templates.getTemplateByName(templateName);

		assertNotNull(tb);
		assertEquals(templateName, tb.getName());
	}
	
	private void setupDeploy() {
		Pair<User, Workspace> p = setupWorkspace("zone");
		User u = p.getFirst();
		Workspace ws = p.getSecond();
		Application app = new Application();
		app.setTrusted(true);
		addOperationFor(WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS, u);
		addOperationFor(WorkAreaOperation.SITE_ADMINISTRATION, u);
		RequestContext rc = fakeRequestContext();
		expect(rc.getZone()).andStubReturn(ws);
		expect(rc.getZoneId()).andStubReturn(ws.getZoneId());
		expect(rc.getUser()).andStubReturn(u);
		expect(rc.getApplication()).andReturn(app).times(2);
		replay(rc);
	}

	private void addOperationFor(WorkAreaOperation op, User u) {
		Workspace ws = coreDao.findById(Workspace.class, u.getWorkspaceId());
		Function f = new Function();
		f.setName("test_" + op.getName());
		f.addOperation(op);
		f.setZoneId(ws.getZoneId());
		functions.addFunction(f);
		WorkAreaFunctionMembership mem = new WorkAreaFunctionMembership();
		mem.setZoneId(ws.getZoneId());
		mem.setFunctionId(f.getId());
		mem.setWorkAreaId(ws.getId());
		mem.setWorkAreaType(ws.getWorkAreaType());
		Set<Long> mIds = new HashSet<Long>();
		mIds.add(u.getId());
		mem.setMemberIds(mIds);
		memberships.addWorkAreaFunctionMembership(mem);

		assert security.checkWorkAreaFunctionMembership(ws.getZoneId(), ws
				.getId(), ws.getWorkAreaType(), op.getName(), mIds);
	}
}
