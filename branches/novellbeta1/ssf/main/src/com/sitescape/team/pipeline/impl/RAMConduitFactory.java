/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.pipeline.impl;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.pipeline.Conduit;
import com.sitescape.team.pipeline.ConduitFactory;

public class RAMConduitFactory implements ConduitFactory {
	
	public Conduit open() throws UncheckedIOException {
		return new RAMConduit();
	}

}
