/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.dao;

/**
 * @author Janet McCann
 *
 */
public interface IXmlPersistence {
    public boolean isDirty();
    public String encodeAsXmlString();
}
