package com.globemed.service;

import com.globemed.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class SecurityService {


    public void logAction(String username, String action) {
        new Thread(() -> {
            String sql = "INSERT INTO audit_log (username, action_description) VALUES (?, ?)";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, action);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public List<String> getLogs(Date startDate, Date endDate, String username) throws SQLException {
        List<String> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT log_timestamp, username, action_description FROM audit_log WHERE 1=1");
        
        if (startDate != null) {
            sql.append(" AND log_timestamp >= ?");
        }
        if (endDate != null) {
            sql.append(" AND log_timestamp <= ?");
        }
        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND username LIKE ?");
        }
        sql.append(" ORDER BY log_timestamp DESC LIMIT 500");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (startDate != null) {
                ps.setTimestamp(paramIndex++, new java.sql.Timestamp(startDate.getTime()));
            }
            if (endDate != null) {
                ps.setTimestamp(paramIndex++, new java.sql.Timestamp(endDate.getTime()));
            }
            if (username != null && !username.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + username + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String logEntry = String.format("[%s] - USER: %s - ACTION: %s",
                        rs.getTimestamp("log_timestamp").toString(),
                        rs.getString("username"),
                        rs.getString("action_description"));
                logs.add(logEntry);
            }
        }
        return logs;
    }
}