package com.sitescape.ef.servlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

/**
 * Call an error page. This Class was created to help the scenario of when your loading an OutputStream
 * and encounter an error. You can not just redirect to an error jsp page in this case.
 * 
 * 
 * @author Rob
 *
 */
public class ErrorController extends SAbstractController {
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		Map model = new HashMap();
		
		response.setContentType("text/html");
		String viewPath = "forum/html_converter_error";
		return new ModelAndView(viewPath, model);
	}
}

