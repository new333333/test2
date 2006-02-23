
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public interface UpdateAttributeSupport {
	/**
	 * Update the object.  If no changes are made return false;
	 * @param newVal
	 * @return
	 */
	public boolean update(Object newVal);

}
