package org.kablink.teaming.remoting.rest.v1.resource.admin;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.Recipient;
import org.kablink.teaming.rest.v1.model.SharingPermission;
import org.kablink.teaming.rest.v1.model.admin.AssignedRight;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.Schedule;
import org.kablink.teaming.rest.v1.model.admin.SelectedDays;
import org.kablink.teaming.rest.v1.model.admin.Time;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.teaming.web.util.NetFolderRole;
import org.kablink.util.api.ApiErrorCode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * User: David
 * Date: 11/20/13
 * Time: 8:59 PM
 */
public class AbstractAdminResource extends AbstractResource {
    protected NetFolder _createNetFolder(NetFolder netFolder, ResourceDriverConfig resourceDriverConfig) throws WriteFilesException, WriteEntryDataException {
        validateMandatoryField(netFolder, "getName");
        validateMandatoryField(netFolder, "getRelativePath");
        Binder.SyncScheduleOption syncScheduleOption = netFolder.getInheritSyncSchedule() ?
                Binder.SyncScheduleOption.useNetFolderServerSchedule : Binder.SyncScheduleOption.useNetFolderSchedule;
        List<NetFolderRole> roles = toNetFolderRoles(netFolder.getAssignedRights());

        Binder parentBinder = getCoreDao().loadReservedBinder(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID,
                RequestContextHolder.getRequestContext().getZoneId() );

        Binder binder = NetFolderHelper.createNetFolder(getTemplateModule(), getBinderModule(), getFolderModule(), getAdminModule(), getLoggedInUser(),
                netFolder.getName(), resourceDriverConfig.getName(), netFolder.getRelativePath(), toScheduleInfo(netFolder.getSyncSchedule()),
                syncScheduleOption, parentBinder.getId(), false, netFolder.getIndexContent(), netFolder.getFullSyncDirOnly());

        if (roles!=null) {
            NetFolderHelper.setNetFolderRights(this, binder.getId(), roles);
        }

        return AdminResourceUtil.buildNetFolder((Folder) binder, this, true);
    }

    protected NetFolder _modifyNetFolder(NetFolder netFolder, ResourceDriverConfig resourceDriverConfig) throws WriteFilesException, WriteEntryDataException {
        validateMandatoryField(netFolder, "getName");
        validateMandatoryField(netFolder, "getRelativePath");
        Binder.SyncScheduleOption syncScheduleOption = netFolder.getInheritSyncSchedule() ?
                Binder.SyncScheduleOption.useNetFolderServerSchedule : Binder.SyncScheduleOption.useNetFolderSchedule;
        List<NetFolderRole> roles = toNetFolderRoles(netFolder.getAssignedRights());

        NetFolderHelper.modifyNetFolder(getBinderModule(), getFolderModule(), netFolder.getId(),
                netFolder.getName(), resourceDriverConfig.getName(), netFolder.getRelativePath(), toScheduleInfo(netFolder.getSyncSchedule()),
                syncScheduleOption, netFolder.getIndexContent(), netFolder.getFullSyncDirOnly());

        if (roles!=null) {
            NetFolderHelper.setNetFolderRights(this, netFolder.getId(), roles);
        }

        return AdminResourceUtil.buildNetFolder((Folder) getBinderModule().getBinder(netFolder.getId()), this, true);
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

    protected List<NetFolderRole> toNetFolderRoles(List<AssignedRight> rights) {
        List<NetFolderRole> roles = null;
        if (rights!=null) {
            roles = new ArrayList<NetFolderRole>();
            for (AssignedRight right : rights) {
                roles.add(toNetFolderRole(right));
            }
        }
        return roles;
    }

    protected NetFolderRole toNetFolderRole(AssignedRight right) {
        validateMandatoryField(right, "getPrincipal", "getId");
        validateMandatoryField(right, "getPrincipal", "getType");
        validateMandatoryField(right, "getAccess", "getRole");
        Recipient.RecipientType recipientType = toEnum(Recipient.RecipientType.class, "'principal'.'type'", right.getPrincipal().getType());
        Access.RoleType roleType = toEnum(Access.RoleType.class, "'access'.'role'", right.getAccess().getRole());
        Principal p = getProfileModule().getEntry(right.getPrincipal().getId());

        NetFolderRole role = new NetFolderRole(p);
        if (roleType != Access.RoleType.NONE ) {
            role.addRole(NetFolderRole.RoleType.AllowAccess);
        }
        SharingPermission perms = right.getAccess().getSharing();
        if (perms!=null) {
            if (Boolean.TRUE.equals(perms.getInternal())) {
                role.addRole(NetFolderRole.RoleType.ShareInternal);
            }
            if (Boolean.TRUE.equals(perms.getExternal())) {
                role.addRole(NetFolderRole.RoleType.ShareExternal);
            }
            if (Boolean.TRUE.equals(perms.getPublic())) {
                role.addRole(NetFolderRole.RoleType.SharePublic);
            }
            if (Boolean.TRUE.equals(perms.getGrantReshare())) {
                role.addRole(NetFolderRole.RoleType.ShareForward);
            }
        }
        return role;
    }

}
