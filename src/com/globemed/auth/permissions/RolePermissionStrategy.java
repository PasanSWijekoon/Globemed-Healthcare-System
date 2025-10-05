package com.globemed.auth.permissions;


public interface RolePermissionStrategy {
    boolean hasPermission(Permission permission);
}
