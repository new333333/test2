package org.kablink.teaming.taglib;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.definition.DefinitionService;
import org.kablink.teaming.module.extension.ExtensionDeployNotifier;
import org.kablink.teaming.security.function.WorkAreaOperation;

import org.kablink.teaming.support.AbstractTestBase;
import org.kablink.util.Pair;

public class DisplayConfigurationTest<S extends ExtensionDeployNotifier<S>> extends AbstractTestBase {

	private static final String name = "extension-test";
	private static final String extensionPath = "/opt/" + name + "/";
	private static final String jsp = "views/view.jsp"; 
	private static final String xml = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<definition xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
					"xsi:schemaLocation=\"http://www.icecore.org/definition-0.1\n" + 
										"definition_builder_config-0.1.xsd\"\n" +
					"caption=\"WarExtensionDeployerTest-2300224201166748020L\"\n" +
					"type=\"1\"\n" +
					"extension=\"" + name + "\">\n" +
				"<item name=\"date\">\n" +
					"<properties>\n" + 
						"<property name=\"caption\" value=\"ok\"/>\n" +
					"</properties>\n" +
					"<jsps>\n" +
						"<jsp name='custom' value='" + jsp + "' />\n" +
					"</jsps>\n" +
				"</item>\n" +
		"</definition>"; 
	private DisplayConfiguration dc = new DisplayConfiguration();
	@Autowired
	private DefinitionService definitions;

	@Test
	public void doStartTagFindExtensionJsp() throws Exception {
		SAXReader r = new SAXReader(false);
		Document def = r.read(new StringReader(xml));
		Entry e = new Entry() {
			@Override
			public EntityType getEntityType() {
				return EntityType.none;
			}
		};
		JspWriter jw = createMock(JspWriter.class);
		jw.print(isA(String.class));
		expectLastCall().anyTimes();
		replay(jw);
		RequestDispatcher rd = createMock(RequestDispatcher.class);
		rd.include(isA(ServletRequest.class), isA(ServletResponse.class));
		replay(rd);
		HttpServletRequest req = createMock(HttpServletRequest.class);
		expect(req.getRequestDispatcher(extensionPath + jsp)).andReturn(rd);
		req.setAttribute(isA(String.class), anyObject());
		expectLastCall().anyTimes();
		replay(req);
		PageContext pc = createMock(PageContext.class);
		expect(pc.getOut()).andReturn(jw).times(3);
		expect(pc.getRequest()).andReturn(req).times(2);
		expect(pc.getResponse()).andReturn(new MockHttpServletResponse());
		replay(pc);
		Pair<User, Workspace> p = setupWorkspace("test");
		addOperationFor(WorkAreaOperation.SITE_ADMINISTRATION, p.getFirst());
		RequestContext rc = fakeRequestContext(p);
		replay(rc);
		definitions.addDefinition(def, true);
		
		dc.setConfigDefinition(def);
		dc.setConfigElement(def.getRootElement());
		dc.setConfigJspStyle("default");
		dc.setEntry(e);
		dc.setId("1");
		dc.setPageContext(pc);
		dc.setProcessThisItem(false);
		
		int v = dc.doStartTag();
		assertEquals(TagSupport.SKIP_BODY, v);
		verify(req);
	}
}
