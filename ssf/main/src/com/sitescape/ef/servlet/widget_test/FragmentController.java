package com.sitescape.ef.servlet.widget_test;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.servlet.SAbstractController;

public class FragmentController extends SAbstractController {

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		String viewPath = "widget_test/view_fragment2";
		return new ModelAndView(viewPath);
	}
}
