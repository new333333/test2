package com.sitescape.ef.servlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;

public class ViewCssController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		String theme = RequestUtils.getStringParameter(request, WebKeys.URL_CSS_THEME, "");
		Map model = new HashMap();
		if (!theme.equals("")) model.put(WebKeys.CSS_THEME, theme);
		response.setContentType("text/css");			
		String viewPath = "common/ssf_css";
		return new ModelAndView(viewPath);
	}
}
