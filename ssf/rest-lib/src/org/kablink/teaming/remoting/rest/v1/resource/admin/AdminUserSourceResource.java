/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 *
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 *
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 *
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 *
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.remoting.rest.v1.resource.admin;

import com.sun.jersey.spi.resource.Singleton;
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import com.webcohesion.enunciate.metadata.rs.ResourceLabel;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import org.dom4j.Element;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapSyncException;
import org.kablink.teaming.domain.NoLdapConnectionConfigByTheIdException;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.admin.GroupSynchronization;
import org.kablink.teaming.rest.v1.model.admin.KeyValuePair;
import org.kablink.teaming.rest.v1.model.admin.LdapSearchInfo;
import org.kablink.teaming.rest.v1.model.admin.LdapUserSource;
import org.kablink.teaming.rest.v1.model.admin.UserSourceSynchronization;
import org.kablink.teaming.rest.v1.model.admin.UserSynchronization;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Resources for managing LDAP User Sources.
 */
@Path("/admin/user_sources")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("LDAP User Sources")
@ResourceLabel("LDAP User Source Resource")
public class AdminUserSourceResource extends AbstractAdminResource {

    /**
     * Retrieves all of the configured LDAP User Sources.
     * @return A SearchResultList of LdapUserSource objects.
     */
    @GET
   	public SearchResultList<LdapUserSource> getUserSources() {
        List<LdapConnectionConfig> configList = getAuthenticationModule().getLdapConnectionConfigs();
        SearchResultList<LdapUserSource> results = new SearchResultList<LdapUserSource>();
        for (LdapConnectionConfig config : configList) {
            results.append(AdminResourceUtil.buildUserSource(config, getResourceDriverModule()));
        }
        return results;
   	}

    /**
     * Creates a new LDAP User Source.
     * <p>
     * The following LDAP User Source fields are mandatory:
     * <ul>
     *     <li>url</li>
     *     <li>username_attribute</li>
     *     <li>guid_attribute</li>
     *     <li>username</li>
     *     <li>password</li>
     *     <li>user_contexts</li>
     * </ul>
     * </p>
     * @param userSource
     * @return  The new Ldap User Source object.
     */
    @POST
   	@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public LdapUserSource createUserSource(LdapUserSource userSource) {
        LdapConnectionConfig config = toLdapConnectionConfig(userSource);
        getAuthenticationModule().saveLdapConnectionConfig(config);
        return AdminResourceUtil.buildUserSource(config, getResourceDriverModule());
   	}

    /**
     * Triggers a sync of all LDAP User Sources.
     *
     * <p>The request blocks until the sync has completed.</p>
     * @return The LDAP sync results.
     */
    @POST
    @Path("sync")
    @Consumes({"*/*"})
   	public org.kablink.teaming.rest.v1.model.admin.LdapSyncResults syncSources() throws LdapSyncException {
        LdapSyncResults results = new LdapSyncResults(String.valueOf( Math.random() ));
        getLdapModule().syncAll(true, null, LdapModule.LdapSyncMode.PERFORM_SYNC, results);
        return AdminResourceUtil.buildLdapSyncResults(results);
   	}

    /**
     * Retrieves the current User Source Synchronization settings.
     * @return The UserSourceSynchronization object.
     */
    @GET
    @Path("sync_config")
    public UserSourceSynchronization getUserSourceSynchronization() {
        UserSourceSynchronization sync = new UserSourceSynchronization();
        LdapSchedule ldapSchedule = getLdapModule().getLdapSchedule();
        sync.setSchedule(AdminResourceUtil.buildSchedule(ldapSchedule.getScheduleInfo()));
        UserSynchronization users = new UserSynchronization();
        users.setRegister(ldapSchedule.isUserRegister());
        users.setSyncProfiles(ldapSchedule.isUserSync());
        if (ldapSchedule.isUserDelete()) {
            users.setRemovedAccountAction(UserSynchronization.RemovedAccountAction.delete.name());
            users.setDeleteWorkspace(ldapSchedule.isUserWorkspaceDelete());
        } else {
            users.setRemovedAccountAction(UserSynchronization.RemovedAccountAction.disable.name());
        }
        users.setDefaultTimezone(getLdapModule().getDefaultTimeZone());
        users.setDefaultLocale(getLdapModule().getDefaultLocaleId());
        sync.setUsers(users);

        GroupSynchronization groups = new GroupSynchronization();
        groups.setRegister(ldapSchedule.isGroupRegister());
        groups.setSyncProfiles(ldapSchedule.isGroupSync());
        groups.setSyncMembership(ldapSchedule.isMembershipSync());
        groups.setDeleteRemovedGroups(ldapSchedule.isGroupDelete());
        sync.setGroups(groups);

        return sync;
    }

