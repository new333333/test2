package com.sitescape.ef.pipeline;

import com.sitescape.ef.UncheckedIOException;

public interface ConduitFactory {

	public Conduit open(String producerName) throws UncheckedIOException;
}
