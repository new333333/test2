package com.sitescape.team.module.shared;

import static com.sitescape.team.module.shared.XmlUtils.maybeGetText;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.sitescape.team.support.AbstractTestBase;

public class XmlUtilsTest extends AbstractTestBase {

	private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><outer><inner value='success' /></outer>";
	
	@Test
	public void maybeGetTextSome() throws Exception {
		SAXReader r = new SAXReader(false);
		Document d = r.read(new StringReader(xml));
		assertEquals("success", maybeGetText(d, "/outer/inner/@value", "failure"));
	}
	
	@Test
	public void maybeGetTextNothing() throws Exception {
		SAXReader r = new SAXReader(false);
		Document d = r.read(new StringReader(xml));
		assertEquals("no-name", maybeGetText(d, "/outer/inner/@name", "no-name"));
	}
}