    /**
     * Updates the User Source Synchronization settings.  Only the fields that are included in the request body are updated.
     * @param sync
     * @return  The updated UserSourceSynchronization settings.
     */
    @PUT
    @Path("sync_config")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public UserSourceSynchronization updateUserSourceSynchronization(UserSourceSynchronization sync) {
        LdapModule ldapModule = getLdapModule();
        LdapSchedule existing = ldapModule.getLdapSchedule();
        if (sync.getSchedule()!=null) {
            ScheduleInfo scheduleInfo = toScheduleInfo(sync.getSchedule());
            existing.setScheduleInfo(scheduleInfo);
        }
        UserSynchronization users = sync.getUsers();
        if (users !=null) {
            if (users.getSyncProfiles()!=null) {
                existing.setUserSync(users.getSyncProfiles());
            }
            if (users.getRegister()!=null) {
                existing.setUserRegister(users.getRegister());
            }
            if (users.getRemovedAccountAction()!=null) {
                UserSynchronization.RemovedAccountAction action = toEnum(UserSynchronization.RemovedAccountAction.class,
                        "removed_account_action", users.getRemovedAccountAction());
                if (action==UserSynchronization.RemovedAccountAction.delete) {
                    existing.setUserDelete(true);
                    if (users.getDeleteWorkspace()!=null) {
                        existing.setUserWorkspaceDelete(users.getDeleteWorkspace());
                    }
                } else {
                    existing.setUserDelete(false);
                }
            }
            if (users.getDefaultTimezone()!=null) {
                TimeZone tz = TimeZone.getTimeZone(users.getDefaultTimezone());
                if (tz==null || !tz.getID().equals(users.getDefaultTimezone())) {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Invalid time zone: " + users.getDefaultTimezone());
                }
                ldapModule.setDefaultTimeZone(users.getDefaultTimezone());
            }
            if (users.getDefaultLocale()!=null) {
                Locale locale = MiscUtil.findLocale(users.getDefaultLocale());
                if (locale==null) {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Invalid locale: " + users.getDefaultLocale());
                }
                ldapModule.setDefaultLocale(users.getDefaultLocale());
            }
        }
        GroupSynchronization groups = sync.getGroups();
        if (groups!=null) {
            if (groups.getRegister()!=null) {
                existing.setGroupRegister(groups.getRegister());
            }
            if (groups.getSyncProfiles()!=null) {
                existing.setGroupSync(groups.getSyncProfiles());
            }
            if (groups.getSyncMembership()!=null) {
                existing.setMembershipSync(groups.getSyncMembership());
            }
            if (groups.getDeleteRemovedGroups()!=null) {
                existing.setGroupDelete(groups.getDeleteRemovedGroups());
            }
        }
        ldapModule.setLdapSchedule(existing);

        return getUserSourceSynchronization();
    }

