package com.globemed.service;

import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.Visit;
import com.globemed.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class PatientDataService implements IPatientDataService {

    @Override
    public PatientRecord getPatientRecord(String patientId) throws SQLException {
        PatientRecord patientRecord = null;
        try (Connection conn = DBUtil.getConnection()) {
            // Fetch patient details
            String sqlPatient = "SELECT f_name, l_name, birthday, registered_date, gender, contact_number, address FROM patients WHERE patient_id = ?";
            try (PreparedStatement psPatient = conn.prepareStatement(sqlPatient)) {
                psPatient.setString(1, patientId);
                ResultSet rsPatient = psPatient.executeQuery();

                if (rsPatient.next()) {
                    // Fetch medical history entries
                    List<MedicalHistoryEntry> medicalHistory = new ArrayList<>();
                    String sqlHistory = "SELECT diagnosis, date_of_entry FROM medical_history WHERE patient_id = ?";
                    try (PreparedStatement psHistory = conn.prepareStatement(sqlHistory)) {
                        psHistory.setString(1, patientId);
                        ResultSet rsHistory = psHistory.executeQuery();
                        while (rsHistory.next()) {
                            medicalHistory.add(new MedicalHistoryEntry(rsHistory.getString("diagnosis"), rsHistory.getDate("date_of_entry")));
                        }
                    }

                    // Fetch treatment plan entries
                    List<TreatmentPlanEntry> treatmentPlans = new ArrayList<>();
                    String sqlTreatment = "SELECT plan_details, start_date, end_date FROM treatment_plans WHERE patient_id = ?";
                    try (PreparedStatement psTreatment = conn.prepareStatement(sqlTreatment)) {
                        psTreatment.setString(1, patientId);
                        ResultSet rsTreatment = psTreatment.executeQuery();
                        while (rsTreatment.next()) {
                            treatmentPlans.add(new TreatmentPlanEntry(rsTreatment.getString("plan_details"), rsTreatment.getDate("start_date"), rsTreatment.getDate("end_date")));
                        }
                    }

                    // Fetch visit history
                    List<Visit> visits = new ArrayList<>();
                    String sqlVisits = "SELECT visit_date, notes FROM visits WHERE patient_id = ?";
                    try (PreparedStatement psVisits = conn.prepareStatement(sqlVisits)) {
                        psVisits.setString(1, patientId);
                        ResultSet rsVisits = psVisits.executeQuery();
                        while (rsVisits.next()) {
                            visits.add(new Visit(rsVisits.getTimestamp("visit_date"), rsVisits.getString("notes")));
                        }
                    }

                    patientRecord = new PatientRecord(
                            patientId,
                            rsPatient.getString("f_name"),
                            rsPatient.getString("l_name"),
                            rsPatient.getDate("birthday"),
                            rsPatient.getDate("registered_date"),
                            rsPatient.getString("gender"),
                            rsPatient.getString("contact_number"),
                            rsPatient.getString("address"),
                            medicalHistory,
                            treatmentPlans,
                            visits
                    );
                }
            }
        }
        return patientRecord;
    }

    @Override
    public void updatePatientRecord(String patientId, PatientRecord data) throws SQLException {
        // Implement complex update logic involving multiple tables
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void deletePatientRecord(String patientId) throws SQLException {
        // Implement delete logic involving multiple tables
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<PatientRecord> getAllPatientRecords() throws SQLException, SecurityException {

        List<PatientRecord> patientRecords = new ArrayList<>();
        String sql = "SELECT * FROM patients"; // SQL query to get all patients

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // For each row in the result, create a PatientRecord object
                PatientRecord patientRecord = new PatientRecord(
                        rs.getString("patient_id"),
                        rs.getString("f_name"),
                        rs.getString("l_name"),
                        rs.getDate("birthday"),
                        rs.getDate("registered_date"),
                        rs.getString("gender"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        null, // Medical history, treatment plans, and visits can be loaded later
                        null,
                        null
                );
                patientRecords.add(patientRecord);
            }
        }
        return patientRecords;

    }

    @Override
    public List<PatientRecord> searchPatients(String searchTerm) throws SQLException, SecurityException {
        List<PatientRecord> patientRecords = new ArrayList<>();
        // SQL query to search across multiple fields
        String sql = "SELECT * FROM patients WHERE patient_id LIKE ? OR f_name LIKE ? OR l_name LIKE ? OR contact_number LIKE ?";
        String searchPattern = "%" + searchTerm + "%"; // Add wildcards for LIKE search

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PatientRecord patientRecord = new PatientRecord(
                        rs.getString("patient_id"),
                        rs.getString("f_name"),
                        rs.getString("l_name"),
                        rs.getDate("birthday"),
                        rs.getDate("registered_date"),
                        rs.getString("gender"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        null, null, null // Details loaded on demand
                );
                patientRecords.add(patientRecord);
            }
        }
        return patientRecords;
    }

    @Override
    public void addPatient(PatientRecord patient) throws SQLException, SecurityException {
        String sql = "INSERT INTO patients (patient_id, f_name, l_name, birthday, registered_date, gender, contact_number, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, patient.getPatientId());
            ps.setString(2, patient.getFName());
            ps.setString(3, patient.getLName());
            ps.setDate(4, new Date(patient.getBirthday().getTime()));
            ps.setDate(5, new Date(patient.getRegisteredDate().getTime()));
            ps.setString(6, patient.getGender());
            ps.setString(7, patient.getContactNumber());
            ps.setString(8, patient.getAddress());

            ps.executeUpdate();
        }

    }

    @Override
    public String getLastPatientId() throws SQLException, SecurityException {
        String lastId = null;
        // This query extracts the number from the ID, casts it to an integer,
        // orders by that number in descending order, and gets the top one.
        String sql = "SELECT patient_id FROM patients ORDER BY CAST(SUBSTRING(patient_id, 3) AS UNSIGNED) DESC LIMIT 1";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                lastId = rs.getString("patient_id");
            }
        }
        return lastId; // This will be null if the table is empty
    }

    @Override
    public void addMedicalHistory(String patientId, MedicalHistoryEntry entry) throws SQLException, SecurityException {
        String sql = "INSERT INTO medical_history (patient_id, diagnosis, date_of_entry) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, entry.getDiagnosis());
            ps.setDate(3, new Date(entry.getDateOfEntry().getTime()));
            ps.executeUpdate();
        }
    }

    @Override
    public void addTreatmentPlan(String patientId, TreatmentPlanEntry plan) throws SQLException, SecurityException {
        String sql = "INSERT INTO treatment_plans (patient_id, plan_details, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, plan.getPlanDetails());
            ps.setDate(3, new Date(plan.getStartDate().getTime()));
            ps.setDate(4, new Date(plan.getEndDate().getTime()));
            ps.executeUpdate();
        }
    }

    @Override
    public void addVisit(String patientId, Visit visit) throws SQLException, SecurityException {
        String sql = "INSERT INTO visits (patient_id, visit_date, notes) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setTimestamp(2, new java.sql.Timestamp(visit.getVisitDate().getTime()));
            ps.setString(3, visit.getNotes());
            ps.executeUpdate();
        }
    }

}
