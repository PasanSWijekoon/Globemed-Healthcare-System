package com.globemed.auth;

import com.globemed.model.Role;
import com.globemed.model.User;
import com.globemed.util.DBUtil;
import com.globemed.auth.permissions.PermissionFactory;
import java.sql.*;

public class AuthService {
    public User login(String username, String password) {
        String sql = "SELECT u.id, u.username, u.staff_id, s.role ,s.f_name,s.l_name,s.email " +
                     "FROM users u JOIN staff s ON u.staff_id = s.staff_id " +
                     "WHERE u.username=? AND u.password=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        Role.fromString(rs.getString("role")),
                        rs.getString("staff_id"),
                        rs.getString("f_name")+" "+rs.getString("l_name")
                );
                user.setPermissionStrategy(PermissionFactory.getStrategy(user.getRole()));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}