package com.sitescape.team.dao.util;

import java.util.List;

public interface Criterion {
	public List<Object> getParameterValues();
	public String toSQLString(String alias);
}
