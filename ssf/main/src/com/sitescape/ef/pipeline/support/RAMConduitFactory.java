package com.sitescape.ef.pipeline.support;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.pipeline.Conduit;
import com.sitescape.ef.pipeline.ConduitFactory;

public class RAMConduitFactory implements ConduitFactory {
	
	public Conduit open() throws UncheckedIOException {
		return new RAMConduit();
	}

}
