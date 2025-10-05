package com.globemed.auth.permissions;

import com.globemed.model.Role;
import java.util.EnumSet;

public class PermissionFactory {

    public static RolePermissionStrategy getStrategy(Role role) {
        switch (role) {
            case Admin:
                return permission -> true;
            case Doctor:
                return new DoctorPermissionStrategy();
            case Nurse:
                return new NursePermissionStrategy();
            case Pharmacist:
                return new PharmacistPermissionStrategy();
            case Coordinator:
                return new CoordinatorPermissionStrategy();
            default:
                return permission -> false;
        }
    }

    private static class DoctorPermissionStrategy implements RolePermissionStrategy {
        private final EnumSet<Permission> permissions = EnumSet.of(
                Permission.VIEW_DASHBOARD, 
                Permission.MANAGE_PATIENTS,
                Permission.MANAGE_APPOINTMENTS, 
                Permission.LOGOUT);

        @Override
        public boolean hasPermission(Permission permission) {
            return permissions.contains(permission);
        }
    }

    private static class NursePermissionStrategy implements RolePermissionStrategy {
        private final EnumSet<Permission> permissions = EnumSet.of(
                Permission.VIEW_DASHBOARD, 
                Permission.MANAGE_PATIENTS,
                Permission.MANAGE_APPOINTMENTS, 
                Permission.LOGOUT);

        @Override
        public boolean hasPermission(Permission permission) {
            return permissions.contains(permission);
        }
    }

    private static class PharmacistPermissionStrategy implements RolePermissionStrategy {
        private final EnumSet<Permission> permissions = EnumSet.of(
                Permission.VIEW_DASHBOARD, 
                Permission.ACCESS_BILLING, 
                Permission.LOGOUT);

        @Override
        public boolean hasPermission(Permission permission) {
            return permissions.contains(permission);
        }
    }

    private static class CoordinatorPermissionStrategy implements RolePermissionStrategy {
        private final EnumSet<Permission> permissions = EnumSet.of(
                Permission.VIEW_DASHBOARD, 
                Permission.MANAGE_APPOINTMENTS,
                Permission.MANAGE_STAFF, 
                Permission.LOGOUT);

        @Override
        public boolean hasPermission(Permission permission) {
            return permissions.contains(permission);
        }
    }
}