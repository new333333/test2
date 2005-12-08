
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public class NoRoleByTheIdException extends NoObjectByTheIdException {
   private static final String NoRoleByTheIdException_ErrorCode = "errorcode.no.role.by.the.id";
  
    public NoRoleByTheIdException(String roleId) {
       super(NoRoleByTheIdException_ErrorCode, roleId);
   }
   public NoRoleByTheIdException(String roleId, String message) {
       super(NoRoleByTheIdException_ErrorCode, roleId, message);
   }
   public NoRoleByTheIdException(String roleId, String message, Throwable cause) {
       super(NoRoleByTheIdException_ErrorCode,roleId, message, cause);
   }
   public NoRoleByTheIdException(String roleId, Throwable cause) {
       super(NoRoleByTheIdException_ErrorCode, roleId, cause);
   }
}
