package com.globemed.model;


import com.globemed.auth.permissions.RolePermissionStrategy;

public class User {

    private int id;
    private String username;
    private String email;
    private Role role;
    private String staffId;
    private String name;
    private RolePermissionStrategy permissionStrategy;

   
    public User(int id, String username, String email, Role role, String staffId, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.staffId = staffId;
        this.name = name;
    }

    
    public void setPermissionStrategy(RolePermissionStrategy permissionStrategy) {
        this.permissionStrategy = permissionStrategy;
    }
  
    public boolean hasPermission(com.globemed.auth.permissions.Permission permission) {
        if (permissionStrategy == null) {
            return false;
        }
        return permissionStrategy.hasPermission(permission);
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getStaffId() { return staffId; }
}