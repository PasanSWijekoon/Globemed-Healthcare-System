package com.globemed.model;

public class Staff {
    private String staffId;
    private String fName;
    private String lName;
    private String role;
    private String contactNumber;
    private String email;

    public Staff(String staffId, String fName, String lName, String role, String contactNumber, String email) {
        this.staffId = staffId;
        this.fName = fName;
        this.lName = lName;
        this.role = role;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters and Setters
    public String getStaffId() { return staffId; }
    public String getFName() { return fName; }
    public String getLName() { return lName; }
    public String getRole() { return role; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
}