    /**
     * Retrieves an LDAP User Source.
     * @param id
     * @return
     */
    @GET
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=404, condition="(LDAP_CONFIG_NOT_FOUND) No LDAP User Source exists with the specified ID.")
    })
    public LdapUserSource getUserSource(@PathParam("id") String id) {
        LdapConnectionConfig config = getAuthenticationModule().getLdapConnectionConfig(id);
        return AdminResourceUtil.buildUserSource(config, getResourceDriverModule());
    }

    /**
     * Deletes an LDAP User Source.
     * @param id
     */
    @DELETE
    @Path("{id}")
    @StatusCodes({
            @ResponseCode(code=204, condition="The LDAP User Source is deleted successfully"),
            @ResponseCode(code=404, condition="(LDAP_CONFIG_NOT_FOUND) No LDAP User Source exists with the specified ID.")
    })
    public void deleteUserSource(@PathParam("id") String id) {
        List<LdapConnectionConfig> configs = getAuthenticationModule().getLdapConnectionConfigs();
        boolean found = false;
        for (int i=0; i<configs.size(); i++) {
            if (configs.get(i).getId().equals(id)) {
                configs.remove(i);
                found = true;
                break;
            }
        }
        if (found) {
            getAuthenticationModule().setLdapConnectionConfigs(configs);
        } else {
            throw new NoLdapConnectionConfigByTheIdException(id);
        }
    }

    private LdapConnectionConfig toLdapConnectionConfig(LdapUserSource source) {
        if (source.getUrl()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'url' field.");
        }
        if (source.getUsernameAttribute()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'username_attribute' field.");
        }
        if (source.getGuidAttribute()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'guid_attribute' field.");
        }
        if (source.getPrincipal()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'proxy_dn' field.");
        }
        if (source.getCredentials()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'proxy_password' field.");
        }
        if (source.getUserSearches()==null || source.getUserSearches().size()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing or empty 'user_contexts' field.");
        }
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setUrl(source.getUrl());
        config.setUserIdAttribute(source.getUsernameAttribute());
        config.setLdapGuidAttribute(source.getGuidAttribute());
        config.setPrincipal(source.getPrincipal());
        config.setCredentials(source.getCredentials());

        // Handle attribute map.
        Map<String, String> attrMap = new HashMap<String, String>();
        if (source.getMappings()==null) {
            List defaultMappings  = SZoneConfig.getElements("ldapConfiguration/userMapping/mapping");
            if ( defaultMappings != null )
            {
                for( Object nextObj : defaultMappings )
                {
                    Element next = (Element) nextObj;
                    attrMap.put(next.attributeValue("from"), next.attributeValue("to"));
                }
            }
        } else {
            for (KeyValuePair pair : source.getMappings()) {
                attrMap.put(pair.getKey(), pair.getValue());
            }
        }
        config.setMappings(attrMap);

        List<LdapConnectionConfig.SearchInfo> userSearches = new ArrayList<LdapConnectionConfig.SearchInfo>();
        List<LdapSearchInfo> userSearchesModel = source.getUserSearches();
        for (LdapSearchInfo info : userSearchesModel) {
            if (info.getBaseDn()==null) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'user_contexts' 'base_dn' field.");
            }
            String filter = info.getFilter();
            if (filter==null) {
                filter = SZoneConfig.getString( "ldapConfiguration/userFilter" );
            }
            LdapConnectionConfig.SearchInfo searchInfo = new LdapConnectionConfig.SearchInfo(info.getBaseDn(), filter, info.getSearchSubtree());
            LdapConnectionConfig.HomeDirConfig homeDirConfig = new LdapConnectionConfig.HomeDirConfig();
            homeDirConfig.setCreationOption(LdapConnectionConfig.HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE);
            searchInfo.setHomeDirConfig(homeDirConfig);
            userSearches.add(searchInfo);
        }
        config.setUserSearches(userSearches);

        List<LdapConnectionConfig.SearchInfo> groupSearches = new ArrayList<LdapConnectionConfig.SearchInfo>();
        List<LdapSearchInfo> groupSearchesModel = source.getGroupSearches();
        if (groupSearchesModel!=null) {
            for (LdapSearchInfo info : groupSearchesModel) {
                if (info.getBaseDn()==null) {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'user_contexts' 'base_dn' field.");
                }
                String filter = info.getFilter();
                if (filter==null) {
                    filter = SZoneConfig.getString( "ldapConfiguration/groupFilter" );
                }
                LdapConnectionConfig.SearchInfo searchInfo = new LdapConnectionConfig.SearchInfo(info.getBaseDn(), filter, info.getSearchSubtree());
                groupSearches.add(searchInfo);
            }
        }
        config.setGroupSearches(groupSearches);

        return config;
    }
}
