package com.sitescape.team.pipeline;

import com.sitescape.team.UncheckedIOException;

public interface ConduitFactory {

	public Conduit open() throws UncheckedIOException;
}
