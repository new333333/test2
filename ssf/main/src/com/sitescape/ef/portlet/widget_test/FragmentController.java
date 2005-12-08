package com.sitescape.ef.portlet.widget_test;

import java.io.OutputStream;
import java.util.Enumeration;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.web.portlet.SAbstractController;

public class FragmentController extends SAbstractController {

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		//There is no action. Just go to the render phase
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		String path = "widget_test/view_fragment";
		String operation = request.getParameter("operation");
		
		if (operation != null) {
			if (operation.equals("showFragment")) {
				byte[] b = new byte[10];
				b[0] = 'H';
				b[1] = 'e';
				b[2] = 'l';
				b[3] = 'l';
				b[4] = 'o';
				
				//Write out the bytes
				Enumeration ct = request.getResponseContentTypes();
				response.setContentType("text/html");
				response.getPortletOutputStream().write(b);
				response.getPortletOutputStream().flush();
				path = "widget_test/view_fragment2";
			}
		}
		
		// Dispatch to the desired operation
		return new ModelAndView(path);
	}

}
