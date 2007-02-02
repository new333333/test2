package com.sitescape.team.pipeline.impl;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.team.pipeline.Conduit;
import com.sitescape.team.pipeline.ConduitFactory;

public class RAMConduitFactory implements ConduitFactory {
	
	public Conduit open() throws UncheckedIOException {
		return new RAMConduit();
	}

}
