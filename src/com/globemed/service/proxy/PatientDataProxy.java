package com.globemed.service.proxy;

import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.Role;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.User;
import com.globemed.model.Visit;
import com.globemed.service.IPatientDataService;
import com.globemed.service.PatientDataService;
import com.globemed.service.decorator.EncryptionDecorator;
import com.globemed.service.decorator.LoggingDecorator;
import java.sql.SQLException;
import java.util.List;

public class PatientDataProxy implements IPatientDataService {

    private final IPatientDataService secureService;
    private final User currentUser;

    public PatientDataProxy(User currentUser) {
        this.currentUser = currentUser;
        IPatientDataService realService = new PatientDataService();
        IPatientDataService loggingService = new LoggingDecorator(realService, this.currentUser); 
        this.secureService = new EncryptionDecorator(loggingService);
       
    }

    private boolean isAuthorized() {
        return currentUser.getRole() == Role.Admin || currentUser.getRole() == Role.Doctor;
    }

    @Override
    public List<PatientRecord> getAllPatientRecords() throws SQLException {
        if (isAuthorized()) {
            return secureService.getAllPatientRecords();
        } else {
            throw new SecurityException("Access denied. " + currentUser.getRole() + " is not authorized to view patient records.");
        }
    }

    @Override
    public PatientRecord getPatientRecord(String patientId) throws SQLException {
        if (isAuthorized()) {
            return secureService.getPatientRecord(patientId);
        } else {
            throw new SecurityException("Access denied. " + currentUser.getRole() + " is not authorized to view patient records.");
        }
    }

    @Override
    public void updatePatientRecord(String patientId, PatientRecord data) throws SQLException {
        if (currentUser.getRole() == Role.Admin) {
            secureService.updatePatientRecord(patientId, data);
        } else {
            throw new SecurityException("Access denied. Only Admins can update patient records.");
        }
    }

    @Override
    public void deletePatientRecord(String patientId) throws SQLException {
        if (currentUser.getRole() == Role.Admin) {
            secureService.deletePatientRecord(patientId);
        } else {
            throw new SecurityException("Access denied. Only Admins can delete patient records.");
        }
    }

    @Override
    public List<PatientRecord> searchPatients(String searchTerm) throws SQLException, SecurityException {
        if (isAuthorized()) {
            return secureService.searchPatients(searchTerm);
        } else {
            throw new SecurityException("Access denied. " + currentUser.getRole() + " is not authorized to search patient records.");
        }
    }

    @Override
    public void addPatient(PatientRecord patient) throws SQLException, SecurityException {
        if (currentUser.getRole() == Role.Admin) {
            secureService.addPatient(patient);
        } else {
            throw new SecurityException("Access denied. Only Admins can add new patients.");
        }
    }

    @Override
    public String getLastPatientId() throws SQLException {
        if (isAuthorized()) {
            return secureService.getLastPatientId();
        } else {
            throw new SecurityException("Access denied.");
        }
    }

    private boolean canModifyMedicalRecords() {
        return currentUser.getRole() == Role.Admin || currentUser.getRole() == Role.Doctor;
    }

    @Override
    public void addMedicalHistory(String patientId, MedicalHistoryEntry entry) throws SQLException {
        if (!canModifyMedicalRecords()) {
            throw new SecurityException("Access Denied.");
        }
        secureService.addMedicalHistory(patientId, entry);
    }

    @Override
    public void addTreatmentPlan(String patientId, TreatmentPlanEntry plan) throws SQLException {
        if (!canModifyMedicalRecords()) {
            throw new SecurityException("Access Denied.");
        }
        secureService.addTreatmentPlan(patientId, plan);
    }

    @Override
    public void addVisit(String patientId, Visit visit) throws SQLException {
        if (!canModifyMedicalRecords()) {
            throw new SecurityException("Access Denied.");
        }
        secureService.addVisit(patientId, visit);
    }
}
