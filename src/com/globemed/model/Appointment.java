package com.globemed.model;

import java.util.Date;

public class Appointment {

    private int appointmentId;
    private String patientId;
    private String staffId;
    private Date appointmentDate;
    private String status;
    private int facilityId;
    private int appointmentTypeId;

    public Appointment(int appointmentId, String patientId, String staffId, Date appointmentDate, String status, int facilityId, int appointmentTypeId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.staffId = staffId;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.facilityId = facilityId;
        this.appointmentTypeId = appointmentTypeId;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public int getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(int appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
