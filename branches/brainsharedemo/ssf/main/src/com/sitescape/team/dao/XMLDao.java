
package com.sitescape.team.dao;
import org.dom4j.Document;
import org.dom4j.Element;
/**
 * @author Janet McCann
 *
 */
public interface XMLDao {
	public Element load(Class className, Long id);

}
