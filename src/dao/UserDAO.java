/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.Database;

public class UserDAO {
    private User readFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("full_name"),
            rs.getInt("role_id")
        );
    }

    public User selectByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        List<User> list = select(sql, username);
        return list.isEmpty() ? null : list.get(0);
    }
    
    public List<User> selectAll() {
        String sql = "SELECT * FROM Users";
        return select(sql);
    }

    // Các hàm insert, update, delete cho người dùng có thể được viết tương tự
    // ...

    private List<User> select(String sql, Object... args) {
        List<User> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu User", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    // Bỏ qua lỗi khi đóng
                }
            }
        }
        return list;
    }
}
