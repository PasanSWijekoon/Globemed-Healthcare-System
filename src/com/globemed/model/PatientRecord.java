package com.globemed.model;

import com.globemed.reports.IReportVisitor;
import java.util.Date;
import java.util.List;

public class PatientRecord {
    private String patientId;
    private String fName;
    private String lName;
    private Date birthday;
    private Date registeredDate;
    private String gender;
    private String contactNumber;
    private String address;
    private List<MedicalHistoryEntry> medicalHistory;
    private List<TreatmentPlanEntry> treatmentPlans;
    private List<Visit> visits;

    public PatientRecord(String patientId, String fName, String lName, Date birthday, Date registeredDate,
                         String gender, String contactNumber, String address, List<MedicalHistoryEntry> medicalHistory,
                         List<TreatmentPlanEntry> treatmentPlans, List<Visit> visits) {
        this.patientId = patientId;
        this.fName = fName;
        this.lName = lName;
        this.birthday = birthday;
        this.registeredDate = registeredDate;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.treatmentPlans = treatmentPlans;
        this.visits = visits;
    }

    public String accept(IReportVisitor visitor) {
        return visitor.visit(this);
    }
    
    // Getters for all fields
    public String getPatientId() { return patientId; }
    public String getFName() { return fName; }
    public String getLName() { return lName; }
    public Date getBirthday() { return birthday; }
    public Date getRegisteredDate() { return registeredDate; }
    public String getGender() { return gender; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public List<MedicalHistoryEntry> getMedicalHistory() { return medicalHistory; }
    public List<TreatmentPlanEntry> getTreatmentPlans() { return treatmentPlans; }
    public List<Visit> getVisits() { return visits; }
}