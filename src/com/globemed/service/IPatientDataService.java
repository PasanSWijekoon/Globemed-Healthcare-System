package com.globemed.service;

import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.Visit;
import java.sql.SQLException;
import java.util.List;

public interface IPatientDataService {
    List<PatientRecord> searchPatients(String searchTerm) throws SQLException, SecurityException;
    List<PatientRecord> getAllPatientRecords() throws SQLException, SecurityException;
    PatientRecord getPatientRecord(String patientId) throws SQLException, SecurityException;
    void updatePatientRecord(String patientId, PatientRecord data) throws SQLException, SecurityException;
    void deletePatientRecord(String patientId) throws SQLException, SecurityException;
    void addPatient(PatientRecord patient) throws SQLException, SecurityException;
    String getLastPatientId() throws SQLException, SecurityException;
    void addMedicalHistory(String patientId, MedicalHistoryEntry entry) throws SQLException, SecurityException;
    void addTreatmentPlan(String patientId, TreatmentPlanEntry plan) throws SQLException, SecurityException;
    void addVisit(String patientId, Visit visit) throws SQLException, SecurityException;
}