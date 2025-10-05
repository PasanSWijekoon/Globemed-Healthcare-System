package com.globemed.service.decorator;

import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.User;
import com.globemed.model.Visit;
import com.globemed.service.IPatientDataService;
import com.globemed.service.SecurityService;
import java.sql.SQLException;
import java.util.List;


public class LoggingDecorator extends PatientDataServiceDecorator {

    private final SecurityService securityService;
    private final User currentUser;


    public LoggingDecorator(IPatientDataService decoratedService, User currentUser) {
        super(decoratedService);
        this.securityService = new SecurityService();
        this.currentUser = currentUser;
    }

    @Override
    public List<PatientRecord> getAllPatientRecords() throws SQLException {
        String action = "Attempt to retrieve all patient records.";
        securityService.logAction(currentUser.getUsername(), action);
        return super.getAllPatientRecords();
    }

    @Override
    public List<PatientRecord> searchPatients(String searchTerm) throws SQLException, SecurityException {
        String action = "Search attempt for patients with term: '" + searchTerm + "'";
        securityService.logAction(currentUser.getUsername(), action);
        return super.searchPatients(searchTerm);
    }

    @Override
    public PatientRecord getPatientRecord(String patientId) throws SQLException, SecurityException {
        String action = "Access attempt to patient record: " + patientId;
        securityService.logAction(currentUser.getUsername(), action);
        return super.getPatientRecord(patientId);
    }
    
    @Override
    public String getLastPatientId() throws SQLException, SecurityException {
        String action = "Attempt to retrieve the last patient ID.";
        securityService.logAction(currentUser.getUsername(), action);
        return super.getLastPatientId();
    }

    @Override
    public void addPatient(PatientRecord patient) throws SQLException, SecurityException {
        String action = "Attempt to add new patient with ID: " + patient.getPatientId();
        securityService.logAction(currentUser.getUsername(), action);
        super.addPatient(patient);
    }

    @Override
    public void updatePatientRecord(String patientId, PatientRecord data) throws SQLException, SecurityException {
        String action = "Update attempt on patient record: " + patientId;
        securityService.logAction(currentUser.getUsername(), action);
        super.updatePatientRecord(patientId, data);
    }

    @Override
    public void deletePatientRecord(String patientId) throws SQLException, SecurityException {
        String action = "Deletion attempt on patient record: " + patientId;
        securityService.logAction(currentUser.getUsername(), action);
        super.deletePatientRecord(patientId);
    }

    @Override
    public void addMedicalHistory(String patientId, MedicalHistoryEntry entry) throws SQLException, SecurityException {
        String action = "Attempt to add medical history for patient: " + patientId;
        securityService.logAction(currentUser.getUsername(), action);
        super.addMedicalHistory(patientId, entry);
    }

    @Override
    public void addTreatmentPlan(String patientId, TreatmentPlanEntry plan) throws SQLException, SecurityException {
        String action = "Attempt to add treatment plan for patient: " + patientId;
        securityService.logAction(currentUser.getUsername(), action);
        super.addTreatmentPlan(patientId, plan);
    }

    @Override
    public void addVisit(String patientId, Visit visit) throws SQLException, SecurityException {
        String action = "Attempt to add visit record for patient: " + patientId;
        securityService.logAction(currentUser.getUsername(), action);
        super.addVisit(patientId, visit);
    }
}
