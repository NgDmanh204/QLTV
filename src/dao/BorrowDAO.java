package dao;

import util.DBConnection;
import model.Borrow;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    public List<Borrow> getAllBorrows() {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT * FROM borrows ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Borrow b = new Borrow(
                    rs.getInt("id"),
                    rs.getString("borrower_name"),
                    rs.getString("phone_number"),
                    rs.getString("borrow_date"),
                    rs.getString("return_date"),
                    rs.getString("book_code")
                );
                b.setReaderId(rs.getInt("reader_id"));
                b.setFine(rs.getDouble("fine"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean addBorrow(Borrow b) {
        String sql = "INSERT INTO borrows (borrower_name, phone_number, borrow_date, return_date, book_code, reader_id) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBorrowerName());
            ps.setString(2, b.getPhoneNumber());
            ps.setString(3, b.getBorrowDate());
            ps.setString(4, b.getReturnDate());
            ps.setString(5, b.getBookCode());
            ps.setObject(6, b.getReaderId() > 0 ? b.getReaderId() : null);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateBorrow(Borrow b) {
        String sql = "UPDATE borrows SET borrower_name=?, phone_number=?, borrow_date=?, return_date=?, book_code=?, reader_id=?, fine=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBorrowerName());
            ps.setString(2, b.getPhoneNumber());
            ps.setString(3, b.getBorrowDate());
            ps.setString(4, b.getReturnDate());
            ps.setString(5, b.getBookCode());
            ps.setObject(6, b.getReaderId() > 0 ? b.getReaderId() : null);
            ps.setDouble(7, b.getFine());
            ps.setInt(8, b.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteBorrow(int id) {
        String sql = "DELETE FROM borrows WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public double calculateFine(String returnDate, String actualReturnDate) {
        try {
            java.sql.Date ret = java.sql.Date.valueOf(returnDate);
            java.sql.Date actual = java.sql.Date.valueOf(actualReturnDate);
            if (actual.after(ret)) {
                long diff = actual.getTime() - ret.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                return days * 5000;
            }
        } catch (Exception e) {}
        return 0;
    }

    public Borrow getBorrowById(int id) {
        String sql = "SELECT * FROM borrows WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Borrow b = new Borrow(rs.getInt("id"), rs.getString("borrower_name"),
                        rs.getString("phone_number"), rs.getString("borrow_date"),
                        rs.getString("return_date"), rs.getString("book_code"));
                b.setReaderId(rs.getInt("reader_id"));
                b.setFine(rs.getDouble("fine"));
                return b;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}