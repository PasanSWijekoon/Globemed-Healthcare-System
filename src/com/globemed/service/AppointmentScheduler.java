package com.globemed.service;

import com.globemed.model.Appointment;
import com.globemed.model.AppointmentType;
import com.globemed.model.Facility;
import com.globemed.model.Role;
import com.globemed.model.User;
import com.globemed.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

public class AppointmentScheduler implements IAppointmentService{

    public void bookAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments (patient_id, staff_id, appointment_date, status, facility_id, appointment_type_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointment.getPatientId());
            ps.setString(2, appointment.getStaffId());
            ps.setTimestamp(3, new java.sql.Timestamp(appointment.getAppointmentDate().getTime()));
            ps.setString(4, appointment.getStatus());
            ps.setInt(5, appointment.getFacilityId());
            ps.setInt(6, appointment.getAppointmentTypeId());
            ps.executeUpdate();
        }
    }

    public void updateAppointmentStatus(int appointmentId, String newStatus) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
        }
    }

    public void cancelAppointment(int appointmentId) throws SQLException {
        updateAppointmentStatus(appointmentId, "Cancelled");
    }

    public List<Appointment> getAppointments(User user) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        if (user.getRole() != Role.Admin) {
            sql += " WHERE staff_id = ?";
        }

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (user.getRole() != Role.Admin) {
                ps.setString(1, user.getStaffId());
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("patient_id"),
                        rs.getString("staff_id"),
                        rs.getTimestamp("appointment_date"),
                        rs.getString("status"),
                        rs.getInt("facility_id"),
                        rs.getInt("appointment_type_id")
                ));
            }
        }
        return appointments;
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("patient_id"),
                        rs.getString("staff_id"),
                        rs.getTimestamp("appointment_date"),
                        rs.getString("status"),
                        rs.getInt("facility_id"),
                        rs.getInt("appointment_type_id")
                );
            }
        }
        return null;
    }

    public boolean isTimeSlotAvailable(String staffId, Timestamp newAppointmentStart, int newAppointmentDuration) throws SQLException {

      
        Calendar cal = Calendar.getInstance();
        cal.setTime(newAppointmentStart);
        cal.add(Calendar.MINUTE, newAppointmentDuration);
        Timestamp newAppointmentEnd = new Timestamp(cal.getTimeInMillis());

        String sql = "SELECT a.appointment_date, at.duration_minutes "
                + "FROM appointments a "
                + "JOIN appointment_types at ON a.appointment_type_id = at.type_id "
                + "WHERE a.staff_id = ? AND DATE(a.appointment_date) = ? AND a.status = 'Scheduled'";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staffId);
            // Keep full datetime in query
            ps.setDate(2, new java.sql.Date(newAppointmentStart.getTime()));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp existingStart = rs.getTimestamp("appointment_date");
                int existingDuration = rs.getInt("duration_minutes");

                cal.setTime(existingStart);
                cal.add(Calendar.MINUTE, existingDuration);
                Timestamp existingEnd = new Timestamp(cal.getTimeInMillis());

                // Check if time intervals overlap
                if (newAppointmentStart.before(existingEnd) && newAppointmentEnd.after(existingStart)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Appointment> getAppointmentsForDate(java.util.Date date) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE DATE(appointment_date) = ?";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new Date(date.getTime()));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("patient_id"),
                        rs.getString("staff_id"),
                        rs.getTimestamp("appointment_date"),
                        rs.getString("status"),
                        rs.getInt("facility_id"),
                        rs.getInt("appointment_type_id")
                ));
            }
        }
        return appointments;
    }

    public List<Facility> getAllFacilities() {
        List<Facility> facilities = new ArrayList<>();
        String sql = "SELECT facility_id, facility_name FROM facilities ORDER BY facility_name";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                facilities.add(new Facility(rs.getInt("facility_id"), rs.getString("facility_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facilities;
    }

    public List<AppointmentType> getAllAppointmentTypes() {
        List<AppointmentType> types = new ArrayList<>();
        String sql = "SELECT type_id, type_name, duration_minutes FROM appointment_types ORDER BY type_name";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("type_id");
                String name = rs.getString("type_name");
                int duration = rs.getInt("duration_minutes");
                types.add(new AppointmentType(id, name, duration));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    @Override
    public boolean isTimeSlotAvailable(String staffId, java.util.Date appointmentDate) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
