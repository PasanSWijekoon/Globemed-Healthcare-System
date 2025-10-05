package com.globemed.service.decorator;

import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.Visit;
import com.globemed.service.IPatientDataService;
import java.sql.SQLException;
import java.util.List; 

public abstract class PatientDataServiceDecorator implements IPatientDataService {

    protected IPatientDataService decoratedService;

    public PatientDataServiceDecorator(IPatientDataService decoratedService) {
        this.decoratedService = decoratedService;
    }

    @Override
    public List<PatientRecord> getAllPatientRecords() throws SQLException {
        return decoratedService.getAllPatientRecords();
    }

    @Override
    public PatientRecord getPatientRecord(String patientId) throws SQLException {
        return decoratedService.getPatientRecord(patientId);
    }

    @Override
    public void updatePatientRecord(String patientId, PatientRecord data) throws SQLException {
        decoratedService.updatePatientRecord(patientId, data);
    }

    @Override
    public void deletePatientRecord(String patientId) throws SQLException {
        decoratedService.deletePatientRecord(patientId);
    }

    @Override
    public List<PatientRecord> searchPatients(String searchTerm) throws SQLException {
        return decoratedService.searchPatients(searchTerm);
    }

    @Override
    public void addPatient(PatientRecord patient) throws SQLException {
        decoratedService.addPatient(patient);
    }

    @Override
    public String getLastPatientId() throws SQLException {
        return decoratedService.getLastPatientId();
    }

    @Override
    public void addMedicalHistory(String patientId, MedicalHistoryEntry entry) throws SQLException {
        decoratedService.addMedicalHistory(patientId, entry);
    }

    @Override
    public void addTreatmentPlan(String patientId, TreatmentPlanEntry plan) throws SQLException {
        decoratedService.addTreatmentPlan(patientId, plan);
    }

    @Override
    public void addVisit(String patientId, Visit visit) throws SQLException {
        decoratedService.addVisit(patientId, visit);
    }
}
