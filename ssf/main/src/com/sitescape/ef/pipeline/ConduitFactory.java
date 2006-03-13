package com.sitescape.ef.pipeline;

import com.sitescape.ef.UncheckedIOException;

public interface ConduitFactory {

	public Conduit open() throws UncheckedIOException;
}
