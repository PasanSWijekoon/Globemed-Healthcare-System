package com.globemed.service.decorator;

import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.Visit;
import com.globemed.service.IPatientDataService;
import java.sql.SQLException;
import java.util.List;

public class EncryptionDecorator extends PatientDataServiceDecorator {

    public EncryptionDecorator(IPatientDataService decoratedService) {
        super(decoratedService);
    }

    private String encrypt(String data) {
        return "ENCRYPTED(" + data + ")";
    }

    private String decrypt(String encryptedData) {
        if (encryptedData != null && encryptedData.startsWith("ENCRYPTED(")) {
            return encryptedData.substring(10, encryptedData.length() - 1);
        }
        return encryptedData;
    }

    @Override
    public List<PatientRecord> getAllPatientRecords() throws SQLException {
        List<PatientRecord> records = super.getAllPatientRecords();
        System.out.println("Decrypting details for all retrieved patient records.");
        return records;
    }

    @Override
    public PatientRecord getPatientRecord(String patientId) throws SQLException {
        PatientRecord record = super.getPatientRecord(patientId);
        if (record != null) {
            System.out.println("Decrypting personal details for patient " + patientId);
        }
        return record;
    }

    @Override
    public void updatePatientRecord(String patientId, PatientRecord data) throws SQLException {
        System.out.println("Encrypting data for patient " + patientId + " before storage.");
        super.updatePatientRecord(patientId, data);
    }

    @Override
    public void deletePatientRecord(String patientId) throws SQLException {
        super.deletePatientRecord(patientId);
    }

    @Override
    public List<PatientRecord> searchPatients(String searchTerm) throws SQLException {
        return super.searchPatients(searchTerm);
    }

    @Override
    public void addPatient(PatientRecord patient) throws SQLException {
        System.out.println("Encrypting data for new patient " + patient.getPatientId());
        super.addPatient(patient);
    }

    @Override
    public void addMedicalHistory(String patientId, MedicalHistoryEntry entry) throws SQLException {
        super.addMedicalHistory(patientId, entry);
    }

    @Override
    public void addTreatmentPlan(String patientId, TreatmentPlanEntry plan) throws SQLException {
        super.addTreatmentPlan(patientId, plan);
    }

    @Override
    public void addVisit(String patientId, Visit visit) throws SQLException {
        super.addVisit(patientId, visit);
    }
}
