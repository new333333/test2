package com.sitescape.team.taglib.extension;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.RandomStringUtils;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockBodyContent;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Repeat;

import com.sitescape.team.support.AbstractTestBase;

public class XmlTransformTagTest extends AbstractTestBase {
	
	private final String xml = "<div><p>"
			+ RandomStringUtils.randomAlphabetic(20) + "</p></div>";
	private XmlTransformTag idt = new XmlTransformTag() {
		private static final long serialVersionUID = 0L;
		@Override
		public <Elements extends List<Element>> List<Element> apply(Elements es) {
			return es;
		}
	};
	private MockHttpServletResponse resp;
	private SecureRandom r = new SecureRandom(RandomStringUtils.random(10).getBytes());
	
	@Before
	public void setup() throws Exception {
		resp = new MockHttpServletResponse();
		idt.setBodyContent(new MockBodyContent(xml, resp));
	}
	
	@Test
	public void doAfterBody() throws Exception {
		assertEquals(Tag.SKIP_BODY, idt.doAfterBody());
		assertEquals(xml, resp.getContentAsString());
	}
	
	@Test
	@Repeat(value = 100)
	public void doAfterBodyList() throws Exception {
		int n = Math.abs(r.nextInt() % 20);
		StringBuilder xml = new StringBuilder();
		for (int i = 0; i < n; ++i) {
			xml.append("<div>" + RandomStringUtils.randomAlphabetic(Math.abs(r.nextInt() % 5) + 1) + "</div>");
		}
		idt.setBodyContent(new MockBodyContent(xml.toString(), resp));
		assertEquals(Tag.SKIP_BODY, idt.doAfterBody());
		assertEquals(xml.toString(), resp.getContentAsString());
	}
	
	@Test
	public void apply() throws Exception {
		List<Element> es = new ArrayList<Element>();
		es.add(new DefaultElement("div"));
		assertEquals(es, idt.apply(es));
	}
	
	@Test
	@Repeat(value = 100)
	public void applyList() throws Exception {
		List<Element> es = new ArrayList<Element>();
		int n = Math.abs(r.nextInt() % 20);
		for (int i = 0; i < n; ++i) {
			DefaultElement e = new DefaultElement("div");
			e.addCDATA(RandomStringUtils.randomAlphabetic(Math.abs(r.nextInt() % 5) + 1));
			es.add(e);
		}
		assertEquals(es, idt.apply(es));
	}

}
