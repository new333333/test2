package com.sitescape.ef.dao.impl;

/**
 * Parameter names are used for the implementation of Hibernate
 * based DAO functionality and typically used to get or set
 * parameters on query object, etc. 
 * For example, "forumName" and zoneName in the following query string:
 * from Binder forum where forum.name=:forumName and forum.zone.id=:zoneName
 * 
 * @author Jong Kim
 *
 */
public class ParameterNames {
    public static final String BINDER_NAME = "binderName";
    public static final String USER_NAME = "userName";
    public static final String COMPANY_ID = "zoneName";
}
