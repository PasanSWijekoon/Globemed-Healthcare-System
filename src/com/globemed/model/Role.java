package com.globemed.model;

public enum Role {
    Admin, Doctor, Nurse, Pharmacist, Coordinator;

    public static Role fromString(String role) {
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(role)) return r;
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
}