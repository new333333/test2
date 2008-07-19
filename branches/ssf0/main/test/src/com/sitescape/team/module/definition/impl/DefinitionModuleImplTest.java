package com.sitescape.team.module.definition.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.support.AbstractTestBase;

public class DefinitionModuleImplTest extends AbstractTestBase {

	private String name = RandomStringUtils.randomAlphabetic(16);
	private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<definition xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "xsi:schemaLocation=\"http://www.icecore.org/definition-0.1\n"
			+ "definition_builder_config.xsd\"\n" + "name=\"" + name + "\"\n"
			+ "caption=\"" + name + "\"\n" + "type=\"1\"\n"
			+ "definitionType=\"1\">\n" + "<item />\n" + "</definition>";

	@Autowired
	private DefinitionModule definitions;

	@Test
	public void addDefinitionNoName() throws Exception {
		defaultRequestContext();
		Definition d = definitions.addDefinition(IOUtils.toInputStream(xml),
				true);
		assertNotNull(d);
		assertEquals(name, d.getName());
		assertEquals(null, d.getBinderId());
	}

	@Test
	public void addDefinitionWithBinderNoName() throws Exception {
		Workspace b = defaultRequestContext().getZone();
		Definition d = definitions.addDefinition(IOUtils.toInputStream(xml), b,
				true);
		assertNotNull(d);
		assertEquals(name, d.getName());
		assertEquals(b.getId(), d.getBinderId());
	}
	
	@Test
	public void getDefinitionByNameNoBinder() throws Exception {
		definitions.addDefinition(IOUtils.toInputStream(xml), true);
		Definition d = definitions.getDefinitionByName(name);
		assertNotNull(d);
		assertEquals(name, d.getName());
	}

}
