package com.globemed.service;

import com.globemed.model.Role;
import com.globemed.model.Staff;
import com.globemed.model.User;
import com.globemed.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDataService {

    public Staff findStaffMember(String searchTerm) throws SQLException {
        Staff staff = null;
        String sql = "SELECT * FROM staff WHERE staff_id = ? OR f_name LIKE ? OR email = ?";
        String namePattern = "%" + searchTerm + "%";

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, searchTerm);
            ps.setString(2, namePattern);
            ps.setString(3, searchTerm);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                staff = new Staff(
                        rs.getString("staff_id"),
                        rs.getString("f_name"),
                        rs.getString("l_name"),
                        rs.getString("role"),
                        rs.getString("contact_number"),
                        rs.getString("email")
                );
            }
        }
        return staff;
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getString("staff_id"), rs.getString("f_name"),
                        rs.getString("l_name"), rs.getString("role"),
                        rs.getString("contact_number"), rs.getString("email")));
            }
        }
        return staffList;
    }

    public List<Staff> searchStaff(String searchTerm) throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE staff_id LIKE ? OR f_name LIKE ? OR l_name LIKE ?";
        String searchPattern = "%" + searchTerm + "%";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getString("staff_id"), rs.getString("f_name"),
                        rs.getString("l_name"), rs.getString("role"),
                        rs.getString("contact_number"), rs.getString("email")));
            }
        }
        return staffList;
    }

    public void addStaffAndUser(Staff staff, String username, String password) throws SQLException {
        Connection conn = DBUtil.getConnection();
        conn.setAutoCommit(false);

        try {

            String staffSql = "INSERT INTO staff (staff_id, f_name, l_name, role, contact_number, email) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psStaff = conn.prepareStatement(staffSql)) {
                psStaff.setString(1, staff.getStaffId());
                psStaff.setString(2, staff.getFName());
                psStaff.setString(3, staff.getLName());
                psStaff.setString(4, staff.getRole());
                psStaff.setString(5, staff.getContactNumber());
                psStaff.setString(6, staff.getEmail());
                psStaff.executeUpdate();
            }

            String userSql = "INSERT INTO users (username, password, staff_id) VALUES (?, ?, ?)";
            try (PreparedStatement psUser = conn.prepareStatement(userSql)) {
                psUser.setString(1, username);
                psUser.setString(2, password);
                psUser.setString(3, staff.getStaffId());
                psUser.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            if (conn != null) {
                conn.close();
            }
        }
    }

    public String getLastStaffId() throws SQLException {
        String lastId = null;
        String sql = "SELECT staff_id FROM staff ORDER BY CAST(SUBSTRING(staff_id, 3) AS UNSIGNED) DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lastId = rs.getString("staff_id");
            }
        }
        return lastId;
    }

    public User getUserByStaffId(String staffId) throws SQLException {
        String sql = "SELECT u.id, u.username, s.role,s.email,s.f_name,s.l_name, u.staff_id FROM users u "
                + "JOIN staff s ON u.staff_id = s.staff_id WHERE u.staff_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staffId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        Role.fromString(rs.getString("role")),
                        rs.getString("staff_id"),
                        rs.getString("f_name")+" "+rs.getString("l_name")
                );
            }
        }
        return null;
    }

}
