package com.sitescape.team.taglib;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.extension.ExtensionDeployNotifier;
import com.sitescape.team.module.extension.impl.WarExtensionDeployer;
import com.sitescape.team.support.AbstractTestBase;

public class DisplayConfigurationTest<S extends ExtensionDeployNotifier<S>> extends AbstractTestBase {

	private static final String name = "extension-test";
	private static final String xml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<definition xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\n" + 
					"xsi:schemaLocation=\"http://www.icecore.org/definition-0.1\n" + 
										"definition_builder_config.xsd\n" +
					"caption=\"WarExtensionDeployerTest-2300224201166748020L\n" +
					"type=\"1\"\n" +
					"extension=\"" + name + "\">\n" + 
			"<item />\n" +
		"</definition>"; 
	private DisplayConfiguration dc = new DisplayConfiguration();
	@Autowired
	private WarExtensionDeployer<S> deployer;

	@Test
	@Ignore
	public void testDoStartTagFindExtensionJsp() throws Exception {
		SAXReader r = new SAXReader(false);
		Document def = r.read(new StringReader(xml));
		Document item = r.read(new StringReader(xml));
		Entry e = new Entry() {
			@Override
			public EntityType getEntityType() {
				return EntityType.none;
			}
		};
		dc.setConfigDefinition(def);
		dc.setConfigElement(item.getRootElement());
		dc.setConfigJspStyle("default");
		dc.setEntry(e);
	}

}
