package dao;

import util.DBConnection;
import model.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAO {
    public List<Reader> getAllReaders() {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Reader(
                    rs.getInt("id"),
                    rs.getString("reader_code"),
                    rs.getString("fullname"),
                    rs.getString("gender"),
                    rs.getString("birthday"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean addReader(Reader r) {
        String sql = "INSERT INTO readers (reader_code, fullname, gender, birthday, phone, email, address) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getReaderCode());
            ps.setString(2, r.getFullname());
            ps.setString(3, r.getGender());
            ps.setString(4, r.getBirthday());
            ps.setString(5, r.getPhone());
            ps.setString(6, r.getEmail());
            ps.setString(7, r.getAddress());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateReader(Reader r) {
        String sql = "UPDATE readers SET reader_code=?, fullname=?, gender=?, birthday=?, phone=?, email=?, address=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getReaderCode());
            ps.setString(2, r.getFullname());
            ps.setString(3, r.getGender());
            ps.setString(4, r.getBirthday());
            ps.setString(5, r.getPhone());
            ps.setString(6, r.getEmail());
            ps.setString(7, r.getAddress());
            ps.setInt(8, r.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteReader(int id) {
        String sql = "DELETE FROM readers WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Reader getReaderById(int id) {
        String sql = "SELECT * FROM readers WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Reader(
                    rs.getInt("id"),
                    rs.getString("reader_code"),
                    rs.getString("fullname"),
                    rs.getString("gender"),
                    rs.getString("birthday"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<Reader> searchReader(String keyword) {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers WHERE reader_code LIKE ? OR fullname LIKE ? OR phone LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Reader(
                    rs.getInt("id"),
                    rs.getString("reader_code"),
                    rs.getString("fullname"),
                    rs.getString("gender"),
                    rs.getString("birthday"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}