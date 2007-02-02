package com.sitescape.team.servlet.forum;

import java.io.OutputStream;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.rss.RssGenerator;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.PrincipalServletRequest;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;

import org.springframework.web.bind.RequestUtils;

public class ViewRssController extends SAbstractController {
	
	public void initApplicationContext() {
		
	}
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Binder binder = getBinderModule().getBinder(binderId);
		User user = (User)request.getUserPrincipal();
		
		if(user == null) {
			// The request object has no information about authenticated user.
			// Note: It means that this is not a request made by the portal
			// through cross-context dispatch targeted to a SSF portlet. 
			HttpSession ses = request.getSession(false);

			if(ses != null) {
				user = (User) ses.getAttribute(WebKeys.USER_PRINCIPAL);
				
				if (user == null) {
					// No principal object is cached in the session.
					// Note: This occurs when a SSF web component (either a servlet
					// or an adapted portlet) is accessed BEFORE at least one SSF
					// portlet is invoked  by the portal through regular cross-context
					// dispatch. 
					user = RequestContextHolder.getRequestContext().getUser();
				}
			}
			else {
				throw new ServletException("No session in place - Illegal request sequence.");
			}
		}
		

		//response.getWriter(getRssGenerator().filterRss(binder,user));
			
		/*response.setContentType(mimeTypes.getContentType(shortFileName));*/
		response.resetBuffer();
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		
		OutputStream out = response.getOutputStream();
		byte[] buffer = getRssGenerator().filterRss(binder,user).getBytes();
		out.write(buffer, 0, buffer.length);

		out.flush();

		response.getOutputStream().flush();

		return null;
	}
}
