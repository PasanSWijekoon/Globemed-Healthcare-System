package com.globemed.service;

import com.globemed.model.Role;
import com.globemed.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;

public class RoleBasedDashboardService extends DashboardService {
    
    private Role currentUserRole;
    private String currentUserId;
    
    public RoleBasedDashboardService(Role userRole, String userId) {
        this.currentUserRole = userRole;
        this.currentUserId = userId;
    }
    
    // Role-based access control for dashboard components
    public Set<String> getAllowedDashboardComponents() {
        Set<String> allowedComponents = new HashSet<>();
        
        switch (currentUserRole) {
            case Admin:
                // Admin sees everything
                allowedComponents.add("TOTAL_PATIENTS");
                allowedComponents.add("TOTAL_STAFF");
                allowedComponents.add("TODAYS_APPOINTMENTS");
                allowedComponents.add("PENDING_INVOICES");
                allowedComponents.add("TOTAL_REVENUE");
                allowedComponents.add("MONTHLY_REVENUE");
                allowedComponents.add("NEW_PATIENTS");
                allowedComponents.add("ACTIVE_PATIENTS");
                allowedComponents.add("APPOINTMENT_STATUS_CHART");
                allowedComponents.add("GENDER_DISTRIBUTION_CHART");
                allowedComponents.add("STAFF_ROLES_CHART");
                allowedComponents.add("INVOICE_STATUS_CHART");
                allowedComponents.add("TOP_DIAGNOSES_CHART");
                allowedComponents.add("INSURANCE_CLAIMS_CHART");
                break;
                
            case Doctor:
                // Doctor focuses on patient care and medical data
                allowedComponents.add("TOTAL_PATIENTS");
                allowedComponents.add("TODAYS_APPOINTMENTS");
                allowedComponents.add("ACTIVE_PATIENTS");
                allowedComponents.add("NEW_PATIENTS");
                allowedComponents.add("APPOINTMENT_STATUS_CHART");
                allowedComponents.add("GENDER_DISTRIBUTION_CHART");
                allowedComponents.add("TOP_DIAGNOSES_CHART");
                break;
                
            case Nurse:
                // Nurse focuses on daily operations and patient care
                allowedComponents.add("TODAYS_APPOINTMENTS");
                allowedComponents.add("ACTIVE_PATIENTS");
                allowedComponents.add("APPOINTMENT_STATUS_CHART");
                allowedComponents.add("GENDER_DISTRIBUTION_CHART");
                break;
                
            case Pharmacist:
                // Pharmacist focuses on prescriptions and diagnoses
                allowedComponents.add("ACTIVE_PATIENTS");
                allowedComponents.add("TOP_DIAGNOSES_CHART");
                allowedComponents.add("GENDER_DISTRIBUTION_CHART");
                break;
                
            case Coordinator:
                // Coordinator focuses on scheduling and basic operations
                allowedComponents.add("TODAYS_APPOINTMENTS");
                allowedComponents.add("APPOINTMENT_STATUS_CHART");
                allowedComponents.add("TOTAL_PATIENTS");
                break;
        }
        
        return allowedComponents;
    }
    
    // Role-specific data access methods
    @Override
    public int getTotalPatients() {
        if (!getAllowedDashboardComponents().contains("TOTAL_PATIENTS")) {
            return 0; // Return 0 or throw exception based on your preference
        }
        return super.getTotalPatients();
    }
    
    @Override
    public int getTotalStaff() {
        if (!getAllowedDashboardComponents().contains("TOTAL_STAFF")) {
            return 0;
        }
        return super.getTotalStaff();
    }
    
    public int getMyAppointments() {
        if (currentUserRole != Role.Doctor) {
            return getTodaysAppointments(); // Return all appointments for non-doctors
        }
        
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM appointments a " +
                    "JOIN staff s ON a.staff_id = s.staff_id " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.id = ? AND DATE(a.appointment_date) = CURDATE()";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentUserId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    public int getMyPatients() {
        if (currentUserRole != Role.Doctor) {
            return getActivePatients();
        }
        
        int total = 0;
        String sql = "SELECT COUNT(DISTINCT a.patient_id) AS total FROM appointments a " +
                    "JOIN staff s ON a.staff_id = s.staff_id " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.id = ? AND a.appointment_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentUserId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    // Role-based financial data access
    @Override
    public double getTotalRevenue() {
        if (!getAllowedDashboardComponents().contains("TOTAL_REVENUE")) {
            return 0.0;
        }
        return super.getTotalRevenue();
    }
    
    @Override
    public double getMonthlyRevenue() {
        if (!getAllowedDashboardComponents().contains("MONTHLY_REVENUE")) {
            return 0.0;
        }
        return super.getMonthlyRevenue();
    }
    
    @Override
    public int getPendingInvoices() {
        if (!getAllowedDashboardComponents().contains("PENDING_INVOICES")) {
            return 0;
        }
        return super.getPendingInvoices();
    }
    
    // Role-based chart data
    public DefaultPieDataset<String> getMyAppointmentStatusData() {
        if (currentUserRole != Role.Doctor) {
            return getAppointmentStatusData();
        }
        
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT a.status, COUNT(*) AS count FROM appointments a " +
                    "JOIN staff s ON a.staff_id = s.staff_id " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.id = ? GROUP BY a.status";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentUserId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                dataset.addValue(status, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }
    
    public DefaultPieDataset<String> getMyTopDiagnosesData() {
        if (currentUserRole != Role.Doctor && currentUserRole != Role.Pharmacist) {
            return getTopDiagnosesData();
        }
        
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT mh.diagnosis, COUNT(*) AS count FROM medical_history mh " +
                    "JOIN visits v ON mh.patient_id = v.patient_id " +
                    "JOIN appointments a ON v.patient_id = a.patient_id " +
                    "JOIN staff s ON a.staff_id = s.staff_id " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.id = ? " +
                    "GROUP BY mh.diagnosis ORDER BY count DESC LIMIT 10";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentUserId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String diagnosis = rs.getString("diagnosis");
                int count = rs.getInt("count");
                dataset.addValue(diagnosis, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }
    
    // Additional methods for role-specific metrics
    public int getTodaysPendingAppointments() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM appointments " +
                    "WHERE DATE(appointment_date) = CURDATE() AND status = 'Scheduled'";
        
        if (currentUserRole == Role.Doctor) {
            sql = "SELECT COUNT(*) AS total FROM appointments a " +
                 "JOIN staff s ON a.staff_id = s.staff_id " +
                 "JOIN users u ON s.staff_id = u.staff_id " +
                 "WHERE u.id = ? AND DATE(a.appointment_date) = CURDATE() AND a.status = 'Scheduled'";
        }
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            if (currentUserRole == Role.Doctor) {
                p.setString(1, currentUserId);
            }
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    public int getMyCompletedAppointmentsToday() {
        if (currentUserRole != Role.Doctor && currentUserRole != Role.Nurse) {
            return 0;
        }
        
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM appointments a " +
                    "JOIN staff s ON a.staff_id = s.staff_id " +
                    "JOIN users u ON s.staff_id = u.staff_id " +
                    "WHERE u.id = ? AND DATE(a.appointment_date) = CURDATE() AND a.status = 'Completed'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentUserId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    // Getters for current user context
    public Role getCurrentUserRole() {
        return currentUserRole;
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    public void setCurrentUser(Role role, String userId) {
        this.currentUserRole = role;
        this.currentUserId = userId;
    }
}