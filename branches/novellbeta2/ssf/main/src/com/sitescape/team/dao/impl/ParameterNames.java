/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.dao.impl;

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
    public static final String USER_NAME = "userName";
    public static final String INTERNAL_ID = "internalId";
    public static final String ZONE_ID="zoneId";
}
