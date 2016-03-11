package org.kablink.teaming.remoting.rest.v1.resource.admin;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.Recipient;
import org.kablink.teaming.rest.v1.model.SharingPermission;
import org.kablink.teaming.rest.v1.model.admin.AssignedRight;
import org.kablink.teaming.rest.v1.model.admin.AssignedSharingPermission;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.Schedule;
import org.kablink.teaming.rest.v1.model.admin.SelectedDays;
import org.kablink.teaming.rest.v1.model.admin.Time;
import org.kablink.teaming.web.util.AssignedRole;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.util.api.ApiErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Base class for Admin Resources
 */
public class AbstractAdminResource extends AbstractResource {
    protected NetFolder _createNetFolder(NetFolder netFolder, ResourceDriverConfig resourceDriverConfig) throws WriteFilesException, WriteEntryDataException {
        validateMandatoryField(netFolder, "getName");
        NetFolderConfig.SyncScheduleOption syncScheduleOption = netFolder.getInheritSyncSchedule() ?
        		NetFolderConfig.SyncScheduleOption.useNetFolderServerSchedule : NetFolderConfig.SyncScheduleOption.useNetFolderSchedule;
        List<AssignedRole> roles = toNetFolderRoles(netFolder.getAssignedRights());

        Binder parentBinder = getCoreDao().loadReservedBinder(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID,
                RequestContextHolder.getRequestContext().getZoneId() );

        NetFolderConfig nfc = NetFolderHelper.createNetFolder(getTemplateModule(), getBinderModule(), getFolderModule(), getNetFolderModule(), getAdminModule(), getLoggedInUser(),
                netFolder.getName(), resourceDriverConfig.getName(), netFolder.getRelativePath(), toScheduleInfo(netFolder.getSyncSchedule()),
                syncScheduleOption, parentBinder.getId(), false, netFolder.getIndexContent(), netFolder.getInheritIndexContent(), netFolder.getFullSyncDirOnly(),
                netFolder.getAllowClientInitiatedSync(),
                netFolder.getInheritClientSyncSettings() );

        Binder binder = getBinderModule().getBinder(nfc.getTopFolderId());

        if (roles!=null) {
            NetFolderHelper.setNetFolderRights(this, binder.getId(), roles);
        }

        return AdminResourceUtil.buildNetFolder(nfc, this, true);
    }

    protected NetFolder _modifyNetFolder(NetFolder netFolder, ResourceDriverConfig resourceDriverConfig) throws WriteFilesException, WriteEntryDataException {
        validateMandatoryField(netFolder, "getName");
        NetFolderConfig.SyncScheduleOption syncScheduleOption = netFolder.getInheritSyncSchedule() ?
        		NetFolderConfig.SyncScheduleOption.useNetFolderServerSchedule : NetFolderConfig.SyncScheduleOption.useNetFolderSchedule;
        List<AssignedRole> roles = toNetFolderRoles(netFolder.getAssignedRights());

        NetFolderHelper.modifyNetFolder(getBinderModule(), getFolderModule(), getNetFolderModule(), netFolder.getId(),
                netFolder.getName(), resourceDriverConfig.getName(), netFolder.getRelativePath(), toScheduleInfo(netFolder.getSyncSchedule()),
                syncScheduleOption, netFolder.getIndexContent(), netFolder.getInheritIndexContent(), netFolder.getFullSyncDirOnly(),
                netFolder.getAllowClientInitiatedSync(),
                netFolder.getInheritClientSyncSettings() );

        if (roles!=null) {
            NetFolderHelper.setNetFolderRights(this, netFolder.getId(), roles);
        }

        Binder binder = getBinderModule().getBinder(netFolder.getId());
        return AdminResourceUtil.buildNetFolder(binder.getNetFolderConfig(), this, true);
    }

