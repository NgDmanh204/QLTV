package dao;

import util.DBConnection;
import model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                        rs.getString("author"), rs.getString("publisher"), rs.getInt("quantity"), rs.getDouble("price")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean addBook(Book b) {
        String sql = "INSERT INTO books (id, title, category, author, publisher, quantity, price) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getId());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getCategory());
            ps.setString(4, b.getAuthor());
            ps.setString(5, b.getPublisher());
            ps.setInt(6, b.getQuantity());
            ps.setDouble(7, b.getPrice());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateBook(Book b) {
        String sql = "UPDATE books SET title=?, category=?, author=?, publisher=?, quantity=?, price=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getCategory());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getPublisher());
            ps.setInt(5, b.getQuantity());
            ps.setDouble(6, b.getPrice());
            ps.setInt(7, b.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Book> searchBook(String keyword) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE id LIKE ? OR title LIKE ? OR author LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                        rs.getString("author"), rs.getString("publisher"), rs.getInt("quantity"), rs.getDouble("price")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean isBookIdExists(int id) {
        String sql = "SELECT id FROM books WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                        rs.getString("author"), rs.getString("publisher"), rs.getInt("quantity"), rs.getDouble("price"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateQuantity(int bookId, int newQuantity) {
        String sql = "UPDATE books SET quantity=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}