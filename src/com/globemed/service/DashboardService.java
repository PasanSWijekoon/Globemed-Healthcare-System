package com.globemed.service;

import com.globemed.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;

public class DashboardService {

    // Core Statistics Methods
    public int getTotalPatients() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM patients";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getTotalStaff() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM staff";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getTodaysAppointments() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM appointments WHERE DATE(appointment_date) = CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getPendingInvoices() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM invoices WHERE status = 'Pending'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public double getTotalRevenue() {
        double total = 0.0;
        String sql = "SELECT SUM(amount) AS total FROM invoices WHERE status = 'Paid'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public double getMonthlyRevenue() {
        double total = 0.0;
        String sql = "SELECT SUM(amount) AS total FROM invoices " +
                    "WHERE status = 'Paid' AND MONTH(service_date) = MONTH(CURDATE()) " +
                    "AND YEAR(service_date) = YEAR(CURDATE())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // Chart Data Methods
    public DefaultPieDataset<String> getAppointmentStatusData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT status, COUNT(*) AS count FROM appointments GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
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

    public DefaultPieDataset<String> getPatientGenderDistribution() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT gender, COUNT(*) AS count FROM patients WHERE gender IS NOT NULL GROUP BY gender";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String gender = rs.getString("gender");
                int count = rs.getInt("count");
                dataset.addValue(gender, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public DefaultPieDataset<String> getStaffRoleDistribution() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT role, COUNT(*) AS count FROM staff GROUP BY role";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String role = rs.getString("role");
                int count = rs.getInt("count");
                dataset.addValue(role, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public DefaultPieDataset<String> getInvoiceStatusData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT status, COUNT(*) AS count FROM invoices GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
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

    public DefaultCategoryDataset<String, String> getMonthlyRevenueData() {
        DefaultCategoryDataset<String, String> dataset = new DefaultCategoryDataset<>();
        String sql = "SELECT DATE_FORMAT(service_date, '%Y-%m') AS month, SUM(amount) AS total " +
                    "FROM invoices WHERE status = 'Paid' " +
                    "GROUP BY DATE_FORMAT(service_date, '%Y-%m') " +
                    "ORDER BY month DESC LIMIT 12";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                dataset.addValue(total, "Revenue", month);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public DefaultCategoryDataset<String, String> getDailyAppointmentsData() {
        DefaultCategoryDataset<String, String> dataset = new DefaultCategoryDataset<>();
        String sql = "SELECT DATE(appointment_date) AS date, COUNT(*) AS count " +
                    "FROM appointments " +
                    "WHERE appointment_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                    "GROUP BY DATE(appointment_date) " +
                    "ORDER BY date";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                int count = rs.getInt("count");
                dataset.addValue(count, "Appointments", date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public DefaultPieDataset<String> getMonthlyAppointmentsDataAsPieDataset() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT DATE_FORMAT(appointment_date, '%M %Y') AS month, COUNT(*) AS count " +
                    "FROM appointments " +
                    "WHERE appointment_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                    "GROUP BY DATE_FORMAT(appointment_date, '%Y-%m'), DATE_FORMAT(appointment_date, '%M %Y') " +
                    "ORDER BY DATE_FORMAT(appointment_date, '%Y-%m')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                int count = rs.getInt("count");
                dataset.addValue(month, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public DefaultPieDataset<String> getMonthlyRevenueDataAsPieDataset() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT DATE_FORMAT(service_date, '%M %Y') AS month, SUM(amount) AS total " +
                    "FROM invoices WHERE status = 'Paid' " +
                    "AND service_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                    "GROUP BY DATE_FORMAT(service_date, '%Y-%m'), DATE_FORMAT(service_date, '%M %Y') " +
                    "ORDER BY DATE_FORMAT(service_date, '%Y-%m')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                dataset.addValue(month, (int) total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    // Additional Analytics Methods
    public DefaultPieDataset<String> getTopDiagnosesData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT diagnosis, COUNT(*) AS count FROM medical_history " +
                    "GROUP BY diagnosis ORDER BY count DESC LIMIT 10";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
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

    public DefaultPieDataset<String> getInsuranceClaimStatusData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        String sql = "SELECT claim_status, COUNT(*) AS count FROM insurance_claims GROUP BY claim_status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String status = rs.getString("claim_status");
                int count = rs.getInt("count");
                dataset.addValue(status, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    public int getRecentPatientRegistrations() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM patients " +
                    "WHERE registered_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getActivePatients() {
        int total = 0;
        String sql = "SELECT COUNT(DISTINCT patient_id) AS total FROM visits " +
                    "WHERE visit_date >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}