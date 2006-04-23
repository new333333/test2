package com.sitescape.ef.ssfs.wck;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface CCClientCallback {

	void additionalInput(HttpServletRequest req, Map uri);
}
