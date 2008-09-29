package com.sitescape.team.taglib.extension;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.RandomStringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.springframework.mock.web.MockBodyContent;
import org.springframework.mock.web.MockExpressionEvaluator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.taglib.extension.WebUrlTag;
import com.sitescape.team.web.WebKeys;

public class WebUrlTagTest extends AbstractTestBase {
	
	private String xml = "<a>some link</a>";
	private String url = "url";
	
	@Test
	public void doAfterBody() throws Exception {
		WebUrlTag ewut = new WebUrlTag();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		MockHttpServletRequest req = new MockHttpServletRequest();
		String extName = RandomStringUtils.randomAlphabetic(20);
		req.setAttribute(WebKeys.CONFIG_DEFINITION, getExtElem(extName));
		req.setContextPath("/ssf");
		MockPageContext pc = new MockPageContext(new MockServletContext(), req);
		new MockExpressionEvaluator(pc);
		ewut.setBodyContent(new MockBodyContent(xml, resp));
		ewut.setAttr("href");
		ewut.setUrl(url);
		ewut.setPageContext(pc);
		
		assertEquals(Tag.SKIP_BODY, ewut.doAfterBody());
		assertEquals("<a href=\"/ssf/opt/" + extName + "/" + url + "\">some link</a>", resp.getContentAsString());
	}
	
	private Document getExtElem(String extName) throws DocumentException {
		return new SAXReader(false).read(new StringReader("<item extension='" + extName + "' />"));
	}
}
