/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.groovy;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.SPropsUtil;
import org.springframework.beans.factory.InitializingBean;

import com.liferay.util.StringUtil;

/**
 * @author jong
 *
 */
public class GroovyScriptService implements InitializingBean {
	
	private Log log = LogFactory.getLog(getClass());
	
	private GroovyScriptEngine engine;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		String[] paths = null;
		String overridePaths = SPropsUtil.getString("groovy.script.override.paths", "");
		if(!overridePaths.trim().equals("")) {
			log.debug("Groovy script override paths specified: " + overridePaths);
			paths = StringUtil.split(overridePaths, ",");
		}
		if(paths == null) {
			paths = new String[] {DirPath.getGroovyScriptPath()};
			log.debug("Groovy scripts in the default location: " + paths[0]);
		}
		engine = new GroovyScriptEngine(paths);
	}
	
	public Binding execute(String scriptName, Binding binding) throws ResourceException, ScriptException {
		engine.run(scriptName, binding);
		return binding;
	}
}
