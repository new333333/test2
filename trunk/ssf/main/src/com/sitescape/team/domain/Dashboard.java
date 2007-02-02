package com.sitescape.team.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * This object represents an abstract dashboard.
 * 
 * @hibernate.class table="SS_Dashboards" dynamic-update="true" dynamic-insert="false" lazy="false"
 * @hibernate.discriminator type="string" length="1" column="type"
 * @hibernate.cache usage="read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Jong Kim
 *
 */
public abstract class Dashboard extends PersistentTimestampObject implements Cloneable {
	public final static String Components = "components";

	//Component list map keys (Components)
	public final static String Id = "id";
	public final static String Scope = "scope";
	public final static String Visible = "visible";

	
    protected Map properties;
	private int nextComponentId=0;
	protected boolean showComponents=true;

	public Dashboard() {
	}

    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType"
     * @return
     */
    public Map getProperties() {
    	return properties;
    }
    public void setProperties(Map properties) {
        this.properties = properties;
    }
    public void setProperty(String name, Object value) {
    	if (properties == null) properties = new HashMap();
    	properties.put(name, value);
    }
    public Object getProperty(String name) {
    	if (properties == null) return null;
    	return properties.get(name);
    }

    /**
     * @hiberate.property
     */
    public int getNextComponentId() {
    	return nextComponentId;
    }
    public void setNextComponentId(int nextComponentId) {
    	this.nextComponentId = nextComponentId;
    }
	public boolean isShowComponents() {
		return showComponents;
	}
	public void setShowComponents(boolean showComponents) {
		this.showComponents = showComponents;
	}    
}
