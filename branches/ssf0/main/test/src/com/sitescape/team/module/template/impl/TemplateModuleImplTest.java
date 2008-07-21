package com.sitescape.team.module.template.impl;

import static com.sitescape.team.util.Maybe.maybe;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.module.template.TemplateService;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.CollectionUtil.Func1;

public class TemplateModuleImplTest extends AbstractTestBase {
	
	private static final String name = "TemplateModuelImplTest-4778106701760087515L";
	private static final String schemaLocationAttr = "xsi:schemaLocation='http://www.icecore.org/template-0.1\n"
			+ "template.xsd'\n"; 
	private SAXReader reader = new SAXReader(false);
	
	@Autowired
	private TemplateService templateModule;
	@Autowired
	private ZoneModule zoneModule;
	
	@Test
	public void addTemplate() throws Exception {
		Document d = reader.read(new StringReader(getWithSchema()));
		TemplateBinder t = templateModule.addTemplate(d, true, zoneModule.getDefaultZone());
		assertNotNull(t);
		assertEquals(name, t.getName());
		
		RequestContext rc = fakeRequestContext(profileDao.findUserByName(SZoneConfig
				.getAdminUserName(zoneModule.getDefaultZone().getName()),
				zoneModule.getDefaultZone().getZoneId()));
		expect(rc.getZoneId()).andStubReturn(zoneModule.getDefaultZone().getZoneId());
		replay(rc);
		TemplateBinder t0 = templateModule.getTemplateByName(name);
		assertEquals(t, t0);
	}
	
	@Test
	public void getTemplateAsXmlHasSchemaLocation() throws Exception {
		Document d = reader.read(new StringReader(getWithoutSchema()));
		TemplateBinder t = templateModule.addTemplate(d, true, zoneModule.getDefaultZone());
		assertTrue(maybe(templateModule.getTemplateAsXml(t).getRootElement().attributeValue(new QName("schemaLocation", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")))).and(new Func1<String, Boolean>() {
			public Boolean apply(String x) {
				// regexp is approximate
				return x.matches("http://www.icecore.org/template(-[0-9\\.]*)?\\s+http://www.icecore.org/template(-[0-9\\.]*)?");
			}}, false));
	}
	
	private String getXml(String schema) {
		return  
		"<?xml version='1.0' encoding='UTF-8'?>\n" +
		"<template xmlns='http://www.icecore.org/template-0.1'\n" +
					"xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" +
					schema +
					"type='5'>\n" +
					"<attribute name='name' type='string'>" + name + "</attribute>\n" +
					"<definition name='test_template' databaseId='4778106701760087515L'/>\n" +
				"</template>\n";					
	}
	
	private String getWithSchema() {
		return getXml(schemaLocationAttr);
	}
	
	private String getWithoutSchema() {
		return getXml("");
	}

}
