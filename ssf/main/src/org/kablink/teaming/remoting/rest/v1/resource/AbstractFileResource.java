package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.api.core.InjectParam;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFileByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoPrincipalByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.util.HttpHeaders;
import org.kablink.util.Validator;
import org.kablink.util.api.ApiErrorCode;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: david
 * Date: 5/22/12
 * Time: 10:39 AM
 */
abstract public class AbstractFileResource extends AbstractResource {
    protected Date dateFromISO8601(String modDateISO8601) {
        if (Validator.isNotNull(modDateISO8601)) {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(modDateISO8601);
            return dateTime.toDate();
        } else {
            return null;
        }
    }

    protected FileProperties writeFileContentByName(
            String entityType,
            long entityId,
            String filename,
            String dataName,
            String modDateISO8601,
            Integer lastVersionNumber,
            Integer lastMajorVersionNumber,
            Integer lastMinorVersionNumber,
            HttpServletRequest request,
            InputStream is)
            throws WriteFilesException, WriteEntryDataException {
        EntityIdentifier.EntityType et = entityTypeFromString(entityType);
        FileAttachment fa;
        boolean result = true;
        Date modDate = dateFromISO8601(modDateISO8601);
        if (et == EntityIdentifier.EntityType.folderEntry) {
            FolderEntry entry = getFolderModule().getEntry(null, entityId);
            fa = entry.getFileAttachment(filename);
            if (fa != null) {
                result = FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyFolderEntryWithFile(entry, dataName, filename, is, modDate);
                }
            } else {
                result = validateArgumentsForNewFile(lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyFolderEntryWithFile(entry, dataName, filename, is, modDate);
                    fa = entry.getFileAttachment(filename);
                }
            }
        } else if (et == EntityIdentifier.EntityType.user) {
            Principal user = getProfileModule().getEntry(entityId);
            if (!(user instanceof User))
                throw new BadRequestException(ApiErrorCode.NOT_USER, "Entity ID '" + entityId + "' does not represent a user");
            fa = user.getFileAttachment(filename);
            if (fa != null) {
                result = FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyPrincipalWithFile(user, dataName, filename, is, modDate);
                }
            } else {
                result = validateArgumentsForNewFile(lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyPrincipalWithFile(user, dataName, filename, is, modDate);
                    fa = user.getFileAttachment(filename);
                }
            }
        } else if (et == EntityIdentifier.EntityType.group) {
            Principal group = getProfileModule().getEntry(entityId);
            if (!(group instanceof Group))
                throw new BadRequestException(ApiErrorCode.NOT_GROUP, "Entity ID '" + entityId + "' does not represent a group");
            fa = group.getFileAttachment(filename);
            if (fa != null) {
                result = FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyPrincipalWithFile(group, dataName, filename, is, modDate);
                }
            } else {
                result = validateArgumentsForNewFile(lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyPrincipalWithFile(group, dataName, filename, is, modDate);
                    fa = group.getFileAttachment(filename);
                }
            }
        } else if (et == EntityIdentifier.EntityType.workspace || et == EntityIdentifier.EntityType.folder || et == EntityIdentifier.EntityType.profiles) {
            Binder binder = getBinderModule().getBinder(entityId);
            fa = binder.getFileAttachment(filename);
            if (fa != null) {
                result = FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    // Ignore modDate param, since it isn't applicable in this case.
                    FileUtils.modifyBinderWithFile(binder, dataName, filename, is);
                }
            } else {
                result = validateArgumentsForNewFile(lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
                if (result) {
                    FileUtils.modifyBinderWithFile(binder, dataName, filename, is);
                    fa = binder.getFileAttachment(filename);
                }
            }
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityType + "' is unknown or not supported by this method");
        }
        if (result)
            return ResourceUtil.buildFileProperties(fa);
        else
            throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file");
    }

    protected boolean validateArgumentsForNewFile(Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
        // These arguments should not be specified when creating a new file.
        if (lastVersionNumber == null && lastMajorVersionNumber == null && lastMinorVersionNumber == null)
            return true;
        else
            return false;
    }

    protected EntityIdentifier.EntityType entityTypeFromString(String entityTypeStr)
            throws BadRequestException {
        try {
            return EntityIdentifier.EntityType.valueOf(entityTypeStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityTypeStr + "' is invalid");
        }
    }

    protected FileAttachment findFileAttachment(String fileId)
            throws NoFileByTheIdException {
        FileAttachment fa = getFileModule().getFileAttachmentById(fileId);
        if (fa == null)
            throw new NoFileByTheIdException(fileId);
        else if (fa instanceof VersionAttachment)
            throw new NoFileByTheIdException(fileId, "The specified file ID represents a file version rather than a file");
        else
            return fa;
    }

    protected InputStream getInputStreamFromMultipartFormdata(HttpServletRequest request)
            throws WebApplicationException, UncheckedIOException {
        InputStream is;
        try {
            ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
            FileItemIterator fii = sfu.getItemIterator(request);
            if (fii.hasNext()) {
                FileItemStream item = fii.next();
                is = item.openStream();
            } else {
                throw new BadRequestException(ApiErrorCode.MISSING_MULTIPART_FORM_DATA, "Missing form data");
            }
        } catch (FileUploadException e) {
            logger.warn("Received bad multipart form data", e);
            throw new BadRequestException(ApiErrorCode.BAD_MULTIPART_FORM_DATA, "Received bad multipart form data");
        } catch (IOException e) {
            logger.error("Error reading multipart form data", e);
            throw new UncheckedIOException(e);
        }
        return is;
    }

    protected InputStream getRawInputStream(HttpServletRequest request)
            throws UncheckedIOException {
        try {
            return request.getInputStream();
        } catch (IOException e) {
            logger.error("Error reading data", e);
            throw new UncheckedIOException(e);
        }
    }

    protected FileAttachment getFileAttachment(DefinableEntity entity, String filename)
            throws NotFoundException {
        FileAttachment fa = entity.getFileAttachment(filename);
        if (fa != null)
            return fa;
        else
            throw new NotFoundException(ApiErrorCode.FILE_NOT_FOUND, "File '" + filename + "' is not found for entity '" + entity.getId() + "' of type '" + entity.getEntityType().name() + "'");
    }

    protected Response readFileContent(String entityType, long entityId, String filename, Date ifModifiedSinceDate)
            throws BadRequestException {
        EntityIdentifier.EntityType et = entityTypeFromString(entityType);
        Binder binder;
        DefinableEntity entity;
        FileAttachment fa;
        if (et == EntityIdentifier.EntityType.folderEntry) {
            entity = getFolderModule().getEntry(null, entityId);
            binder = entity.getParentBinder();
        } else if (et == EntityIdentifier.EntityType.user) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof User))
                throw new BadRequestException(ApiErrorCode.NOT_USER, "Entity ID '" + entityId + "' does not represent a user");
            binder = entity.getParentBinder();
        } else if (et == EntityIdentifier.EntityType.group) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof Group))
                throw new BadRequestException(ApiErrorCode.NOT_GROUP, "Entity ID '" + entityId + "' does not represent a group");
            binder = entity.getParentBinder();
        } else if (et == EntityIdentifier.EntityType.workspace || et == EntityIdentifier.EntityType.folder || et == EntityIdentifier.EntityType.profiles) {
            entity = getBinderModule().getBinder(entityId);
            binder = (Binder) entity;
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityType + "' is unknown or not supported by this method");
        }
        fa = getFileAttachment(entity, filename);
        Date lastModDate = fa.getModification().getDate();
        if (ifModifiedSinceDate != null) {
            // If-Modified-Since header was specified (i.e., conditional GET)
            if (!ifModifiedSinceDate.before(lastModDate)) {
                // The file has not been modified since the specified date.
                return Response.status(Response.Status.NOT_MODIFIED).build();
            }
        }
        String mt = new MimetypesFileTypeMap().getContentType(filename);
        return Response.ok(getFileModule().readFile(binder, entity, fa), mt).lastModified(lastModDate).build();
    }

    protected void deleteFile(String entityType, long entityId, String filename)
            throws WriteFilesException, WriteEntryDataException, BadRequestException {
        EntityIdentifier.EntityType et = entityTypeFromString(entityType);
        FileAttachment fa;
        if (et == EntityIdentifier.EntityType.folderEntry) {
            try {
                FolderEntry entry = getFolderModule().getEntry(null, entityId);
                fa = entry.getFileAttachment(filename);
                if (fa != null)
                    FolderUtils.deleteFileInFolderEntry(entry, fa);
            } catch (NoFolderEntryByTheIdException e) {
                // The specified entry no longer exists. This is OK for delete request.
            }
        } else if (et == EntityIdentifier.EntityType.user) {
            try {
                Principal user = getProfileModule().getEntry(entityId);
                if (!(user instanceof User))
                    throw new BadRequestException(ApiErrorCode.NOT_USER, "Entity ID '" + entityId + "' does not represent a user");
                fa = user.getFileAttachment(filename);
                if (fa != null) {
                    List deletes = new ArrayList();
                    deletes.add(fa.getId());
                    getProfileModule().modifyEntry(entityId, new EmptyInputData(), null, deletes, null, null);
                }
            } catch (NoPrincipalByTheIdException e) {
                // The specified user no longer exists. This is OK for delete request.
            }
        } else if (et == EntityIdentifier.EntityType.group) {
            try {
                Principal group = getProfileModule().getEntry(entityId);
                if (!(group instanceof User))
                    throw new BadRequestException(ApiErrorCode.NOT_GROUP, "Entity ID '" + entityId + "' does not represent a group");
                fa = group.getFileAttachment(filename);
                if (fa != null) {
                    List deletes = new ArrayList();
                    deletes.add(fa.getId());
                    getProfileModule().modifyEntry(entityId, new EmptyInputData(), null, deletes, null, null);
                }
            } catch (NoPrincipalByTheIdException e) {
                // The specified user no longer exists. This is OK for delete request.
            }
        } else if (et == EntityIdentifier.EntityType.workspace || et == EntityIdentifier.EntityType.folder || et == EntityIdentifier.EntityType.profiles) {
            try {
                Binder binder = getBinderModule().getBinder(entityId);
                fa = binder.getFileAttachment(filename);
                if (fa != null) {
                    List deletes = new ArrayList();
                    deletes.add(fa.getId());
                    getBinderModule().modifyBinder(entityId, new EmptyInputData(), null, deletes, null);
                }
            } catch (NoBinderByTheIdException e) {
                // The specified binder no longer exists. This is OK for delete request.
            }
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityType + "' is unknown or not supported by this method");
        }
    }

    protected Date getIfModifiedSinceDate(HttpServletRequest request) {
        Date date = null;
        long longDate = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (longDate != -1)
            date = new Date(longDate);
        return date;
    }

    protected DefinableEntity findDefinableEntity(String entityType, long entityId)
            throws BadRequestException, NotFoundException {
        EntityIdentifier.EntityType et = entityTypeFromString(entityType);
        DefinableEntity entity;
        if (et == EntityIdentifier.EntityType.folderEntry) {
            entity = getFolderModule().getEntry(null, entityId);
        } else if (et == EntityIdentifier.EntityType.user) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof User))
                throw new BadRequestException(ApiErrorCode.NOT_USER, "Entity ID '" + entityId + "' does not represent a user");
        } else if (et == EntityIdentifier.EntityType.group) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof Group))
                throw new BadRequestException(ApiErrorCode.NOT_GROUP, "Entity ID '" + entityId + "' does not represent a group");
        } else if (et == EntityIdentifier.EntityType.workspace || et == EntityIdentifier.EntityType.folder || et == EntityIdentifier.EntityType.profiles) {
            entity = getBinderModule().getBinder(entityId);
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityType + "' is unknown or not supported by this method");
        }
        return entity;
    }

    protected List<FileVersionProperties> fileVersionsFromFileAttachment(FileAttachment fa) {
        Set<VersionAttachment> vas = fa.getFileVersions();
        List<FileVersionProperties> list = new ArrayList<FileVersionProperties>(vas.size());
        for (VersionAttachment va : vas) {
            list.add(ResourceUtil.fileVersionFromFileAttachment(va));
        }
        return list;
    }
}
