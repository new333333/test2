/**
 * 
 */
package com.sitescape.team.liferay.events;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.liferay.portal.model.Company;
import com.liferay.portal.model.impl.UserImpl;
import com.sitescape.team.asmodule.bridge.BridgeUtil;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.SessionUtil;

/**
 * @author dml
 *
 */
public class LoginPostActionTest extends AbstractTestBase implements BeanClassLoaderAware {

	/**
	 * State-less class, not Spring instantiated (hence not injected)
	 */
	private LoginPostAction loginPostAction = new LoginPostAction();
	@Autowired(required = true)
	protected ResourceLoader resourceLoader;
	private ClassLoader classLoader;
	
	@Test
	@SuppressWarnings("unchecked")
	@Ignore("Doesn't actually perform tests, just sets up necessary mock infrastructure")
	public void runRedirectNonAdminFullScreen() throws Exception {
		String zone = "zone";
		Workspace z = setupWorkspace(zone).getSecond();
		User user = profileDao.findUserByName(adminUser, z.getId());
		UserImpl u0 = new UserImpl();
		u0.setScreenName(user.getName());
		
		MockServletContext c = new MockServletContext(resourceLoader);
		MockHttpSession s = new MockHttpSession(c);
		HttpServletRequest req = createMock(HttpServletRequest.class);
		SessionFactory sf = createMock(SessionFactory.class);
		Session hs = createNiceMock(Session.class);
		Company co = createMock(Company.class);
		RequestDispatcher requestDispatcher = createNiceMock(RequestDispatcher.class);
		expect(req.getSession(false)).andReturn(s);
		expect(req.getSession()).andReturn(s);
		expect(req.getAttribute("COMPANY_ID")).andReturn(1L);
		expect(req.getAttribute("COMPANY")).andReturn(co);
		expect(req.getAttribute("USER_ID")).andReturn(user.getId());
		expect(req.getAttribute("USER")).andReturn(u0);
		expect(co.getWebId()).andReturn(zone);
		expectLastCall().times(2);
		expect(sf.openSession()).andReturn(hs);
		expect(hs.isOpen()).andReturn(true);
		expect(hs.getSessionFactory()).andReturn(sf);
		expect(hs.getFlushMode()).andReturn(FlushMode.COMMIT);
		replay(req);
		replay(co);
		replay(sf);
		replay(hs);
		replay(requestDispatcher);
		MockHttpServletResponse res = new MockHttpServletResponse();
		RequestContext rc = fakeRequestContext();
		expect(rc.getUser()).andStubReturn(user);
		replay(rc);
		// XXX The necessity of the code below should bring anyone in their right mind to tears
		SessionUtil.setSessionFactory(sf);
		BridgeUtil.setClassLoader(classLoader);
		BridgeUtil.setCCDispatcher(requestDispatcher);
		TransactionSynchronizationManager.bindResource(sf, new SessionHolder(hs));
		
		loginPostAction.run(req, res);
	}

	@Required
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;		
	}

}