    protected ScheduleInfo toScheduleInfo(Schedule model) {
        if (model==null) {
            return null;
        }
        validateMandatoryField(model, "getDayFrequency");
        Schedule.DayFrequency dayFrequency = toEnum(Schedule.DayFrequency.class, "when", model.getDayFrequency());
        if (dayFrequency == Schedule.DayFrequency.selected_days) {
            validateMandatoryField(model, "getSelectedDays");
        } else {
            validateDisallowedField(model, "Only allowed if 'when' is 'selected_days'", "getSelectedDays");
        }
        boolean atDefined = isDefined(model, "getAt");
        boolean everyDefined = isDefined(model, "getEvery");
        if (atDefined && everyDefined) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Cannot specify both 'at' and 'every'.");
        } else if (!atDefined && !everyDefined) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Must specify either 'at' or 'every'.");
        }
        if (atDefined) {
            validateMandatoryField(model, "getAt", "getHour");
            validateMandatoryField(model, "getAt", "getMinute");
            Integer hour = model.getAt().getHour();
            if (hour<0 || hour>=24) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'at'.'hour' must be between 0 and 23 inclusive.");
            }
            Integer minute = model.getAt().getMinute();
            if (minute<0 || minute>=60) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'at'.'minute' must be between 0 and 59 inclusive.");
            }
        } else {
            Integer hour = model.getEvery().getHour();
            Integer minute = model.getEvery().getMinute();
            if ((hour!=null && hour!=0) && (minute!=null && minute!=0)) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Cannot specify both 'every'.'hour' and 'every'.'minute'.");
            }
            if ((hour==null || hour==0) && (minute==null || minute==0)) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Must specify either 'every'.'hour' and 'every'.'minute'.");
            }
            if (hour!=null && hour!=0) {
                if (hour==1 || hour==2 || hour==3 || hour==4 || hour==6 || hour==8 || hour==12) {
                    //pass
                } else {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Valid values for 'every'.'hour' are: 1, 2, 3, 4, 6, 8 or 12");
                }
            } else {
                if (minute==15 || minute==30 || minute==45) {
                    //pass
                } else {
                    throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Valid values for 'every'.'minute' are: 15, 30 or 45");
                }
            }
        }

        // Patterned this after GwtServerHelper.getScheduleInfoFromGwtSchedule()
        ScheduleInfo schInfo = new ScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
        schInfo.setEnabled(model.getEnabled());
        org.kablink.teaming.jobs.Schedule schedule = new org.kablink.teaming.jobs.Schedule("");
        Random randomMinutes = new Random();
        if (dayFrequency == Schedule.DayFrequency.daily) {
            schedule.setDaily(true);
        } else {
            schedule.setDaily(false);
            SelectedDays days = model.getSelectedDays();
            schedule.setOnMonday( days.getMon() );
            schedule.setOnTuesday( days.getTue() );
            schedule.setOnWednesday( days.getWed() );
            schedule.setOnThursday( days.getThu() );
            schedule.setOnFriday( days.getFri() );
            schedule.setOnSaturday( days.getSat() );
            schedule.setOnSunday(days.getSun());
        }

        if (atDefined) {
            Time at = model.getAt();
            schedule.setHours( at.getHour().toString() );
            schedule.setMinutes( at.getMinute().toString() );
        } else {
            Time every = model.getEvery();
            Integer hour = every.getHour();
            Integer minute = every.getMinute();
            if (hour!=null && hour!=0) {
                schedule.setMinutes( Integer.toString( randomMinutes.nextInt( 60 ) ) );
                schedule.setHours( "0/" + hour );
            } else {
                schedule.setHours( "*" );
                if ( minute == 15 || minute == 30 )
                {
                    schedule.setMinutes( randomMinutes.nextInt( minute ) + "/" + minute );
                }
                else if ( minute == 45 )
                {
                    schedule.setMinutes( "0/45" );
                }
            }
        }
        schInfo.setSchedule(schedule);
        return schInfo;
    }

    protected List<AssignedRole> toNetFolderRoles(List<AssignedRight> rights) {
        List<AssignedRole> roles = null;
        if (rights!=null) {
            roles = new ArrayList<AssignedRole>();
            for (AssignedRight right : rights) {
                roles.add(toNetFolderRole(right));
            }
        }
        return roles;
    }

    protected AssignedRole toNetFolderRole(AssignedRight right) {
        validateMandatoryField(right, "getPrincipal", "getId");
        validateMandatoryField(right, "getPrincipal", "getType");
        validateMandatoryField(right, "getAccess", "getRole");
        Recipient.RecipientType recipientType = toEnum(Recipient.RecipientType.class, "'principal'.'type'", right.getPrincipal().getType());
        Access.RoleType roleType = toEnum(Access.RoleType.class, "'access'.'role'", right.getAccess().getRole());
        Principal p = getProfileModule().getEntry(right.getPrincipal().getId());

        AssignedRole role = new AssignedRole(p);
        if (roleType != Access.RoleType.NONE ) {
            role.addRole(AssignedRole.RoleType.AllowAccess);
        }
        SharingPermission perms = right.getAccess().getSharing();
        if (perms!=null) {
            if (Boolean.TRUE.equals(perms.getInternal())) {
                role.addRole(AssignedRole.RoleType.ShareInternal);
            }
            if (Boolean.TRUE.equals(perms.getExternal())) {
                role.addRole(AssignedRole.RoleType.ShareExternal);
            }
            if (Boolean.TRUE.equals(perms.getPublic())) {
                role.addRole(AssignedRole.RoleType.SharePublic);
            }
            if (Boolean.TRUE.equals(perms.getPublicLink())) {
                role.addRole(AssignedRole.RoleType.SharePublicLinks);
            }
            if (Boolean.TRUE.equals(perms.getGrantReshare())) {
                role.addRole(AssignedRole.RoleType.ShareForward);
            }
        }
        return role;
    }

    protected List<AssignedRole> toAssignedRoles(List<AssignedSharingPermission> perms) {
        List<AssignedRole> roles = null;
        if (perms!=null) {
            roles = new ArrayList<AssignedRole>();
            for (AssignedSharingPermission perm : perms) {
                roles.add(toAssignedRole(perm));
            }
        }
        return roles;
    }

    protected AssignedRole toAssignedRole(AssignedSharingPermission permission) {
        validateMandatoryField(permission, "getPrincipal", "getId");
        validateMandatoryField(permission, "getPrincipal", "getType");
        Recipient.RecipientType recipientType = toEnum(Recipient.RecipientType.class, "'principal'.'type'", permission.getPrincipal().getType());
        Principal p = getProfileModule().getEntry(permission.getPrincipal().getId());

        AssignedRole role = new AssignedRole(p);
        SharingPermission perms = permission.getSharing();
        if (perms!=null) {
            if (Boolean.TRUE.equals(perms.getInternal())) {
                role.addRole(AssignedRole.RoleType.EnableShareInternal);
            }
            if (Boolean.TRUE.equals(perms.getExternal())) {
                role.addRole(AssignedRole.RoleType.EnableShareExternal);
            }
            if (Boolean.TRUE.equals(perms.getAllInternal())) {
                role.addRole(AssignedRole.RoleType.EnableShareWithAllInternal);
            }
            if (Boolean.TRUE.equals(perms.getAllExternal())) {
                role.addRole(AssignedRole.RoleType.EnableShareWithAllExternal);
            }
            if (Boolean.TRUE.equals(perms.getPublic())) {
                role.addRole(AssignedRole.RoleType.EnableSharePublic);
            }
            if (Boolean.TRUE.equals(perms.getPublicLink())) {
                role.addRole(AssignedRole.RoleType.EnableLinkSharing);
            }
            if (Boolean.TRUE.equals(perms.getGrantReshare())) {
                role.addRole(AssignedRole.RoleType.EnableShareForward);
            }
        }
        return role;
    }

}
