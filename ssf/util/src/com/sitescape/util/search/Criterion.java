package com.sitescape.util.search;

import org.dom4j.Branch;
import org.dom4j.Element;

public interface Criterion
{
	Element toQuery(Branch parent);
}
