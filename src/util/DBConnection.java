package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    // Sửa tên database thành QuanLyThuVien
    private static final String URL = "jdbc:mysql://localhost:3306/QuanLyThuVien?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "manh2004"; 

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Lỗi kết nối DB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}