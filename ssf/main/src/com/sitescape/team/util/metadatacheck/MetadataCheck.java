package com.sitescape.team.util.metadatacheck;

import java.util.Map;

public interface MetadataCheck {

	public String check(String input) throws MetadataCheckException;
}