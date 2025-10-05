package com.globemed.model;

import java.util.Date;

public class MedicalHistoryEntry {
    private String diagnosis;
    private Date dateOfEntry;

    public MedicalHistoryEntry(String diagnosis, Date dateOfEntry) {
        this.diagnosis = diagnosis;
        this.dateOfEntry = dateOfEntry;
    }

    public String getDiagnosis() { return diagnosis; }
    public Date getDateOfEntry() { return dateOfEntry; }
}